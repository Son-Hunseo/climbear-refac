package com.example.climbear.data.center.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.center.model.CenterData
import com.example.climbear.data.center.model.CenterMyData
import retrofit2.Response
import retrofit2.http.GET

interface CenterApi {
    @GET("api/v1/centers/list")
    suspend fun getCenterList(): Response<ApiResponse<List<CenterData>>>

    @GET("api/v1/centers/my")
    suspend fun getCenterMy(): Response<ApiResponse<List<CenterMyData>>>
}