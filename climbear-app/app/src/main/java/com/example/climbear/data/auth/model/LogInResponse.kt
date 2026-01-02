package com.example.climbear.data.auth.model

data class LogInResponse(
    val status: String,
    val data: LogInData,
    val error: LogInError
)

data class LogInData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val email: String,
    val nickname: String,
    val isNewUser: Boolean
)

data class LogInError(
    val statusCode: Int,
    val message: String
)