package com.example.climbear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomCameraBottomBar (
    modifier: Modifier = Modifier,
    startSlot: (@Composable () -> Unit)? = null,
    centerSlot: (@Composable () -> Unit)? = null,
    endSlot: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 좌측 버튼
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomStart
        ) {
            startSlot?.invoke()
        }
        // 중앙 버튼
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            centerSlot?.invoke()
            }
        // 우측 버튼
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomEnd
        ) {
            endSlot?.invoke()
        }
    }
}

@Composable
fun TempBottomButton(
    onButtonClick: () -> Unit = {}
) {
    Button(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDCDCDC),
            contentColor = Color.Black
        ),
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(color = Color(0xFFDCDCDC))
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomCameraBottomBarPreview() {
    ClimbearTheme {
        CustomCameraBottomBar(
            startSlot = { TempBottomButton() },
            centerSlot = { TempBottomButton() },
            endSlot = { TempBottomButton() }
        )
    }
}