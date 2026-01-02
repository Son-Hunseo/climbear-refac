package com.example.climbear.ui.component.solutionhint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun SolutionTabBar(
    modifier: Modifier = Modifier,
    selectedTab: String = "hint",
    isLogLoaded: Boolean = false,
    onSelectHint: () -> Unit = {},
    onSelectLog: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF2F2FB)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabWidth = 120.dp

            // 힌트 탭
            Button(
                onClick = onSelectHint,
                modifier = Modifier
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "hint") Color.White else Color.Transparent,
                    contentColor = Color(0xFF141478)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("힌트")
            }

            // 로그 탭
            Button(
                onClick = onSelectLog,
                enabled = isLogLoaded,
                modifier = Modifier
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "holdLog") Color.White else Color.Transparent,
                    contentColor = Color(0xFF141478),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Gray
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (isLogLoaded) "로그" else "로딩 중",
                    maxLines = 1
                )
            }
        }
    }
}


@Preview(
//    showBackground = true,
)
@Composable
fun SolutionTabBarPreview() {
    ClimbearTheme {
        SolutionTabBar()
    }
}