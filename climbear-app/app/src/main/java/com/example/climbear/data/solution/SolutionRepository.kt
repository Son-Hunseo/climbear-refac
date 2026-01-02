package com.example.climbear.data.solution

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.solution.api.SolutionApi
import com.example.climbear.data.solution.model.SolutionData
import javax.inject.Inject

class SolutionRepository @Inject constructor(
    private val api: SolutionApi
) {
    suspend fun getSolution(problemId: Int): Result<ApiResponse<SolutionData>> {
        return try {
            val response = api.getSolution(problemId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message))
                    }
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