package com.example.climbear.data.image

import com.example.climbear.data.image.api.ImageApi
import com.example.climbear.data.image.model.presignedUrlResponse
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val imageApi: ImageApi
) {
    suspend fun getPresignedUrl(fileName: String): Result<presignedUrlResponse> {
        return try {
            val response = imageApi.getPresignedUrl(fileName)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("url 없음"))
                }
            } else {
                Result.failure(Exception("Api Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}