package com.example.climbear.util

import android.media.MediaExtractor
import android.media.MediaFormat

/**
 * 비디오 경로에서 실제 FPS를 읽어옵니다.
 * @param videoPath 앱 내부 혹은 외부 저장소의 비디오 파일 경로
 * @return 프레임 레이트 (fps), 메타데이터에 없으면 기본 30fps 반환
 */
object VideoUtils {
    fun getVideoFps(videoPath: String): Int {
        val extractor = MediaExtractor().apply {
            setDataSource(videoPath)
        }
        // 보통 트랙 0이 비디오 트랙
        val format: MediaFormat = extractor.getTrackFormat(0)
        val fps = if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
            format.getInteger(MediaFormat.KEY_FRAME_RATE)
        } else {
            30  // 기본값
        }
        extractor.release()
        return fps
    }
}
