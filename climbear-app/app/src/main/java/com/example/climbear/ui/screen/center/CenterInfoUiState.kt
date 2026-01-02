package com.example.climbear.ui.screen.center

import com.example.climbear.data.center.model.DailyProblemItem
import java.util.Calendar

data class CenterInfoUiState(
    val centerList: List<CenterDisplayData> = emptyList(),
    val userLat: Double? = null,
    val userLng: Double? = null
)

data class CenterDisplayData(
    val centerId: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: String,
    val rawDist: Int?
)

data class CenterMyUiState(
    val centerData: List<CenterVisitWithCounts> = emptyList()
)

data class CenterVisitWithCounts(
    val centerName: String,
    val dailyProblem: List<DailyProblemItem>,
    val problemCounts: Array<Int>,
    val visitDates: List<Calendar>
)

data class CalendarCell(
    val date: Calendar,
    val visited: Boolean
)
