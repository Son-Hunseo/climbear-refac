package com.example.climbear.ui.component.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape

@Preview
@Composable
fun ImageNavigationButton(
    imageResId: Int = R.drawable.logo,
    text: String = "이미지 네비게이션 버튼",
    onClick: () -> Unit = {},
    fontSize: TextUnit = 12.sp,
    height: Dp = 45.dp,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp), // 버튼 모양에 맞게 조절
                ambientColor = Color(0xFFBDBDD6),
                spotColor = Color(0xFFBDBDD6)
            ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp // 기본 elevation을 제거해야 중첩 방지
        ),
        contentPadding = PaddingValues(start = 8.dp, end = 2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0x14, 0x14, 0x78)
        )
    )
    {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 8.dp)
                )
                Text(
                    text = text,
                    fontSize = fontSize
                )
            }
            Image(
                painter = painterResource(R.drawable.home_right_arrow),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )
        }
    }
}