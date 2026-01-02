package com.example.climbear.ui.screen.userinput

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.UserPreferencesRepository
import com.example.climbear.data.user.UserRepository
import com.example.climbear.data.user.model.EditUserInfoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInputViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserInputUiState())
    val uiState: StateFlow<UserInputUiState> = _uiState

    fun onHeightInputChanged(input: String) {
        val parsed = input.toDoubleOrNull()
        val error = when {
            input.isEmpty() -> "키를 입력해 주세요."
            parsed == null -> "숫자를 입력해 주세요."
            parsed < 120.0 -> "120이상 입력해 주세요."
            else -> null
        }

        val currentReachError = _uiState.value.reachError
        _uiState.update {
            it.copy(
                heightInput = input,
                heightError = error,
                canSave = error == null && currentReachError == null
            )
        }
    }

    fun onReachInputChanged(input: String) {
        val parsed = input.toDoubleOrNull()
        val error = when {
            input.isEmpty() -> "리치를 입력해 주세요."
            parsed == null -> "숫자를 입력해 주세요."
            parsed < 100.0 -> "100이상 입력해 주세요."
            else -> null
        }

        val currentHeightError = _uiState.value.heightError
        _uiState.update {
            it.copy(
                reachInput = input,
                reachError = error,
                canSave = error == null && currentHeightError == null
            )
        }
    }

    fun validateInput(): String? {
        val height = _uiState.value.heightInput.toDoubleOrNull()
        val reach = _uiState.value.reachInput.toDoubleOrNull()
        Log.d("userInput", "$height, $reach input")

        return when {
            height == null -> "키는 반드시 입력 되어야 합니다."
            reach == null -> "리치는 반드시 입력 되어야 합니다."
            height < 120.0 -> "키는 120 이상 입력 되어야 합니다."
            reach < 100.0 -> "리치는 100 이상 입력 되어야 합니다."
            else -> null
        }
    }

    fun onSave(isLoggedIn: Boolean, onSuccess: () -> Unit = {}) {
        val height = _uiState.value.heightInput.toDoubleOrNull()
        val reach = _uiState.value.reachInput.toDoubleOrNull()
        if (height != null && reach != null) {
            viewModelScope.launch {
                saveLocally(height, reach)
                if (isLoggedIn) sendToServer(height, reach)
                onSuccess()
            }
        } else {
            // null 일 때 처리
        }
    }

    private suspend fun saveLocally(height: Double, reach: Double) {
        // DataStore 저장
        userPreferencesRepository.savedUserDimensions(height, reach)
    }

    private suspend fun sendToServer(height: Double, reach: Double) {
        val request = EditUserInfoRequest(
            height = height,
            armSpan = reach
        )
        val result = userRepository.patchUserInfo(request)
        if (result.isSuccess) {
            val patchUserInfoResponses = result.getOrNull()
            // 성공 처리
            Log.d("User", "회원 정보 수정 성공 ${patchUserInfoResponses?.data}")
        } else {
            // 실패 처리
            Log.d("User", "회원 정보 수정 실패")
        }
    }

    fun setUserInputState(height: Double, reach: Double) {
        _uiState.update {
            it.copy(
                heightInput = height.toString(),
                reachInput = reach.toString(),
            )
        }
    }
}