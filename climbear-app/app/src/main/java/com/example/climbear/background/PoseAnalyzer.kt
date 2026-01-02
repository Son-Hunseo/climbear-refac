package com.example.climbear.background // 또는 분석 로직에 적합한 패키지

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.net.Uri
import android.util.Log
import com.example.climbear.util.Box
import com.example.climbear.util.Point
import com.example.climbear.util.PolygonBox
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Optional
import kotlin.math.max

data class FrameData(
    val bitmap: Bitmap,
    val timestampUs: Long,
    val originalWidth: Int,
    val originalHeight: Int
)

data class FrameLog(
    val timeMs: Long,
    val left_hand: Int?,
    val right_hand: Int?,
    val left_foot: Int?,
    val right_foot: Int?
) {
    fun hasSameHolds(other: FrameLog): Boolean {
        return this.left_hand == other.left_hand &&
                this.right_hand == other.right_hand &&
                this.left_foot == other.left_foot &&
                this.right_foot == other.right_foot
    }
}

class PoseAnalyzer(private val context: Context) {

    companion object {
        // 신체 부위 랜드마크
        const val LEFT_HAND = 15
        const val RIGHT_HAND = 16
        const val LEFT_HEEL = 27   // 왼발 뒤꿈치
        const val RIGHT_HEEL = 28  // 오른발 뒤꿈치
        const val LEFT_TOE = 31    // 왼발 끝
        const val RIGHT_TOE = 32   // 오른발 끝

        val LANDMARK_INDICES = listOf(LEFT_HAND, RIGHT_HAND, LEFT_HEEL, RIGHT_HEEL, LEFT_TOE, RIGHT_TOE)

        // 보간을 위한 부모 관절 매핑
        val LIMB_TO_PARENT = mapOf(
            LEFT_HAND to 13,   // 왼손에서 왼쪽 손목
            RIGHT_HAND to 14,  // 오른손에서 오른쪽 손목
            LEFT_HEEL to 25,   // 왼발 뒤꿈치 -> 왼쪽 발목
            RIGHT_HEEL to 26,  // 오른발 뒤꿈치 -> 오른쪽 발목
            LEFT_TOE to 25,    // 왼발 끝 -> 왼쪽 발목
            RIGHT_TOE to 26    // 오른발 끝 -> 오른쪽 발목
        )

        // 기본 설정값
        const val DEFAULT_FPS = 5
        const val DEFAULT_BOX_INSET_PERCENTAGE = -0.39f
        const val DEFAULT_STABILITY_THRESHOLD_SECONDS = 0.13f
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5f
        const val DEFAULT_LANDMARK_VISIBILITY_THRESHOLD = 0.55f
        const val DEFAULT_INTERPOLATION_SCALE = 1.15f
        const val DEFAULT_FRAME_SKIP_FACTOR = 2
        const val DEFAULT_FRAME_SCALE_FACTOR = 0.4f
        const val RELEASE_THRESHOLD = 2 // 연속 비접촉 허용 프레임 수
        const val PIPELINE_CHANNEL_CAPACITY = 5 // 채널 버퍼 크기
        const val TAG = "PoseAnalyzer" // 로그 태그
    }

    // 분석 상태를 저장하는 변수들
    private val contactFrames = mutableMapOf<Int, MutableMap<Int, Int>>()
    private val lastTouchedBox = mutableMapOf<Int, Int?>()
    private val stableInNewBox = mutableMapOf<Int, Int?>()
    private val logs = mutableListOf<FrameLog>()
    private var prevLandmarks: List<NormalizedLandmark>? = null
    private val releaseFrames = mutableMapOf<Int, Int>()

    //입력 비트맵 변환 (기기 환경 고려)
    private fun ensureArgb8888(input: Bitmap): Bitmap {
        return if (input.config != Bitmap.Config.ARGB_8888) {
            input.copy(Bitmap.Config.ARGB_8888, /* isMutable = */ true)
        } else {
            input
        }
    }

    /**
     * 비디오 URI를 받아 분석을 수행하고 FrameLog 리스트를 반환하는 suspend 함수.
     *
     * @param videoUri 분석할 비디오의 URI.
     * @param holdCoordinates 홀드 ID와 좌표(Point 리스트)를 매핑한 맵.
     * @param fps 초당 분석할 프레임 수.
     * @param boxInsetPercentage 박스 크기 조절 비율.
     * @param stabilityThresholdSeconds 안정 접촉으로 간주할 시간(초).
     * @param frameSkipFactor 건너뛸 프레임 수.
     * @param frameScaleFactor 프레임 크기 조절 비율.
     * @param poseDetectionConfidence 포즈 감지 신뢰도 임계값.
     * @param landmarkVisibilityThreshold 랜드마크 가시성 임계값.
     * @param interpolationScale 보간 스케일 팩터.
     * @return 분석 결과로 생성된 FrameLog 리스트. 오류 발생 시 빈 리스트 반환.
     */
    suspend fun analyzeVideoUri(
        videoUri: Uri,
        holdCoordinates: Map<Int, List<Point>>,
        fps: Int = DEFAULT_FPS,
        boxInsetPercentage: Float = DEFAULT_BOX_INSET_PERCENTAGE,
        stabilityThresholdSeconds: Float = DEFAULT_STABILITY_THRESHOLD_SECONDS,
        frameSkipFactor: Int = DEFAULT_FRAME_SKIP_FACTOR,
        frameScaleFactor: Float = DEFAULT_FRAME_SCALE_FACTOR,
        poseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
        landmarkVisibilityThreshold: Float = DEFAULT_LANDMARK_VISIBILITY_THRESHOLD,
        interpolationScale: Float = DEFAULT_INTERPOLATION_SCALE
    ): List<FrameLog> = withContext(Dispatchers.Default) { // 백그라운드 스레드에서 실행

        // 상태 초기화
        logs.clear()
        prevLandmarks = null
        contactFrames.clear()
        lastTouchedBox.clear()
        stableInNewBox.clear()
        releaseFrames.clear()
        LANDMARK_INDICES.forEach {
            contactFrames[it] = mutableMapOf()
            lastTouchedBox[it] = null
            stableInNewBox[it] = null
            releaseFrames[it] = 0
        }

        var poseLandmarker: PoseLandmarker? = null
        var retriever: MediaMetadataRetriever? = null
        val channel = Channel<FrameData>(PIPELINE_CHANNEL_CAPACITY)

        try {
            // PoseLandmarker 초기화
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker.task")
                .setDelegate(Delegate.GPU)
                .build()
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.VIDEO)
                .setMinPoseDetectionConfidence(poseDetectionConfidence)
                .setMinTrackingConfidence(landmarkVisibilityThreshold)
                .setMinPosePresenceConfidence(landmarkVisibilityThreshold)
                .build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.i(TAG, "PoseLandmarker created successfully.")

            // MediaMetadataRetriever 초기화
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoUri)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val frameIntervalUs = 1_000_000L / fps
            val rawFps = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                ?.toFloatOrNull() ?: fps.toFloat()
            val effectiveFps = rawFps / frameSkipFactor
            val thresholdFrames = max(1, (stabilityThresholdSeconds * effectiveFps).toInt())

            // 홀드 좌표를 PolygonBox로 변환
            val localBoxes = holdCoordinates
                .filter { it.value.isNotEmpty() }
                .map { (id, pts) -> PolygonBox.from(Box(id, pts), boxInsetPercentage) }

            // Producer (프레임 추출) 코루틴
            val producerJob = launch(Dispatchers.IO) { // 파일 I/O는 IO 스레드에서
                Log.d(TAG, "Producer started.")
                var frameIndex = 0
                val localRetriever = retriever!!
                try {
                    while (isActive) { // 코루틴이 활성 상태일 때만 실행
                        if (frameIndex % frameSkipFactor != 0) {
                            frameIndex++; continue
                        }
                        val timeUs = frameIndex * frameIntervalUs
                        if (timeUs / 1000 >= durationMs) break

                        val bmp = localRetriever.getFrameAtTime(timeUs, OPTION_CLOSEST) ?: break
                        var smallBmp: Bitmap? = null
                        var fixedBmp: Bitmap? = null
                        try {
                            val origW = bmp.width; val origH = bmp.height
                            val scaleWidth = (origW * frameScaleFactor).toInt()
                            val scaleHeight = (origH * frameScaleFactor).toInt()
                            smallBmp = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true)
                            fixedBmp = ensureArgb8888(smallBmp)

                            channel.send(FrameData(fixedBmp, timeUs, origW, origH))
                            smallBmp.takeIf { it != fixedBmp }?.recycle()

                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing/sending frame: $timeUs", e)
                            fixedBmp?.recycle()
                            smallBmp?.takeIf { it != fixedBmp }?.recycle()
                        } finally {
                            bmp.recycle()
                        }
                        frameIndex++
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Producer error", e)
                } finally {
                    Log.d(TAG, "Producer finished. Closing channel.")
                    channel.close() // Producer가 끝나면 채널 닫기
                }
            }

            // Consumer (프레임 분석) 코루틴
            val consumerJob = launch { // Default 스레드에서 실행
                Log.d(TAG, "Consumer started.")
                val localPoseLandmarker = poseLandmarker!!

                for (frameData in channel) { // 채널에서 데이터 수신
                    var processedBitmap: Bitmap? = null
                    try {
                        processedBitmap = frameData.bitmap
                        val mpImage: MPImage = BitmapImageBuilder(processedBitmap).build()
                        val result = localPoseLandmarker.detectForVideo(mpImage, frameData.timestampUs)

                        val landmarkList = result.landmarks()
                        if (landmarkList.isEmpty()) continue

                        val mutableLandmarks = landmarkList.first().toMutableList()

                        // 랜드마크 보간 로직
                        prevLandmarks?.let { prev ->
                            LIMB_TO_PARENT.forEach { (idx, baseIdx) ->
                                if (idx in LANDMARK_INDICES) {
                                    val cur = mutableLandmarks[idx]
                                    val vis = cur.visibility().orElse(0f)
                                    if (vis < landmarkVisibilityThreshold) {
                                        val vX = prev[idx].x() - prev[baseIdx].x()
                                        val vY = prev[idx].y() - prev[baseIdx].y()
                                        val base = mutableLandmarks[baseIdx]
                                        val ix = base.x() + vX * interpolationScale
                                        val iy = base.y() + vY * interpolationScale
                                        mutableLandmarks[idx] = NormalizedLandmark.create(
                                            ix.toFloat(), iy.toFloat(), base.z(),
                                            Optional.empty(), Optional.empty()
                                        )
                                    }
                                }
                            }
                        }

                        // 접촉 박스 판정
                        val currentTouched = LANDMARK_INDICES.associateWith { idx ->
                            val px = (mutableLandmarks[idx].x() * frameData.originalWidth).toInt()
                            val py = (mutableLandmarks[idx].y() * frameData.originalHeight).toInt()
                            localBoxes.firstOrNull { polyBox ->
                                PolygonBox.pointInPolygon(polyBox.points, px, py) ||
                                        (with(polyBox.bbox) { px in left..right && py in top..bottom })
                            }?.id
                        }

                        val timeMs = frameData.timestampUs / 1000L
                        var logGeneratedThisFrame = false

                        // 안정 접촉 로직
                        LANDMARK_INDICES.forEach { idx ->
                            val touched = currentTouched[idx]
                            if (touched == null) {
                                val r = (releaseFrames[idx] ?: 0) + 1
                                releaseFrames[idx] = r
                                if (r >= RELEASE_THRESHOLD) {
                                    if (stableInNewBox[idx] != null) {
                                        logGeneratedThisFrame = true
                                    }
                                    contactFrames[idx]?.clear()
                                    lastTouchedBox[idx] = null
                                    stableInNewBox[idx] = null
                                    releaseFrames[idx] = 0
                                }
                            } else {
                                releaseFrames[idx] = 0
                                val last = lastTouchedBox[idx]
                                val counts = contactFrames.computeIfAbsent(idx) { mutableMapOf() }
                                if (touched != last) counts.clear()
                                val newCount = (counts[touched] ?: 0) + 1
                                counts[touched] = newCount

                                if (newCount == thresholdFrames && stableInNewBox[idx] != touched) {
                                    stableInNewBox[idx] = touched
                                    logGeneratedThisFrame = true
                                }
                                lastTouchedBox[idx] = touched
                            }
                        }

                        // 로그 생성 및 추가
                        if (logGeneratedThisFrame) {
                            val newLog = FrameLog(
                                timeMs,
                                stableInNewBox[LEFT_HAND],
                                stableInNewBox[RIGHT_HAND],
                                stableInNewBox[LEFT_TOE] ?: stableInNewBox[LEFT_HEEL],
                                stableInNewBox[RIGHT_TOE] ?: stableInNewBox[RIGHT_HEEL]
                            )

                            if (logs.isEmpty() || !logs.last().hasSameHolds(newLog)) {
                                logs.add(newLog)
                                // onContact 콜백 제거 (Worker에서는 직접 콜백 필요 없음)
                            }
                        }

                        prevLandmarks = mutableLandmarks.toList()

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing frame in consumer", e)
                    } finally {
                        processedBitmap?.recycle() // 비트맵 재활용
                    }
                }
                Log.d(TAG, "Consumer finished.")
            }

            // Producer와 Consumer가 모두 끝날 때까지 대기
            producerJob.join()
            consumerJob.join()

            Log.d(TAG, "Analysis successful, returning ${logs.size} logs.")
            return@withContext logs.toList() // 최종 로그 리스트 반환

        } catch (e: Exception) {
            Log.e(TAG, "Error in video processing setup or execution", e)
            return@withContext emptyList<FrameLog>() // 오류 시 빈 리스트 반환
        } finally {
            // 리소스 해제
            retriever?.release()
            poseLandmarker?.close()
            Log.d(TAG, "Resources released.")
        }
    }
}