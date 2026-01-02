package com.example.climbear.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.getZenDotsFontFamily
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun CustomLoadingOverlay(
    message: String = "LOADING..."
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF000000).copy(alpha = 0.8f)
            ),
        contentAlignment = Alignment.Center
    ) {
        CustomLoadingLogo(message = message)
    }
}

@Composable
fun CustomLoadingLogo(
    message: String = "LOADING..."
) {
    val imageA = painterResource(R.drawable.logo_white_scratch)
    val imageB = painterResource(R.drawable.logo_white_hand)

    // ----------- 애니메이션 속도(시간) 조정 -----------
    val animationDurationA = 1100 // 이미지 a 애니메이션 속도(ms)
    val animationDurationB = 1000 // 이미지 b 애니메이션 속도(ms)
    // -----------------------------------------------

    val imageHeight = 200.dp
    val moveRange = imageHeight.value * 0.3f // 이동 범위(dp)

    val offsetY = remember { Animatable(0f) }
    val revealFraction = remember { Animatable(0f) }

    // --- 두 애니메이션을 항상 동시에 시작 ---
    LaunchedEffect(Unit) {
        while (true) {
            // 동시에 시작
            launch {
                offsetY.animateTo(
                    targetValue = moveRange,
                    animationSpec = tween(durationMillis = animationDurationB, easing = LinearEasing)
                )
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = animationDurationB, easing = LinearEasing)
                )
            }
            launch {
                revealFraction.snapTo(0f)
                revealFraction.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = animationDurationA, easing = LinearEasing)
                )
            }
            // 두 애니메이션이 끝날 때까지 대기
            delay(maxOf(animationDurationA, animationDurationB) * 2L)
        }
    }
    // --- 동시 시작 부분 끝 ---

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ------------------- 겹치게 만드는 부분 -------------------
        Box(
            modifier = Modifier
                .size(imageHeight)
                .clipToBounds()
        ) {
            // 위에 위치할 이미지 A (마스킹 애니메이션)
            Image(
                painter = imageA,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        clip = true
                        alpha = 1f
                        scaleY = revealFraction.value
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    }
            )
            // 아래에 위치할 이미지 B (이동 애니메이션)
            Image(
                painter = imageB,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetY.value.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 30.sp,
            color = Color.White,
            fontFamily = getZenDotsFontFamily()
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomLoadingLogoPreview() {
    ClimbearTheme {
        CustomLoadingOverlay()
    }
}
