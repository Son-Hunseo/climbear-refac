package com.example.climbear.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.climbear.R
import com.example.climbear.data.dashboard.DashboardRepository
import com.example.climbear.data.dashboard.model.Rank
import com.example.climbear.data.local.model.LogoData
import com.example.climbear.util.loadGrades
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

val logoMap = mapOf(
    "white" to LogoData(
        Color(0xFFF7F7FC),
        R.drawable.logo_small_white,
        R.drawable.logo_big_white,
        null,
        R.drawable.dashboard_toyellow
    ),
    "yellow" to LogoData(
        Color(0xFFFFC800),
        R.drawable.logo_small_yellow,
        R.drawable.logo_big_yellow,
        R.drawable.dashboard_nextyellow,
        R.drawable.dashboard_toorange
    ),
    "orange" to LogoData(
        Color(0xFFF57E00),
        R.drawable.logo_small_orange,
        R.drawable.logo_big_orange,
        R.drawable.dashboard_nextorange,
        R.drawable.dashboard_togreen
    ),
    "green" to LogoData(
        Color(0xFF1B7C1D),
        R.drawable.logo_small_green,
        R.drawable.logo_big_green,
        R.drawable.dashboard_nextgreen,
        R.drawable.dashboard_toblue
    ),
    "blue" to LogoData(
        Color(0xFF141478),
        R.drawable.logo,
        R.drawable.logo,
        R.drawable.dashboard_nextblue,
        R.drawable.dashboard_tored
    ),
    "red" to LogoData(
        Color(0xFF990003),
        R.drawable.logo_small_red,
        R.drawable.logo_big_red,
        R.drawable.dashboard_nextred,
        R.drawable.dashboard_topurple
    ),
    "purple" to LogoData(
        Color(0xFF8E17C9),
        R.drawable.logo_small_purple,
        R.drawable.logo_big_purple,
        R.drawable.dashboard_nextpurple,
        R.drawable.dashboard_togray
    ),
    "gray" to LogoData(
        Color(0xFF878787),
        R.drawable.logo_small_gray,
        R.drawable.logo_big_gray,
        R.drawable.dashboard_nextgray,
        R.drawable.dashboard_topink
    ),
    "pink" to LogoData(
        Color(0xFF781467),
        R.drawable.logo_small_pink,
        R.drawable.logo_big_pink,
        R.drawable.dashboard_nextpink,
        null
    )
)

val nextMap = mapOf(
    "white" to logoMap["yellow"],
    "yellow" to logoMap["orange"],
    "orange" to logoMap["green"],
    "green" to logoMap["blue"],
    "blue" to logoMap["red"],
    "red" to logoMap["purple"],
    "purple" to logoMap["gray"],
    "gray" to logoMap["pink"],
    "pink" to null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val _myRankLogo = MutableStateFlow<LogoData>(
        LogoData(
            Color.White,
            R.drawable.logo_small_white,
            R.drawable.logo_big_white,
            null,
            R.drawable.dashboard_toyellow
        )
    )
    val myRankLogo: StateFlow<LogoData> = _myRankLogo

    private val _nextRankLogo = MutableStateFlow<LogoData>(
        LogoData(
            Color.Yellow,
            R.drawable.logo_small_yellow,
            R.drawable.logo_big_yellow,
            R.drawable.dashboard_nextyellow,
            R.drawable.dashboard_toorange
        )
    )
    val nextRankLogo: StateFlow<LogoData> = _nextRankLogo

    fun updateRankColor(rank: String, context: Context) {
        val grades = loadGrades(context)

        val grade = grades.find { grade ->
            grade.color.uppercase() == rank.uppercase()
        }

        if (grade == null) {
            return
        }

        val logo = logoMap[grade.color.lowercase()]
        val nextLogo = nextMap[grade.color.lowercase()]

        when (logo) {
            null -> return
            else -> {
                _myRankLogo.value = LogoData(
                    color = logo.color,
                    smallLogoResId = logo.smallLogoResId,
                    bigLogoResId = logo.bigLogoResId,
                    nextResId = logo.nextResId,
                    arrowResId = logo.arrowResId
                )
            }
        }
        when (nextLogo) {
            null -> return
            else -> {
                _nextRankLogo.value = LogoData(
                    color = nextLogo.color,
                    smallLogoResId = nextLogo.smallLogoResId,
                    bigLogoResId = nextLogo.bigLogoResId,
                    nextResId = nextLogo.nextResId,
                    arrowResId = nextLogo.arrowResId
                )
            }
        }
    }

    private val _rankState = MutableStateFlow<Rank>(Rank())
    val rankState: StateFlow<Rank> = _rankState

    suspend fun getExp() {
        Log.d("okh", "getExp")
        val data = dashboardRepository.getExp().getOrNull()?.data

        if (data != null) {
            _rankState.value = Rank(
                exp = data.exp,
                maxExp = data.maxExp,
                levelName = data.levelName,
                nextLevelName = data.nextLevelName
            )
        }
    }
}