package com.example.climbear.ui.screen.holdlog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.hold.model.Coordinate
import com.example.climbear.data.holdlog.model.HoldLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldLogViewModel @Inject constructor(

) : ViewModel() {
    private val _frames = MutableStateFlow<List<Bitmap>>(emptyList())
    val frames: StateFlow<List<Bitmap>> = _frames

    private val _isFramesLoaded = MutableStateFlow(false)
    val isFramesLoaded: StateFlow<Boolean> = _isFramesLoaded

    private var previousUri: Uri? = null

    fun extractFramesFromVideo(
        context: Context,
        videoUri: Uri?,
        holdLogs: List<HoldLog>?,
        holdMap:Map<Int, List<Coordinate>>,
    ) {
        Log.d("videoTest", "영상 추출 시작")
        if (previousUri == videoUri && _frames.value.isNotEmpty() || videoUri == null || holdLogs == null) return
        _frames.value = emptyList()

        Log.d("videoTest", "조건 추가")

        viewModelScope.launch(Dispatchers.IO) {
            _isFramesLoaded.value = false
            val retriever = MediaMetadataRetriever()
            val frames = mutableListOf<Bitmap>()

            // 디바이스에서 접근 시
            retriever.setDataSource(context, videoUri)
            Log.d("videoTest", "디바이스 접근")

            // 저장된 동영상 사용
//            val afd = context.resources.openRawResourceFd(R.raw.dummy_video)
//            retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            for (holdLog in holdLogs) {
                val originalFrame = retriever.getFrameAtTime(holdLog.timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST) ?: continue
                val frameWithOverlay = drawOverlay(originalFrame, holdLog.holdList, holdMap)
                frames.add(frameWithOverlay)
            }
            Log.d("videoTest", "프레임 완료")
            retriever.release()
            _frames.value = frames
            _isFramesLoaded.value = true
            previousUri = videoUri
        }
    }

    private fun drawOverlay(
        bitmap: Bitmap,
        holdIds: List<String>,
        holdMap: Map<Int, List<Coordinate>>
    ): Bitmap {
        val frame = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(frame)
        val paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 10f
            isAntiAlias = true
        }

        holdIds.forEach { id ->
            val holds = id.toIntOrNull()
                ?.let { holdMap[it] }
                ?: return@forEach
            if (holds.isNotEmpty()) {
                val points = holds.map {
                    Offset(it.x.toFloat(), it.y.toFloat())
                }
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach {
                        lineTo(it.x, it.y)
                    }
                    close()
                }
                canvas.drawPath(path, paint)
            }
        }
        return frame
    }
}