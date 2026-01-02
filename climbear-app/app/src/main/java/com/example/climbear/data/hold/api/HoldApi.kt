package com.example.climbear.data.hold.api

import com.example.climbear.data.hold.model.HoldClassifyRequest
import com.example.climbear.data.hold.model.HoldClassifyResponse
import com.example.climbear.data.hold.model.HoldRequest
import com.example.climbear.data.hold.model.HoldResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HoldApi {
    @POST("ai/v1/hold")
    suspend fun postHold(@Body request: HoldRequest): Response<List<HoldResponse>>

    @POST("ai/v1/hold/classify")
    suspend fun postClassifyHolds(@Body request: HoldClassifyRequest): Response<HoldClassifyResponse>
}