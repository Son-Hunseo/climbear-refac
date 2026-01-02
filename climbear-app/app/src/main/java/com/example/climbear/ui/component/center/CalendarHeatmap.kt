package com.example.climbear.ui.component.center

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.ui.screen.center.CenterInfoViewModel
import java.util.Calendar

@Composable
fun CalendarHeatmap(modifier: Modifier = Modifier, visitDates: List<Calendar>, centerInfoViewModel: CenterInfoViewModel) {
    val calendarMatrix = remember(visitDates) { centerInfoViewModel.build4WeekCalendar(visitDates) }

    Column (
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                Text(
                    text = it,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(align = Alignment.Top),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            calendarMatrix.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1f)
                                .padding(2.dp)
                                .background(
                                    color = if (cell.visited) Color(0xFFE5E5E4) else Color(
                                        0XFFF7F6F6
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}
