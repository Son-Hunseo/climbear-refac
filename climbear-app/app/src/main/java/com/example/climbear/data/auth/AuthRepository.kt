package com.example.climbear.data.auth

import com.example.climbear.data.RetrofitClient
import com.example.climbear.data.auth.api.AuthApi
import com.example.climbear.data.auth.model.LogInRequest
import com.example.climbear.data.auth.model.LogInResponse
import com.example.climbear.data.auth.model.RefreshTokenRequest
import com.example.climbear.data.auth.model.RefreshTokenResponse
import com.example.climbear.data.hold.api.HoldApi
import com.example.climbear.data.hold.model.HoldClassifyRequest
import com.example.climbear.data.hold.model.HoldClassifyResponse
import com.example.climbear.data.hold.model.HoldRequest
import com.example.climbear.data.hold.model.HoldResponse

class AuthRepository {
    private val api = RetrofitClient.create(AuthApi::class.java)

    suspend fun postLogIn(request: LogInRequest): Result<LogInResponse> {
        return try {
            val response = api.postLogIn(request)
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

    suspend fun postLogOut(refreshToken: String?): Result<Unit> {
        return try {
            val response = api.postLogOut("Bearer $refreshToken")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Api error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}