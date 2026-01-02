package com.example.climbear.ui.screen.userinput

data class UserInputUiState(
    val heightInput: String = "",
    val reachInput: String = "",
    val heightError: String? = null,
    val reachError: String? = null,
    val canSave: Boolean = false
)