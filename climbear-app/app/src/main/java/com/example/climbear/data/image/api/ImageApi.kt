package com.example.climbear.data.image.api

import com.example.climbear.data.image.model.presignedUrlResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {
    @GET("ai/v1/image/presigned-url")
    suspend fun getPresignedUrl(@Query("filename") fileName: String): Response<presignedUrlResponse>
}