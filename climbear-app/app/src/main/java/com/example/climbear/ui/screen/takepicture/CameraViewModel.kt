package com.example.climbear.ui.screen.takepicture

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CameraState(
    val zoomLevel: Float = 1.0f,
    val wideZoomLevel: Float = 0.0f,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK
)

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState

    fun setZoomLevel(level: Float) {
        _cameraState.update {
            it.copy(
                zoomLevel = level,
                wideZoomLevel = level * 2
            )
        }
    }

    fun toggleLensFacing() {
        _cameraState.update {
            it.copy(
                lensFacing = if (it.lensFacing == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            )
        }
    }

    fun shouldRebindCamera(oldZoom: Float, newZoom: Float): Boolean {
        if (newZoom < 1.0f) {
            if (oldZoom >= 1.0f) {
                return true
            }
            return false
        } else {
            if (oldZoom < 1.0f) {
                return true
            }
            return false
        }
    }
}