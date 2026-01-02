package com.example.climbear.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomOnOffButton(
    activeImageRes: Int,
    inactiveImageRes: Int,
    contentDescription: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    onClick: () -> Unit = {}
) {
    val imageRes = if (active) activeImageRes else inactiveImageRes

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size),
        )
    }
}

@Preview
@Composable
fun CustomOnOffButtonPreview() {
    ClimbearTheme {
        CustomOnOffButton(
            activeImageRes = R.drawable.capture_active,
            inactiveImageRes = R.drawable.capture,
            contentDescription = "촬영",
            active = false
        )
        CustomOnOffButton(
            activeImageRes = R.drawable.record_active,
            inactiveImageRes = R.drawable.record,
            contentDescription = "녹화",
            active = false
        )
        CustomOnOffButton(
            activeImageRes = R.drawable.live_activate,
            inactiveImageRes = R.drawable.live_deactivate,
            contentDescription = "라이브",
            active = false,
            size = 40.dp
        )
        CustomOnOffButton(
            activeImageRes = R.drawable.light_activate,
            inactiveImageRes = R.drawable.light_deactivate,
            contentDescription = "플래시",
            active = false,
            size = 40.dp
        )
        CustomOnOffButton(
            activeImageRes = R.drawable.done,
            inactiveImageRes = R.drawable.select,
            contentDescription = "홀드 수동 조정",
            active = false,
            size = 40.dp
        )
    }
}