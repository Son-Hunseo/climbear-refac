package com.example.climbear.data.user

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.auth.api.UserApi
import com.example.climbear.data.user.model.EditUserInfoRequest
import com.example.climbear.data.user.model.UserInfoResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApi
) {
    suspend fun getUserInfo(): Result<UserInfoResponse> {
        return try {
            val response = api.getUserInfo()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("no body"))
                }
            } else {
                Result.failure(Exception("Api error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun patchUserInfo(request: EditUserInfoRequest): Result<ApiResponse<String>> {
        return try {
            val response = api.patchUserInfo(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("no body"))
                }
            } else {
                Result.failure(Exception("Api error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}