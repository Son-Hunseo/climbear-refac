package com.example.climbear.ui.screen.home

import androidx.lifecycle.ViewModel
import com.example.climbear.data.record.RecordRepository
import com.example.climbear.data.record.model.RecordData
import com.example.climbear.util.isToday
import com.example.climbear.util.toDay
import com.example.climbear.util.toYearMonthPair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class RecordUiState(
    val allRecords: List<RecordData> = emptyList(),
    val todayRecords: List<RecordData> = emptyList(),
    val monthlyRecordUiList: List<MonthSummary> = emptyList()
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {
    sealed class UploadState {
        object Loading : UploadState()
        object Success : UploadState()
        data class Error(val message: String) : UploadState()
    }

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Loading)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState

    val levelOrder = listOf(
        "white", "yellow", "orange", "green", "blue", "red", "purple", "gray", "pink"
    )

    suspend fun getRecordList() {
        _uploadState.value = UploadState.Loading
        try {
            val records = recordRepository.getRecordList().getOrNull()

            val data = records?.data

            if (data == null) {
                return
            }
            val todayRecords = data.filter { it.lastSolvesDate.isToday() }

            val recordsByMonth = data.groupBy { it.lastSolvesDate.toYearMonthPair() }

            val recordsByDayInMonth = recordsByMonth.mapValues { (_, monthLyList) ->
                monthLyList.groupBy { it.lastSolvesDate.toDay() }
            }


            val monthlyRecordUiList = recordsByDayInMonth.map { (yearMonth, dailyMap) ->
                val levelCountsTotal = IntArray(9)

                val dailySummaries = dailyMap.map { (day, records) ->
                    val levelCounts = IntArray(9)
                    records.forEach { record ->
                        val index = levelOrder.indexOf(record.level.lowercase())
                        if (index != -1) {
                            levelCounts[index] += 1
                            levelCountsTotal[index] += 1
                        }
                    }
                    DaySummary(day, levelCounts)
                }.sortedByDescending { it.day }

                MonthSummary(
                    year = yearMonth.first,
                    month = yearMonth.second,
                    totalLevelCounts = levelCountsTotal,
                    days = dailySummaries
                )
            }

            _uiState.value = RecordUiState(
                allRecords = data,
                todayRecords = todayRecords,
                monthlyRecordUiList = monthlyRecordUiList
            )

            _uploadState.value = UploadState.Success
        } catch (e: Exception) {
            _uploadState.value = UploadState.Error(e.message ?: "통신 에러")
        }
    }
}

data class DaySummary(
    val day: Int,
    val levelCounts: IntArray
)

data class MonthSummary(
    val year: Int,
    val month: Int,
    val totalLevelCounts: IntArray,
    val days: List<DaySummary>
)
