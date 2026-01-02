package com.example.climbear.data.auth.model

data class RefreshTokenResponse(
    val status: String,
    val data: RefreshTokenData,
    val error: RefreshTokenError
)

data class RefreshTokenData(
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenError(
    val statusCode: Int,
    val message: String
)