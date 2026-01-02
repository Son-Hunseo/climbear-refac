package com.example.climbear.data.auth

import android.util.Log
import com.example.climbear.data.auth.api.AuthApi
import com.example.climbear.data.auth.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authApi: AuthApi, // 토큰 갱신용 Retrofit API
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 무한 루프 방지: 이미 재시도한 요청은 처리하지 않음
        if (responseCount(response) >= 2) {
            TokenManager.emitLoginRequired()
            return null
        }

        val refreshToken = TokenManager.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            TokenManager.emitLoginRequired()
            return null
        }

        val newToken = runBlocking {
            try {
                val request = RefreshTokenRequest(refreshToken)
                val result = authApi.postRefreshToken("Bearer $refreshToken", request)

                if (result.isSuccessful) {
                    val body = result.body()
                    val access = body?.data?.accessToken
                    val refresh = body?.data?.refreshToken

                    if (access != null && refresh != null) {
                        // 새로운 토큰 저장
                        TokenManager.saveTokens(access, refresh)
                        access
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        return if (newToken != null) {
            // 기존 요청에 새로운 토큰으로 Authorization 헤더 추가
            response.request.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newToken")
                .build()
        } else {
            // 재발급 실패 → 로그인 필요
            TokenManager.emitLoginRequired()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
