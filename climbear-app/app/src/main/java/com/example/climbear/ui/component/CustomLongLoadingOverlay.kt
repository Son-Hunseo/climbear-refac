package com.example.climbear.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.getZenDotsFontFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CustomLongLoadingOverlay(
    message: String = "LOADING..."
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000).copy(alpha = 0.8f))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingImageSequenceWithFade(
                images = listOf(
                    R.drawable.loading_01,
                    R.drawable.loading_02,
                    R.drawable.loading_03,
                    R.drawable.loading_04,
                    R.drawable.loading_05,
                    R.drawable.loading_06
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedLoadingText(message = message)
        }
    }
}

@Composable
fun LoadingImageSequenceWithFade(
    images: List<Int>,
    modifier: Modifier = Modifier,
    durationMillis: Int = 5000
) {
    var currentIndex by remember { mutableStateOf(Random.nextInt(images.size)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(durationMillis.toLong())
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Crossfade(targetState = currentIndex, modifier = modifier) { index ->
        Image(
            painter = painterResource(id = images[index]),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
    }
}

@Composable
fun AnimatedLoadingText(
    message: String = "LOADING..."
) {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = message,
        fontSize = 30.sp,
        color = Color.White.copy(alpha = alpha),
        modifier = Modifier
            .offset(y = offsetY.dp),
        fontFamily = getZenDotsFontFamily()
    )
}

@Preview(showBackground = true)
@Composable
fun CustomLongLoadingOverlayPreview() {
    ClimbearTheme {
        CustomLongLoadingOverlay()
    }
}
