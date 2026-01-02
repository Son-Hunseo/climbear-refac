package com.example.climbear.data.dashboard.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.dashboard.model.Rank
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApi {
    @GET("api/v1/users/exp")
    suspend fun getExp(): Response<ApiResponse<Rank>>
}