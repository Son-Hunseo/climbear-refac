package com.example.climbear.data.user.model

data class UserInfoResponse(
    val status: String,
    val data: UserInfoData?,
    val error: UserInfoError?
)

data class UserInfoData(
    val email: String,
    val nickname: String,
    val height: Double,
    val armSpan: Double,
)

data class UserInfoError(
    val statusCode: Int,
    val message: String
)