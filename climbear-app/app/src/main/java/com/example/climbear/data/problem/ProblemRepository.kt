package com.example.climbear.data.problem

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.problem.api.ProblemApi
import com.example.climbear.data.problem.model.ProblemData
import com.example.climbear.data.problem.model.ProblemRequest
import javax.inject.Inject

class ProblemRepository @Inject constructor(
    private val api: ProblemApi
) {
    suspend fun postProblem(request: ProblemRequest): Result<ApiResponse<ProblemData>> {
        return try {
            val response = api.postProblem(request)
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