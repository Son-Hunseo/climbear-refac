package com.example.climbear.ui.screen.userlogin

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climbear.data.auth.AuthRepository
import com.example.climbear.data.auth.TokenManager
import com.example.climbear.data.auth.model.LogInRequest
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class UserLogInViewModel : ViewModel() {

    // Repository
    private val repository = AuthRepository()

    // 로그인
    fun logIn(kakaoAccessToken: String, onSuccess: () -> Unit = {}) {
        val request = LogInRequest(accessToken = kakaoAccessToken)

        viewModelScope.launch {
            val result = repository.postLogIn(request)
            if (result.isSuccess) {
                val logInResponses = result.getOrNull()

                // 로그인 성공 시 토큰 저장
                val accessToken = logInResponses?.data?.accessToken
                val refreshToken = logInResponses?.data?.refreshToken
                if (accessToken != null && refreshToken != null) {
                    TokenManager.saveTokens(accessToken, refreshToken)
                }
                onSuccess() // 성공 시 회원 정보 조회 userInfoViewModel.getUserInfo(navigateToHome)
            } else {
                // 실패 처리
            }
        }
    }

    // 로그아웃
    fun logOut(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val refreshToken = TokenManager.getRefreshToken()
            val result = repository.postLogOut(refreshToken)
            if (result.isSuccess) {
                // 로그아웃 성공 시 토큰 초기화
                TokenManager.clearTokens()
                onSuccess()  // 성공 시 로그인 상태를 로그아웃으로 변경 userInfoViewModel.setLoginStatusFalse()
            } else {
                // 실패 처리
            }
        }
    }

   // 카카오 로그인
    fun kakaoLogin(context: Context, onResult: (OAuthToken?, Throwable?) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    // fallback to account login
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = onResult)
                } else {
                    onResult(token, null)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = onResult)
        }
    }

    // 로그인 성공 or 실패 콜백 함수
    fun handleKakaoLoginResult(
        token: OAuthToken?,
        error: Throwable?,
        onLoginSuccess: () -> Unit = {}
    ) {
        when {
            token != null -> {
                Log.d("KakaoLogin", "카카오 로그인 성공")
                logIn(token.accessToken, onSuccess = onLoginSuccess)
            }
            error != null -> {
                Log.e("KakaoLogin", "로그인 실패", error)
            }
            else -> {
                Log.w("KakaoLogin", "알 수 없는 상태")
            }
        }
    }
}