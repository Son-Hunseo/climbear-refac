package com.example.climbear.ui.screen.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.UserPreferencesRepository
import com.example.climbear.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userUiState = MutableStateFlow(UserInfoUiState())
    val userUiState: StateFlow<UserInfoUiState> = _userUiState

    init {
        loadLocalUserInfo()
    }

    // 회원정보 조회
    fun getUserInfo(onSuccess: () -> Unit = {}, onRequiredMoreInfo: () -> Unit = {}) {
        viewModelScope.launch {
            val result = userRepository.getUserInfo()
            if (result.isSuccess) {
                val userInfoResponses = result.getOrNull()

                // 회원 정보 조회 성공 시 로그인
                _userUiState.value = _userUiState.value.copy(
                    isLoggedIn = true,
                    email = userInfoResponses?.data?.email,
                    nickname = userInfoResponses?.data?.nickname,
                    height = userInfoResponses?.data?.height.takeIf { it != 0.0 },
                    armSpan = userInfoResponses?.data?.armSpan.takeIf { it != 0.0 }
                )
                // 키, 리치 서버 정보 없을 때, 로컬 정보 로드
                if (userInfoResponses?.data == null || userInfoResponses.data.height == 0.0 || userInfoResponses.data.armSpan == 0.0) {
                    onRequiredMoreInfo()
                } else {
                    onSuccess()
                }
                Log.d("logIn", "회원정보 조회 성공")
            } else {
                // 실패 처리
                Log.d("logIn", "회원정보 조회 실패")
            }
        }
    }

    // 로그인 상태 변경
    fun setLoginStateFalse() {
        _userUiState.value = _userUiState.value.copy(
            isLoggedIn = false,
            email = null,
            nickname = null
        )
    }

    // 사용자 정보 초기화
    fun resetUserInfo() {
        _userUiState.value = _userUiState.value.copy(
            isLoggedIn = false,
            email = null,
            nickname = null,
            height = null,
            armSpan = null
        )
    }

    // 사용자 키, 리치 로드 (DataStore)
    private fun loadLocalUserInfo() {
        viewModelScope.launch {
            userPreferencesRepository.userDimensions.collect { data ->
                _userUiState.value = _userUiState.value.copy(
                    height = data.height,
                    armSpan = data.armSpan
                )
            }
        }
    }
}