package com.example.climbear.ui.screen.selecthold

import androidx.compose.ui.geometry.Offset
import com.example.climbear.data.hold.model.HoldResponse

enum class SelectHoldStep {
    SELECT_GRID,
    DETECT_HOLDS,
    SELECT_START,
    SELECT_TOP,
    SELECT_COLOR,
    SELECT_LEVEL,
    COMPLETE
}

data class HoldSelectUiState(
    val gridPoints: List<Offset> = emptyList(), // 좌표 2개 저장
    val currentStep: SelectHoldStep = SelectHoldStep.SELECT_GRID,
    val isHoldLoaded: Boolean = false,
    val holdCoordinates: List<HoldResponse> = emptyList(),
    val selectedHoldIds: Set<Int> = emptySet(),
    val startHoldIds: Set<Int> = emptySet(),
    val topHoldId: Int? = null,
    val choiceColor: String? = null,
    val level: String? = null,
    val problemId: Int? = null,
    val categoryId: Int? = null,
    val lat: Double? = null,
    val lng: Double? = null,
)

data class HoldStepState(
    val isHoldLoaded: Boolean = false,
    val currentStep: SelectHoldStep = SelectHoldStep.SELECT_GRID,
    val guideMessage: String = "암벽의 구멍 2개를 선택하세요.",
    val warningMessage: String? = null,
)