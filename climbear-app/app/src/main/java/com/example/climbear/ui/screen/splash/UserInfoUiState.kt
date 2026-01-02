package com.example.climbear.ui.screen.splash

data class UserInfoUiState(
    val isLoggedIn: Boolean = false,
    val email: String? = null,
    val nickname: String? = null,
    val height: Double? = null,
    val armSpan: Double? = null,
)