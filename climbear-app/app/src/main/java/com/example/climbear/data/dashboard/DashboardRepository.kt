package com.example.climbear.data.dashboard

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.dashboard.api.DashboardApi
import com.example.climbear.data.dashboard.model.Rank
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    private val dashboardApi: DashboardApi
) {
    suspend fun getExp(): Result<ApiResponse<Rank>> {
        return try {
            val response = dashboardApi.getExp()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message))
                    }
                } else {
                    Result.failure(Exception("No Response"))
                }
            } else {
                Result.failure(Exception("API Fail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}