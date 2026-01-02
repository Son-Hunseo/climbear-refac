package com.example.climbear.ui.screen.solutionhint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.climbear.R
import com.example.climbear.background.PoseAnalyzer.Companion.DEFAULT_FPS
import com.example.climbear.data.hold.model.Coordinate
import com.example.climbear.data.hold.model.HoldResponse
import com.example.climbear.data.holdlog.model.HoldLog
import com.example.climbear.data.record.RecordRepository
import com.example.climbear.data.record.model.FrameLog
import com.example.climbear.data.record.model.RecordRequest
import com.example.climbear.data.record.model.Route
import com.example.climbear.data.record.model.SimilarRecordData
import com.example.climbear.data.record.model.toHoldResponse
import com.example.climbear.data.solution.SolutionRepository
import com.example.climbear.data.solution.model.SolutionData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

data class RouteResult(
    val route: Route,
    val isSolved: Boolean,
    val successFrameCount: Int,
    val holdLog: List<HoldLog>
)

data class SolutionUiState(
    val solutionData: SolutionData? = null,
    val similarRecordList: List<SimilarRecordData> = emptyList(),
    val similarSolutionData: SolutionData? = null,
    val routeResult: RouteResult? = null,
    val isLogLoaded: Boolean = false,
    val loadState: SolutionLoadState = SolutionLoadState.Loading,
    val solutionImageUrl: String = "",
    val solutionHolds: List<HoldResponse> = emptyList(),
    val selectedTab: String = "hint"
)

data class SolutionBitmapsState(
    val bitmaps: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false
)

sealed class SolutionLoadState {
    data object Loading : SolutionLoadState()
    data object Success : SolutionLoadState()
    data object Error : SolutionLoadState()
}

@HiltViewModel
class SolutionViewModel @Inject constructor(
    private val solutionRepository: SolutionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SolutionUiState())
    val uiState: StateFlow<SolutionUiState> = _uiState

    private val _selectedHint = MutableStateFlow<SolutionData?>(null)
    val selectedHint: StateFlow<SolutionData?> = _selectedHint

    private val _bitmaps = MutableStateFlow(SolutionBitmapsState())
    val bitmaps: StateFlow<SolutionBitmapsState> = _bitmaps

    private fun resetSolution() {
        _uiState.update {
            it.copy(
                solutionImageUrl = "",
                solutionHolds = emptyList()
            )
        }
    }

    fun resetState() {
        _uiState.update {
            it.copy(
                selectedTab = "hint",
                solutionHolds = emptyList(),
                solutionImageUrl = ""
            )
        }
        _selectedHint.value = null
    }

    fun selectTab(tabName: String) {
        _uiState.update {
            it.copy(
                selectedTab = tabName
            )
        }
    }

    fun startLogLoad() {
        _uiState.update {
            it.copy(
                isLogLoaded = false
            )
        }
    }

    fun loadSolutionState(problemId: Int, categoryId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loadState = SolutionLoadState.Loading)
            }

            val solutionLoadSuccess = loadSolution(problemId)
            val similarSolutionsLoadSuccess = loadSimilarSolutions(categoryId)

            if (solutionLoadSuccess && similarSolutionsLoadSuccess) {
                _uiState.update {
                    it.copy(loadState = SolutionLoadState.Success)
                }
            } else {
                _uiState.update {
                    it.copy(loadState = SolutionLoadState.Error)
                }
            }
        }
    }

    private suspend fun loadSolution(problemId: Int): Boolean {
        return try {
            val result = solutionRepository.getSolution(problemId)
            if (result.isSuccess) {
                result.getOrNull()?.data?.let { data ->
                    _uiState.update {
                        it.copy(
                            solutionData = SolutionData(
                                leftHand = data.leftHand,
                                rightHand = data.rightHand,
                                leftFoot = data.leftFoot,
                                rightFoot = data.rightFoot
                            )
                        )
                    }
                }
                true
            } else {
                Log.e("SolutionViewModel", "API 호출 실패: ${result.exceptionOrNull()?.message}")
                false
            }
        } catch (e: Exception) {
            Log.e("SolutionViewModel", "문제 ID가 없습니다: ${e.message}")
            false
        }
    }

    /**
     * 로그를 읽어 서버로 기록 전송하는 메서드
     */
    fun postRecordFromLog(
        context: Context,
        problemId: Int?,
        startHoldIds: Set<Int>,
        topHoldId: Int?,
        fps: Int = DEFAULT_FPS,
        height: Double
    ) {
        _uiState.update {
            it.copy(isLogLoaded = false)
        }
        val problemIdVal = problemId ?: return

        val result = loadRouteFromLog(context, startHoldIds, topHoldId) ?: run {
            Log.e("SolutionViewModel", "Route를 불러오지 못했습니다.")
            return
        }

        _uiState.update {
            it.copy(
                routeResult = result,
                isLogLoaded = true
            )
        }

        val frameCount = result.successFrameCount
        val timeMs = frameCount * 1000L / fps
        val timeForApi = (timeMs / 1000).toInt()

        val recordRequest = RecordRequest(
            problemId = problemIdVal,
            route = result.route,
            time = timeForApi,
            height = height,
        )

        if (result.isSolved) {
            viewModelScope.launch {
                val apiResult = recordRepository.postRecordsMember(recordRequest)
                if (apiResult.isSuccess) {
                    Log.d("SolutionViewModel", "기록 전송 성공: ${apiResult.getOrNull()?.data?.message}")
                } else {
                    Log.e("SolutionViewModel", "기록 전송 실패: ${apiResult.exceptionOrNull()?.message}")
                }
            }
        } else {
            viewModelScope.launch {
                val apiResult = recordRepository.patchRecordsFail(problemIdVal)
                if (apiResult.isSuccess) {
                    Log.d("SolutionViewModel", "기록 전송 성공: ${apiResult.getOrNull()?.data?.message}")
                } else {
                    Log.e("SolutionViewModel", "기록 전송 실패: ${apiResult.exceptionOrNull()?.message}")
                }
            }
        }
    }

    private suspend fun loadSimilarSolutions(categoryId: Int): Boolean {
        return try {
            val result = recordRepository.getSimilarRecordList(categoryId)
            if (result.isSuccess) {
                val list = result.getOrNull()
                if (list?.data != null) {
                    _uiState.update {
                        it.copy(
                            similarRecordList = list.data
                        )
                    }
                }
                true
            } else {
                Log.e("SolutionViewModel", "Api is failed")
                false
            }
        } catch (e: Exception) {
            Log.e("SolutionViewModel", "Api is failed", e)
            false
        }
    }

    private suspend fun loadDetailSolution(problemId: Int) {
        try {
            val result = recordRepository.getDetailRecord(problemId)
            if (result.isSuccess) {
                val detailData = result.getOrNull()
                if (detailData?.data != null) {
                    _uiState.update {
                        it.copy(
                            similarSolutionData = detailData.data[0].route,
                            solutionImageUrl = detailData.data[0].imageName,
                            solutionHolds = detailData.data[0].selected.map { hold -> hold.toHoldResponse() }
                        )
                    }
                    _selectedHint.value = _uiState.value.similarSolutionData
                }
            } else {
                Log.e("SolutionViewModel", "loadDetailSolution Api is failed")
            }
        } catch (e: Exception) {
            Log.e("SolutionViewModel", "Api is failed", e)
        }
    }

    fun selectHint(
        context: Context,
        problemId: Int,
        url: String = "",
        holds: List<HoldResponse> = emptyList(),
        originalWidth: Int = 0,
        originalHeight: Int = 0
    ) {
        viewModelScope.launch {
            when (problemId) {
                -1 -> {
                    _selectedHint.value = null
                    resetSolution()
                }

                0 -> {
                    _bitmaps.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    _selectedHint.value = _uiState.value.solutionData
                    resetSolution()

                    _selectedHint.value?.let { hint ->
                        val bitmaps = generateOverlayBitmapsFromUrl(
                            context = context,
                            imageUrl = url,
                            solutionData = hint,
                            holds = holds,
                            originalWidth = originalWidth,
                            originalHeight = originalHeight
                        )
                        _bitmaps.update {
                            it.copy(
                                bitmaps = bitmaps
                            )
                        }
                    }
                    _bitmaps.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }

                else -> {
                    _bitmaps.update {
                        it.copy(
                            isLoading = true
                        )
                    }

                    loadDetailSolution(problemId)

                    val imageUrl = uiState
                        .map { it.solutionImageUrl }
                        .filter { it.isNotBlank() }
                        .first()

                    val imageSize = getImageSizeFromUrl(imageUrl)

                    _selectedHint.value?.let { hint ->
                        val bitmaps = generateOverlayBitmapsFromUrl(
                            context = context,
                            imageUrl = _uiState.value.solutionImageUrl,
                            solutionData = hint,
                            holds = _uiState.value.solutionHolds,
                            originalWidth = imageSize?.first ?: 3024,
                            originalHeight = imageSize?.second ?: 4032
                        )
                        _bitmaps.update {
                            it.copy(
                                bitmaps = bitmaps
                            )
                        }
                    }
                    _bitmaps.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun generateOverlayBitmapsFromUrl(
        context: Context,
        imageUrl: String,
        solutionData: SolutionData,
        holds: List<HoldResponse>,
        originalWidth: Int,
        originalHeight: Int,
    ): List<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)

            if (result is SuccessResult) {
                val bitmap =
                    (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
                return@withContext generateOverlayBitmaps(
                    context,
                    bitmap,
                    solutionData,
                    holds,
                    originalWidth,
                    originalHeight
                )
            } else {
                Log.e("Bitmap", "Image load failed for URL: $imageUrl")
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            Log.e("Bitmap", "Error loading image from URL", e)
            emptyList()
        }
    }

    private fun generateOverlayBitmaps(
        context: Context,
        originalBitmap: Bitmap?,
        solutionData: SolutionData,
        holds: List<HoldResponse>,
        originalWidth: Int,
        originalHeight: Int,
    ): List<Bitmap> {
        if (originalBitmap == null) {
            return emptyList()
        }

        val holdMap = holds.associateBy { it.holdId }
        val frameCount = solutionData.leftHand.size
        val outputBitmaps = mutableListOf<Bitmap>()

        val drawableMap = mapOf(
            0 to ContextCompat.getDrawable(context, R.drawable.left_hand),
            1 to ContextCompat.getDrawable(context, R.drawable.right_hand),
            2 to ContextCompat.getDrawable(context, R.drawable.left_foot),
            3 to ContextCompat.getDrawable(context, R.drawable.right_foot)
        )

        val scaleX = originalBitmap.width.toFloat() / originalWidth
        val scaleY = originalBitmap.height.toFloat() / originalHeight

        for (i in 0 until frameCount) {
            val frame = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(frame)

            val sameHoldHand = solutionData.leftHand[i] == solutionData.rightHand[i]
            val sameHoldFoot = solutionData.leftFoot[i] == solutionData.rightFoot[i]

            listOf(
                solutionData.leftHand[i],
                solutionData.rightHand[i],
                solutionData.leftFoot[i],
                solutionData.rightFoot[i]
            ).forEachIndexed { index, holdId ->
                val hold = holdMap[holdId] ?: return@forEachIndexed

                val average = hold.average ?: run {
                    val totalX = hold.coordinates.sumOf { it.x }
                    val totalY = hold.coordinates.sumOf { it.y }
                    val count =
                        hold.coordinates.size.takeIf { it > 0 } ?: return@run Coordinate(0, 0)
                    Coordinate(totalX / count, totalY / count)
                }

                val centerX = (average.x * scaleX).toInt()
                val centerY = (average.y * scaleY).toInt()


                val iconSize = 96
                val offsetSize = (iconSize / 4)

                val offsetX = when (index) {
                    0 -> if (sameHoldHand) -offsetSize else 0
                    1 -> if (sameHoldHand) offsetSize else 0
                    2 -> if (sameHoldFoot) -offsetSize else 0
                    3 -> if (sameHoldFoot) offsetSize else 0
                    else -> 0
                }
                val offsetY = when (index) {
                    2 -> offsetSize
                    3 -> offsetSize
                    else -> 0
                }

                drawableMap[index]?.apply {
                    setBounds(
                        centerX + offsetX - iconSize / 2,
                        centerY + offsetY - iconSize / 2,
                        centerX + offsetX + iconSize / 2,
                        centerY + offsetY + iconSize / 2
                    )
                    draw(canvas)
                }
            }
            outputBitmaps.add(frame)
        }

        return outputBitmaps
    }

    private suspend fun getImageSizeFromUrl(imageUrl: String): Pair<Int, Int>? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()
                connection.disconnect()

                val width = options.outWidth
                val height = options.outHeight

                if (width > 0 && height > 0) {
                    if (width > height) Pair(height, width)
                    else Pair(width, height)
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    /**
     * JSON 로그에서 Route와 isSolved 여부 및 성공 프레임 차이를 반환
     */
    private fun loadRouteFromLog(
        context: Context,
        startHoldIds: Set<Int>,
        topHoldId: Int?
    ): RouteResult? {
        val file = File(context.filesDir, "hold_log.json")
        if (!file.exists()) return null

        // 전체 로그 읽기
        val json = file.readText()
        val type = object : TypeToken<List<FrameLog>>() {}.type
        var logs: List<FrameLog> = GsonBuilder().serializeNulls().create().fromJson(json, type)

        logs = logs.filter { it.timeMs >= 500 }

        // 시작 인덱스
        var rawStartIndex: Int
        rawStartIndex = logs.indexOfFirst { frame ->
            (frame.left_hand != null && startHoldIds.contains(frame.left_hand))
                    && (frame.right_hand != null && startHoldIds.contains(frame.right_hand))
                    && frame.timeMs >= 2000
        }
        if (rawStartIndex < 0) {
            var consecutiveOneHandStartIndex = -1

            for (logIndex in 0 until logs.size - 1) {
                val frame1 = logs[logIndex]
                val frame2 = logs[logIndex + 1]

                val conditionMetFrame1 =
                    (frame1.timeMs >= 2000) &&
                            ((frame1.left_hand != null && startHoldIds.contains(frame1.left_hand)) ||
                                    (frame1.right_hand != null && startHoldIds.contains(frame1.right_hand)))

                if (conditionMetFrame1) {
                    val conditionMetFrame2 =
                        (frame2.timeMs >= 2000) &&
                                ((frame2.left_hand != null && startHoldIds.contains(frame2.left_hand)) ||
                                        (frame2.right_hand != null && startHoldIds.contains(frame2.right_hand)))

                    if (conditionMetFrame2) {
                        consecutiveOneHandStartIndex = logIndex
                        break
                    }
                }
            }
            if (consecutiveOneHandStartIndex >= 0) {
                rawStartIndex = consecutiveOneHandStartIndex
            } else {
                rawStartIndex = -1
            }
        }
        if (rawStartIndex < 0) {
            rawStartIndex = 4
        }
        val startIndex = rawStartIndex.coerceAtMost(logs.lastIndex)

        // 종료 인덱스
        var rawEndIndex: Int = -1
        topHoldId?.let { target ->
            // 양손 동시 종료 조건
            val rawEndIndexBothHands = logs.indexOfFirst { frame ->
                (frame.left_hand != null && frame.left_hand == target)
                        && (frame.right_hand != null && frame.right_hand == target)
            }
            if (rawEndIndexBothHands >= 0) {
                rawEndIndex = rawEndIndexBothHands
            } else {
                // 한 손 종료 조건
                val rawEndIndexOneHand = logs.indexOfFirst { frame ->
                    (frame.left_hand != null && frame.left_hand == target)
                            || (frame.right_hand != null && frame.right_hand == target)
                }
                if (rawEndIndexOneHand >= 0) {
                    // 한 손 종료 시 +3 적용
                    rawEndIndex = maxOf(0, rawEndIndexOneHand + 3)
                } else {
                    rawEndIndex = -1
                }
            }
        } ?: run {
            rawEndIndex = -1 //
        }

        val endIndex = rawEndIndex // rawEndIndex를 그대로 사용
        // 5) 해결 여부 및 자를 범위 계산
        val isSolved = endIndex >= startIndex && endIndex >= 0
        val sliceEnd = if (isSolved) endIndex + 1 else logs.size

        // 6) 서브리스트
        val slicedLogs = logs.subList(startIndex, sliceEnd)

        // 7) Route 생성
        val route = Route(
            leftHand = slicedLogs.map { it.left_hand },
            rightHand = slicedLogs.map { it.right_hand },
            leftFoot = slicedLogs.map { it.left_foot },
            rightFoot = slicedLogs.map { it.right_foot }
        )

        // 8) HoldLog 리스트
        val holdLogList = slicedLogs.map { frame ->
            HoldLog(
                timeMs = frame.timeMs,
                holdList = listOfNotNull(
                    frame.left_hand?.toString(),
                    frame.right_hand?.toString(),
                    frame.left_foot?.toString(),
                    frame.right_foot?.toString()
                )
            )
        }

        // 9) 성공 프레임 수
        val successFrames = if (isSolved) endIndex - startIndex else 0

        return RouteResult(
            route = route,
            isSolved = isSolved,
            successFrameCount = successFrames,
            holdLog = holdLogList
        )
    }
}