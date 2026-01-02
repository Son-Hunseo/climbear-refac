package com.example.climbear.data.auth.api

import com.example.climbear.data.auth.model.LogInRequest
import com.example.climbear.data.auth.model.LogInResponse
import com.example.climbear.data.auth.model.RefreshTokenRequest
import com.example.climbear.data.auth.model.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    // 로그인
    @POST("api/v1/auth/kakao/login")
    suspend fun postLogIn(@Body request: LogInRequest): Response<LogInResponse>

    // 로그아웃
    @POST("api/v1/auth/logout")
    suspend fun postLogOut(@Header("Authorization") token: String?): Response<Unit>

    // 토큰 재발급
    @POST("api/v1/auth/refresh")
    suspend fun postRefreshToken(@Header("Authorization") token: String?, @Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}