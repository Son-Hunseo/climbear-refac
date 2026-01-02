package com.example.climbear.ui.screen.center

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.center.CenterRepository
import com.example.climbear.data.center.model.CenterData
import com.example.climbear.data.center.model.CenterMyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject
import java.util.Calendar
import java.util.Locale

@HiltViewModel
class CenterInfoViewModel @Inject constructor(
    private val centerRepository: CenterRepository
) : ViewModel() {

    private val _centerUiState = MutableStateFlow(CenterInfoUiState())
    val centerUiState: StateFlow<CenterInfoUiState> = _centerUiState

    private val _centerMyUiState = MutableStateFlow(CenterMyUiState())
    val centerMyUiState: StateFlow<CenterMyUiState> = _centerMyUiState

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val levelOrder =
        arrayOf("white", "yellow", "orange", "green", "blue", "red", "purple", "gray", "pink")

    // 전체 암장 정보 조히
    fun getCenterList(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = centerRepository.getCenterList()
            if (result.isSuccess) {
                val centerResponses = result.getOrNull()
                val userLat = _centerUiState.value.userLat
                val userLng = _centerUiState.value.userLng

                if (centerResponses?.data != null) {
                    val displayList = centerResponses.data.map{
                        it.toDisplayData(userLat, userLng)
                    }
                    _centerUiState.update {
                        it.copy(centerList = displayList)
                    }
                }
                onSuccess()
            } else {
                // 실패 처리
            }
        }
    }

    // 나의 암장 정보 조회
    fun getCenterMy(context: Context, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = centerRepository.getCenterMy()
            if (result.isSuccess) {
                val centerMyResponses = result.getOrNull()
                val parsed = centerMyResponses?.data?.map {
                    CenterVisitWithCounts(
                        centerName = it.centerName,
                        dailyProblem = it.dailyProblem,
                        problemCounts = calculateProblemCountsFor4Weeks(it),
                        visitDates = extractVisitDatesFor4Weeks(it)
                    )
                } ?: emptyList()
                _centerMyUiState.update{it.copy(centerData = parsed)}
                onSuccess()
            } else {
                // 실패 처리
            }
        }
    }

    private fun calculateProblemCountsFor4Weeks(center: CenterMyData): Array<Int> {
        val counts = Array(9) { 0 }
        val today = Calendar.getInstance()
        val fourWeeksAgo = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -4)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        center.dailyProblem.forEach { record ->
            val parsedDate = runCatching { dateFormat.parse(record.date) }.getOrNull()
            val calendar = Calendar.getInstance().apply { time = parsedDate }

            if (!calendar.before(fourWeeksAgo) && !calendar.after(today)) {
                record.levelCount.forEach { levelCount ->
                    val level = levelCount.level.lowercase()
                    val index = levelOrder.indexOf(level)
                    if (index != -1) {
                        counts[index] += levelCount.count
                    }
                }
            }
        }
        return counts
    }

    private fun extractVisitDatesFor4Weeks(center: CenterMyData): List<Calendar> {
        val today = Calendar.getInstance()
        val fourWeeksAgo = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -4) // 총 4주 포함
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // 시작 기준을 월요일로 설정
        }
        return center.dailyProblem.mapNotNull { record ->
            val parsedDate = runCatching { dateFormat.parse(record.date) }.getOrNull() ?: return@mapNotNull null
            Calendar.getInstance().apply { time = parsedDate }
        }.filter { calendar ->
            !calendar.before(fourWeeksAgo) && !calendar.after(today)
        }
    }

    fun build4WeekCalendar(visitDates: List<Calendar>): List<List<CalendarCell>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.add(Calendar.WEEK_OF_YEAR, -4)

        return List(5) { week ->
            List(7) { day ->
                val cellDate = (calendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, week * 7 + day)
                }
                val visited = visitDates.any { isSameDay(it, cellDate) }
                CalendarCell(cellDate, visited)
            }
        }
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun CenterData.toDisplayData(userLat: Double?, userLng: Double?): CenterDisplayData {
        val (distanceInt, distanceText) = calculateDistanceInMeters(userLat, userLng, latitude, longitude)
        val address = shortenAddress(address)
        return CenterDisplayData(
            centerId = centerId,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            distanceMeters = distanceText,
            rawDist = distanceInt
        )
    }

    fun updateLocation(lat: Double, lng: Double) {
        _centerUiState.update {
            it.copy(userLat = lat, userLng = lng)
        }
    }

    private fun calculateDistanceInMeters(
        lat1: Double?, lng1: Double?,
        lat2: Double, lng2: Double
    ): Pair<Int?, String> {
        if (lat1 == null || lng1 == null) {
            return null to ""
        }
        val start = android.location.Location("").apply {
            latitude = lat1
            longitude = lng1
        }
        val end = android.location.Location("").apply {
            latitude = lat2
            longitude = lng2
        }
        val rawDist = start.distanceTo(end).toInt()
        val text = if (rawDist < 1000) {
            "${rawDist}m"
        } else {
            String.format("%.1fkm", rawDist / 1000.0)
        }
        return rawDist to text
    }

    private fun shortenAddress(address: String): String {
        return address.split(" ").take(3).joinToString(" ")
    }
}