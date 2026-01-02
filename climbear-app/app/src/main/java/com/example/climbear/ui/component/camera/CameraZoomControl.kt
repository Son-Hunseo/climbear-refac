package com.example.climbear.ui.component.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor

@Composable
fun CameraZoomControl(
    zoomLevel: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val zoomButtons = mutableListOf(0.5f, 1.0f, 2.0f)

    if (zoomLevel < 1.0f) {
        zoomButtons[0] = zoomLevel
    } else if (zoomLevel < 2.0f) {
        zoomButtons[1] = zoomLevel
    } else {
        zoomButtons[2] = zoomLevel
    }

    fun handleButtonClick(zoom: Float) {
        onZoomChange(zoom)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = modifier
                .wrapContentSize()
                .padding(16.dp)
                .clip(
                    shape = CircleShape
                )
                .background(color = Color.Gray.copy(alpha = 0.3f))
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            zoomButtons.forEach { zoom ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable { handleButtonClick(zoom) }
                        .then(
                            if (zoomLevel == zoom) {
                                Modifier
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .background(
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (zoomLevel == zoom) (floor(zoomLevel * 10.0) / 10.0).toString()
                            .removeSuffix(".0") else zoom.toString().removeSuffix(".0"),
                        color = if (zoomLevel == zoom) Color.Black else Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}