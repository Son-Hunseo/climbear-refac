package com.example.climbear.ui.screen.playvideo

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.climbear.R
import com.example.climbear.ui.component.CustomCameraMessage
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.selecthold.SelectHoldStep
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun PlayVideoScreen(
    onHomeButtonClicked: () -> Unit = {},
    moveToSelectHold: () -> Unit = {},
    moveToPrev: () -> Unit = {},
    sharedMediaUriViewModel: SharedMediaUriViewModel
) {
    Scaffold(
        topBar = {
            CustomToolBar(
                onLogoClick = onHomeButtonClicked
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            PlayVideodMain(
                sharedMediaUriViewModel = sharedMediaUriViewModel
            )

            PlayVideoBottomBar(
                navigateToBack = moveToPrev,
                navigateToNext = moveToSelectHold
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlayVideodMain(
    modifier: Modifier = Modifier,
    sharedMediaUriViewModel: SharedMediaUriViewModel
) {
    val uri = sharedMediaUriViewModel.videoUri
    Log.i("PlayVideo", "uri = ${uri}")
    uri?.let {
        val localContext = LocalContext.current

        val exoPlayer = remember {
            ExoPlayer.Builder(localContext).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                playWhenReady = true
            }
        }
        Box(
            modifier = modifier
//                .fillMaxSize()
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clipToBounds()
        ) {
            AndroidView(
                factory = {
                    PlayerView(localContext).apply {
                        player = exoPlayer
                        useController = true
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}

@Composable
fun PlayVideoBottomBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF141478),
    message: String = "다음 단계를 진행해 주세요.",
    navigateToBack: () -> Unit = {},
    navigateToNext: () -> Unit = {},
) {
    // 하단 버튼
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 안내 메시지
        CustomCameraMessage(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            message = message,
            backgroundColor = backgroundColor,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 이전 & 다음 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // 좌측 버튼
            IconButton(
                onClick = { navigateToBack() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
//                    .background(Color(0xFFFFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color(0xFF141478),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            // 중간 버튼 (비활성화)
            Button(
                onClick = { },
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFFFFFFFF)
                ),
                enabled = false
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.centerbar),
                    contentDescription = "Center Bar",
                    modifier = Modifier
                        .width(120.dp)
                        .padding(4.dp),
                    tint = Color(0xFF141478)
                )
            }

            // 우측 버튼
            IconButton(
                onClick = { navigateToNext() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
//                    .background(Color(0xFFFFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color(0xFF141478),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun PlayVideoPreview() {
    ClimbearTheme {
        PlayVideoScreen(sharedMediaUriViewModel = viewModel())
    }
}