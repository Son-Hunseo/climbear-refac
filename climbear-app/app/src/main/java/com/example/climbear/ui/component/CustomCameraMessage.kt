package com.example.climbear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomCameraMessage(
    modifier: Modifier = Modifier,
    message: String = "테스트 알림 메시지입니다.",
    backgroundColor: Color = Color(0xFF141478),
    fontSize: TextUnit = 20.sp
) {
    Text(
        text = message,
        fontSize = fontSize,
        color = Color(0xFFFFFFFF),
        modifier = modifier
            .background(
                color = backgroundColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50.dp),
            )
            .padding(horizontal = 24.dp, vertical = 8.dp),
        maxLines = 1,
        textAlign = TextAlign.Center,
    )
}

@Preview(
    showBackground = true
)
@Composable
fun CustomCameraMessagePreview() {
    ClimbearTheme {
        CustomCameraMessage()
    }
}


