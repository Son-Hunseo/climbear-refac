package com.example.climbear.data.center

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.RetrofitClient
import com.example.climbear.data.center.api.CenterApi
import com.example.climbear.data.center.model.CenterData
import com.example.climbear.data.center.model.CenterMyData
import javax.inject.Inject

class CenterRepository @Inject constructor(
    private val centerApi: CenterApi
) {
    suspend fun getCenterList(): Result<ApiResponse<List<CenterData>>> {
        return try {
            val response = centerApi.getCenterList()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message ?: "api 통신 실패"))
                    }
                } else {
                    Result.failure(Exception("No response"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCenterMy(): Result<ApiResponse<List<CenterMyData>>> {
        return try {
            val response = centerApi.getCenterMy()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message ?: "api 통신 실패"))
                    }
                } else {
                    Result.failure(Exception("No response"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}