package com.example.climbear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.climbear.R

// Preview일 때는 default 폰트 적용
@Composable
fun getZenDotsFontFamily(): FontFamily {
    val isPreview = LocalInspectionMode.current
    return if (isPreview) {
        FontFamily.Default
    } else {
        FontFamily(
            Font(R.font.zendots_regular)
        )
    }
}

@Composable
fun getPretendardFontFamily(): FontFamily {
    val isPreview = LocalInspectionMode.current
    return if (isPreview) {
        FontFamily.Default
    } else {
        FontFamily(
            Font(R.font.pretendard_light, FontWeight.Light),
            Font(R.font.pretendard_regular, FontWeight.Normal),
            Font(R.font.pretendard_medium, FontWeight.Medium),
            Font(R.font.pretendard_semibold, FontWeight.SemiBold),
            Font(R.font.pretendard_bold, FontWeight.Bold)
        )
    }
}