package com.example.climbear.data.auth

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 토큰을 가져오고, null이 아닌 경우에만 Authorization 헤더에 추가
        val authToken = TokenManager.accessToken.value
        val newRequest = if (!shouldExcludeAuth(request.url) && authToken != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}

private fun shouldExcludeAuth(url: HttpUrl): Boolean {
    val path = url.encodedPath
    return path == "/api/v1/auth/kakao/login"
}
