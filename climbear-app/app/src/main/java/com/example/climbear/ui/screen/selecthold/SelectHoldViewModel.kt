package com.example.climbear.ui.screen.selecthold

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.hold.HoldRepository
import com.example.climbear.data.hold.model.HoldClassifyRequest
import com.example.climbear.data.hold.model.HoldRequest
import com.example.climbear.data.problem.ProblemRepository
import com.example.climbear.data.problem.model.HoldCoordinates
import com.example.climbear.data.problem.model.ProblemRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectHoldViewModel @Inject constructor(
    private val holdRepository: HoldRepository,
    private val problemRepository: ProblemRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HoldSelectUiState())
    val uiState: StateFlow<HoldSelectUiState> = _uiState

    private val _holdStepState = MutableStateFlow(HoldStepState())
    val holdStepState: StateFlow<HoldStepState> = _holdStepState

    private val _previousUrl = MutableStateFlow<String?>(null)
    val previousUrl: StateFlow<String?> = _previousUrl

    // url 저장
    private fun setPreviousUrl(url: String) {
        _previousUrl.value = url // 기본값으로 덮어씀
    }

    // 초기화
    private fun resetUiState() {
        _uiState.value = _uiState.value.copy(
            gridPoints = emptyList(),
            holdCoordinates = emptyList(),
            selectedHoldIds = emptySet(),
            startHoldIds = emptySet(),
            topHoldId = null,
            level = null,
            choiceColor = null,
            problemId = null,
            categoryId = null,
            lat = null,
            lng = null
        )
        _holdStepState.value = _holdStepState.value.copy(
            isHoldLoaded = false,
            currentStep = SelectHoldStep.SELECT_GRID,
            guideMessage = "암벽의 구멍 2개를 선택하세요.",
            warningMessage = null
        )
    }

    // 홀드 인식
    fun detectHold(pictureUrl: String) {
        _uiState.update {
            it.copy(gridPoints = emptyList())
        }

        if (_previousUrl.value == pictureUrl) return
        resetUiState()
        val request = HoldRequest(pictureUrl = pictureUrl)
        viewModelScope.launch {
            _holdStepState.update { it.copy(isHoldLoaded = false) }
            val result = holdRepository.postHold(request)

            if (result.isSuccess) {
                val holdResponses = result.getOrNull() ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    holdCoordinates = holdResponses
                )
                setPreviousUrl(pictureUrl)
                Log.d("hold", "홀드 호출 성공")
                _holdStepState.update { it.copy(isHoldLoaded = true) }
            } else {
                // 실패 처리
                Log.d("hold", "홀드 호출 실패 ${result.exceptionOrNull()}")
            }
        }
    }

    // 스타트 홀드 선택 토글
    fun toggleStartHold(x: Float, y: Float) {
        Log.d("StepCheck", "toggleStartHold")
        val state = _uiState.value
        val selectedHolds = state.holdCoordinates.filter { it.holdId in state.selectedHoldIds }
        val nearest = selectedHolds.minByOrNull { hold ->
            val dx = hold.average!!.x - x
            val dy = hold.average!!.y - y
            dx * dx + dy * dy
        } ?: return
        val id = nearest.holdId
        val current = state.startHoldIds // Set<Int>
        val updated = if (id in current) {
            current - id
        } else if (current.size < 2) {
            current + id
        } else return
        _uiState.update { it.copy(startHoldIds = updated) }
    }

    // 탑 홀드 선택 토글
    fun toggleTopHold(x: Float, y: Float) {
        Log.d("StepCheck", "toggleTopHold")
        val state = _uiState.value
        val selectedHolds =
            state.holdCoordinates.filter { it.holdId in state.selectedHoldIds && it.holdId !in state.startHoldIds }
        val nearest = selectedHolds.minByOrNull { hold ->
            val dx = hold.average!!.x - x
            val dy = hold.average!!.y - y
            dx * dx + dy * dy
        } ?: return
        val toggledId = if (state.topHoldId == nearest.holdId) null else nearest.holdId
        _uiState.update { it.copy(topHoldId = toggledId) }
    }

    // 홀드 선택 토글
    fun toggleSelectedHold(x: Float, y: Float) {
        Log.d("StepCheck", "toggleSelectedHold")
        val state = _uiState.value
        val nearest = state.holdCoordinates
            .minByOrNull { hold ->
                val dx = hold.average!!.x - x
                val dy = hold.average!!.y - y
                dx * dx + dy * dy
            } ?: return
        val current = state.selectedHoldIds // Set<Int>
        val id = nearest.holdId

        // 너무 거리가 먼 좌표 제외
//        val distanceSquared = nearest?.let { (it.x - x) * (it.x - x) + (it.y - y) * (it.y - y) }
//        val id = if (distanceSquared != null && distanceSquared <= 100 * 100) {
//            nearest.id
//        } else null ?: return

        val updated = if (id in current) {
            current - id
        } else {
            current + id
        }
        _uiState.update { it.copy(selectedHoldIds = updated) }
    }

    fun setColor(color: String) {
        _uiState.update {
            it.copy(
                choiceColor = color
            )
        }
    }

    fun setLevel(level: String) {
        _uiState.update {
            it.copy(
                level = level
            )
        }
    }

    fun addGridPoint(x: Float, y: Float) {
        val currentPoints = _uiState.value.gridPoints
        val updatedPoints = if (currentPoints.size < 2) {
            currentPoints + Offset(x, y)
        } else {
            currentPoints.drop(1) + Offset(x, y)
        }
        _uiState.update {
            it.copy(gridPoints = updatedPoints)
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _uiState.update {
            it.copy(
                lat = lat,
                lng = lng
            )
        }
    }

    // 문제 등록
    fun postProblem(onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        val state = _uiState.value
        // selected에 포함된 Hold만 추출
        val selectedHolds = state.holdCoordinates
            .filter { state.selectedHoldIds.contains(it.holdId) }

        val selected = selectedHolds.map {
            HoldCoordinates(it.holdId, it.coordinates)
        }
        val xValues = selectedHolds.map { it.average!!.x }
        val yValues = selectedHolds.map { it.average!!.y }

        val widthDiff = (xValues.maxOrNull() ?: 0) - (xValues.minOrNull() ?: 0)
        val heightDiff = (yValues.maxOrNull() ?: 0) - (yValues.minOrNull() ?: 0)

        // 스타트 홀드 Id
        val startHold = state.startHoldIds.toList()
        // 탑 홀드 Id
        val endHold = listOfNotNull(state.topHoldId)
        val color = state.choiceColor
        val level = state.level
        val points = state.gridPoints
        val diagonalDistance = if (points.size >= 2) {
            val dx = points[1].x - points[0].x
            val dy = points[1].y - points[0].y
            kotlin.math.sqrt(dx * dx + dy * dy).toInt()
        } else {
            0
        }
        val lat = state.lat
        val lng = state.lng

        // 위도, 경도, 색상, 레벨 변경 필요
        val request = ProblemRequest(
            latitude = lat,
            longitude = lng,
            heightDiff = heightDiff,
            widthDiff = widthDiff,
            selected = selected,
            startHold = startHold,
            endHold = endHold,
            choiceColor = color,
            level = level,
            pixelGrid = diagonalDistance,
            imageName = _previousUrl.value.toString()
        )

        viewModelScope.launch {
            val result = problemRepository.postProblem(request)

            if (result.isSuccess) {
                val problemResponses = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    problemId = problemResponses?.data?.problemId,
                    categoryId = problemResponses?.data?.categoryId
                )
                // 성공시 콜백 실행
                onSuccess()
            } else {
                // 실패 처리
                Log.d("APILog", "실패: ${result.exceptionOrNull()}")
            }
        }
    }

    // 다음 단계로 이동
    fun goToNextStep() {
        if (!canGoToNextStep()) return
        val nextStep = when (_holdStepState.value.currentStep) {
            SelectHoldStep.SELECT_GRID -> SelectHoldStep.DETECT_HOLDS
            SelectHoldStep.DETECT_HOLDS -> SelectHoldStep.SELECT_START
            SelectHoldStep.SELECT_START -> SelectHoldStep.SELECT_TOP
            SelectHoldStep.SELECT_TOP -> SelectHoldStep.SELECT_COLOR
            SelectHoldStep.SELECT_COLOR -> SelectHoldStep.SELECT_LEVEL
            SelectHoldStep.SELECT_LEVEL -> SelectHoldStep.COMPLETE
            SelectHoldStep.COMPLETE -> SelectHoldStep.COMPLETE
        }
        Log.d("SelectHold", "Next step: $nextStep")
        _holdStepState.update {
            it.copy(
                currentStep = nextStep,
                guideMessage = updateGuideMessage(nextStep),
                warningMessage = null // 이동 시 경고 초기화
            )
        }
    }

    // 이전 단계로 이동
    fun goToPreviousStep() {
        val prevStep = when (_holdStepState.value.currentStep) {
            SelectHoldStep.DETECT_HOLDS -> SelectHoldStep.SELECT_GRID
            SelectHoldStep.SELECT_START -> SelectHoldStep.DETECT_HOLDS
            SelectHoldStep.SELECT_TOP -> SelectHoldStep.SELECT_START
            SelectHoldStep.SELECT_COLOR -> SelectHoldStep.SELECT_TOP
            SelectHoldStep.SELECT_LEVEL -> SelectHoldStep.SELECT_COLOR
            SelectHoldStep.COMPLETE -> SelectHoldStep.SELECT_LEVEL
            SelectHoldStep.SELECT_GRID -> SelectHoldStep.SELECT_GRID
        }
        // 단계 변경
        _holdStepState.update {
            it.copy(
                currentStep = prevStep,
                guideMessage = updateGuideMessage(prevStep),
                warningMessage = null // 이동 시 경고 초기화
            )
        }
        _uiState.update {
            it.copy(
                // 이전 단계 관련 값 초기화
                selectedHoldIds = if (prevStep < SelectHoldStep.DETECT_HOLDS) emptySet() else it.selectedHoldIds,
                startHoldIds = if (prevStep < SelectHoldStep.SELECT_START) emptySet() else it.startHoldIds,
                topHoldId = if (prevStep < SelectHoldStep.SELECT_TOP) null else it.topHoldId,
                choiceColor = if (prevStep < SelectHoldStep.SELECT_COLOR) null else it.choiceColor,
                level = if (prevStep < SelectHoldStep.SELECT_LEVEL) null else it.level
            )
        }
    }

    // 유효성 검사
    private fun canGoToNextStep(): Boolean {
        val state = _uiState.value
        val warning = when (_holdStepState.value.currentStep) {
            SelectHoldStep.SELECT_GRID ->
                if (state.gridPoints.size < 2) "암벽의 구멍 2개를 선택하세요." else null

            SelectHoldStep.DETECT_HOLDS ->
                if (state.selectedHoldIds.size < 3) "최소 3개의 홀드를 선택해 주세요." else null

            SelectHoldStep.SELECT_START ->
                if (state.startHoldIds.isEmpty()) "스타트 홀드를 선택해 주세요." else null

            SelectHoldStep.SELECT_TOP ->
                if (state.topHoldId == null) "탑 홀드를 선택해 주세요." else null

            SelectHoldStep.SELECT_COLOR ->
                if (state.choiceColor == null) "홀드 색상을 선택해 주세요." else null

            SelectHoldStep.SELECT_LEVEL ->
                if (state.level == null) "홀드 레벨을 선택해 주세요." else null

            else -> null
        }
        _holdStepState.update { it.copy(warningMessage = warning) }
        return warning == null // 검증 성공 여부 반환
    }

    // 안내 문구
    private fun updateGuideMessage(step: SelectHoldStep): String {
        return when (step) {
            SelectHoldStep.SELECT_GRID -> "암벽의 구멍 2개를 선택하세요."
            SelectHoldStep.DETECT_HOLDS -> "홀드를 선택해 주세요."
            SelectHoldStep.SELECT_START -> "스타트 홀드를 선택해 주세요."
            SelectHoldStep.SELECT_TOP -> "탑 홀드를 선택해 주세요."
            SelectHoldStep.SELECT_COLOR -> "홀드 색상을 선택해 주세요."
            SelectHoldStep.SELECT_LEVEL -> "레벨을 선택해 주세요."
            SelectHoldStep.COMPLETE -> "선택한 문제를 확인해 주세요"
            else -> ""
        }
    }

    // 색상 분류
    fun classifyColor(imageUrl: String, onSuccess: () -> Unit = {}) {
        val state = _uiState.value

        if (state.selectedHoldIds.isEmpty()) return

        val request = HoldClassifyRequest(
            imageUrl = imageUrl,
            selectedHoldIdList = state.selectedHoldIds.toList(),
            holds = state.holdCoordinates
        )

        viewModelScope.launch {
            _holdStepState.update { it.copy(isHoldLoaded = false) }
            val result = holdRepository.postClassifyHolds(request)
            Log.d("APILog", "API 호출 성공")

            if (result.isSuccess) {
                val holdClassifyResponses = result.getOrNull()
                Log.d("APILog", "응답: $holdClassifyResponses")
                _uiState.value = _uiState.value.copy(
                    selectedHoldIds = buildSet {
                        addAll(_uiState.value.selectedHoldIds)
                        addAll(holdClassifyResponses?.selected ?: emptySet())
                    }
                )
                // 성공시 콜백 실행
                _holdStepState.update { it.copy(isHoldLoaded = true) }
                onSuccess()
            } else {
                // 실패 처리
                Log.d("APILog", "실패: $result")
            }
        }
    }
}
