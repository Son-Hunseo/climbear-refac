package com.example.climbear.data.record.model

/**
 * 단일 메시지 응답
 */
data class RecordResponse(
    val message: String
)

data class ErrorDetail(
    val statusCode: Int,
    val message: String
)

data class ApiResponse<T>(
    val status: String,
    val data: T?,
    val error: ErrorDetail?
)

data class FailResponse(
    val message: String,
    val seq: Int?,
    val value: Any?
)
