package com.example.climbear.data.hold

import com.example.climbear.data.hold.api.HoldApi
import com.example.climbear.data.hold.model.HoldClassifyRequest
import com.example.climbear.data.hold.model.HoldClassifyResponse
import com.example.climbear.data.hold.model.HoldRequest
import com.example.climbear.data.hold.model.HoldResponse
import javax.inject.Inject

class HoldRepository @Inject constructor(
    private val api: HoldApi
) {
    suspend fun postHold(request: HoldRequest): Result<List<HoldResponse>> {
        return try {
            val response = api.postHold(request)
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

    suspend fun postClassifyHolds(request: HoldClassifyRequest): Result<HoldClassifyResponse> {
        return try {
            val response = api.postClassifyHolds(request)
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