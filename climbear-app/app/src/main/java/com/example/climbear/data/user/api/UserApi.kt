package com.example.climbear.data.auth.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.user.model.EditUserInfoRequest
import com.example.climbear.data.user.model.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {
    // 회원 정보 조회
    @GET("api/v1/users/me")
    suspend fun getUserInfo(): Response<UserInfoResponse>

    // 회원 정보 수정
    @PATCH("api/v1/users/info")
    suspend fun patchUserInfo(@Body request: EditUserInfoRequest): Response<ApiResponse<String>>
}