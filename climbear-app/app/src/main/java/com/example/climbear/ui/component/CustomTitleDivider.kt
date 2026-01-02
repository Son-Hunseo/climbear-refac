package com.example.climbear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomTitleDivider(
    title: String = "Information"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomGradientLine(
            modifier = Modifier.weight(1f)
        )
        Text(
            text = title,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        CustomGradientLine(
            modifier = Modifier.weight(1f),
            colorStops = arrayOf(
                0.0f to Color.Transparent,
                0.2f to Color.Black.copy(alpha = 0.5f),
                0.5f to Color.Black.copy(alpha = 0.3f),
                1.0f to Color.Transparent
            ),
        )
    }
}

@Composable
fun CustomGradientLine (
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 1.dp,
    colorStops: Array<Pair<Float, Color>> = arrayOf(
        0.0f to Color.Transparent,
        0.5f to Color.Black.copy(alpha = 0.3f),
        0.8f to Color.Black.copy(alpha = 0.5f),
        1.0f to Color.Transparent
    ),
) {
    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer { alpha = 0.99f }
            .background(
                Brush.horizontalGradient(
                    colorStops = colorStops
                )
            )
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomTitleDividerPreview() {
    ClimbearTheme {
        CustomTitleDivider()
    }
}