package com.example.climbear.data.problem.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.problem.model.ProblemData
import com.example.climbear.data.problem.model.ProblemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProblemApi {
    // 회원정보 조회
    @POST("api/v1/problems")
    suspend fun postProblem(@Body request: ProblemRequest): Response<ApiResponse<ProblemData>>
}