package com.example.climbear.data

data class ApiResponse<T> (
    val status: String,
    val data: T?,
    val error: ApiError?
)

data class ApiError (
    val statusCode: Int,
    val message: String
)