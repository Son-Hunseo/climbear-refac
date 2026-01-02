package com.example.climbear.ui.component.center

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecordGraph(
    problemCounts: Array<Int>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val maxCount = problemCounts.maxOrNull() ?: 0
    val normalizedValues = if (maxCount > 0) {
        problemCounts.map { it.toFloat() / maxCount }
    } else {
        // 0으로 채움: 모두 흐리게 처리
        List(problemCounts.size) { 0f }
    }

    Column(
        modifier = modifier
//            .padding(vertical = 10.dp)
//            .fillMaxWidth()
    ) {
        // 그래프 막대
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .weight(2f)
        ) {
            normalizedValues.forEachIndexed { index, value ->
                val isZero = maxCount == 0
                val barColor =
                    colors.getOrElse(index) { Color.Gray }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // 막대 배경
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xF7, 0xF7, 0xFC))
                    )
                    // 기본값
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(barColor)
                    )
                    // 진행도
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .then(
                                if (!isZero) {
                                    Modifier
                                        .fillMaxHeight(if (isZero) 1f else value)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(barColor.copy(alpha = 0.5f))
                                } else Modifier
                            )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGraph() {
    val problemCounts = arrayOf(0, 3, 4, 6, 2, 5, 1, 0, 2, 4)
    val colors = listOf(
        Color(0xFFE0E0E0), Color(0xFFFFA726), Color(0xFFFFD600), Color(0xFF66BB6A),
        Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFFBDBDBD),
        Color(0xFFE91E63), Color(0xFFEC407A)
    )

    RecordGraph(
        problemCounts = problemCounts,
        colors = colors
    )
}