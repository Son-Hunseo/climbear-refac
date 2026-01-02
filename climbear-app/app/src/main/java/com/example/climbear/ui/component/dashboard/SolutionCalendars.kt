package com.example.climbear.ui.component.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.data.dashboard.model.BoulderingGrade
import com.example.climbear.ui.screen.home.MonthSummary
import com.example.climbear.ui.screen.home.RecordUiState
import com.example.climbear.util.loadGrades
import com.example.climbear.util.toColor
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

@Composable
fun SolutionCalendars(
    modifier: Modifier = Modifier,
    height: Double? = 180.0,
    reach: Double?,
    uiState: RecordUiState = RecordUiState(),
    moveToInput: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "문제 풀이\n캘린더",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF141478)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable(onClick = moveToInput)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) { // [변경] 중앙정렬
                            Text(
                                "키  ",
                                color = Color(0xFF141478)
                            )
                            Text(
                                text = if (height?.rem(1.0) == 0.0) height.toInt()
                                    .toString() else height.toString(),
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF141478)
                            )
                            Text(
                                "cm",
                                color = Color(0xFF141478)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) { // [변경] 중앙정렬
                            Text(
                                "리치  ",
                                color = Color(0xFF141478)
                            )
                            Text(
                                text = if (reach?.rem(1.0) == 0.0) reach.toInt()
                                    .toString() else reach.toString(),
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF141478)
                            )
                            Text(
                                "cm",
                                color = Color(0xFF141478)
                            )
                        }
                    }
                    Image(
                        painter = painterResource(R.drawable.dashboard_pen),
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(10.dp))
        }

        if (uiState.monthlyRecordUiList.isNotEmpty()) {
            uiState.monthlyRecordUiList.sortedWith(
                compareByDescending<MonthSummary> { it.year }
                    .thenByDescending { it.month }
            ).forEach { monthSummary ->
                item {
                    SolutionCalendar(
                        year = monthSummary.year,
                        month = monthSummary.month,
                        monthSummary = monthSummary
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }
        } else {
            item {
                val today = Calendar.getInstance()
                val year = today.get(Calendar.YEAR)
                val month = today.get(Calendar.MONTH) + 1
                val day = today.get(Calendar.DAY_OF_MONTH)

                val boxHeight = 36.dp // [추가] 연보라 박스 높이 통일

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.97f)
                            .padding(vertical = 12.dp, horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = year.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF141478).copy(alpha = 0.25f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // 월 박스
                        Box(
                            modifier = Modifier
                                .height(boxHeight) // [변경] 높이 통일
                                .background(
                                    color = Color(0xFFF2F2FB),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${month}월",
                                color = Color(0xFF141478),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        // 일+안내문 박스
                        Box(
                            modifier = Modifier
                                .height(boxHeight) // [변경] 높이 통일
                                .background(
                                    color = Color(0xFFF2F2FB),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = String.format(Locale.KOREA, "%02d", day),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF141478)
                                )
                                Spacer(modifier = Modifier.size(16.dp)) // [변경] 여백 늘림
                                Text(
                                    text = "문제를 풀고 저장해 보세요.",
                                    color = Color(0xFF141478),
                                    fontWeight = FontWeight.Thin,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolutionCalendar(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    monthSummary: MonthSummary? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val grades = loadGrades(context)

    val days = monthSummary?.days

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = year.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0x14, 0x14, 0x78, 25)
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Text(
                text = month.toString() + "월",
                modifier = Modifier
                    .background(color = Color(0xF2, 0xF2, 0xFB), shape = RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xF2, 0xF2, 0xFB),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CalendarItems(
                            grades = grades,
                            day = null,
                            modifier = Modifier.weight(1f),
                            solutions = monthSummary?.totalLevelCounts ?: IntArray(9) { 1 })
                        CalendarButton(
                            expanded = expanded,
                            onClick = { expanded = !expanded },
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                if (expanded && days != null) {
                    items(days) { day ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CalendarItems(
                                grades = grades,
                                day = day.day,
                                modifier = Modifier.weight(1f),
                                solutions = day.levelCounts
                            )
                            Spacer(modifier = Modifier.size(25.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.dashboard_under_arrow),
            contentDescription = null,
            modifier = Modifier.rotate(if (expanded) 180f else 0f)
        )
    }
}

@Preview
@Composable
fun CalendarPreview() {
    SolutionCalendars(
        height = 180.0,
        reach = 105.0
    )
}

@Composable
fun CalendarItems(
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
    grades: List<BoulderingGrade> = emptyList(),
    day: Int?,
    solutions: IntArray = IntArray(9)
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .then(
                if (isToday || day == null) {
                    Modifier.background(
                        color = Color(0xF2, 0xF2, 0xFB),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (day != null) {
            Text(
                text = String.format(Locale.KOREA, "%02d", day),
                modifier = Modifier
                    .background(color = Color(0xF2, 0xF2, 0xFB), shape = RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xF2, 0xF2, 0xFB),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.dashboard_sigma),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(grades) { index, grade ->
                if (solutions[index] != 0) {
                    CalendarItem(
                        color = grade.hex.toColor(),
                        count = solutions[index],
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarItem(
    modifier: Modifier = Modifier,
    color: Color,
    count: Int,
    isToday: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color = color.copy(alpha = 0.7f))
        )
        Text(
            text = count.toString(),
            fontWeight = if (isToday) FontWeight.Black else FontWeight.Thin
        )
    }
}
