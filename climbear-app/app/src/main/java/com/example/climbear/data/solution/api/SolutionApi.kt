package com.example.climbear.data.solution.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.solution.model.SolutionData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SolutionApi {
    @GET("api/v1/solutions/problem/{problemId}")
    suspend fun getSolution(@Path("problemId") problemId: Int): Response<ApiResponse<SolutionData>>
}