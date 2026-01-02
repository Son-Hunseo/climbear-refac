package com.example.climbear.data.local.model

import androidx.compose.ui.graphics.Color

data class LogoData(
    val color: Color,
    val smallLogoResId: Int,
    val bigLogoResId: Int,
    val nextResId: Int?,
    val arrowResId: Int?,
)
