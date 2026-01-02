package com.example.climbear.ui.component.selectHold

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.ui.screen.selecthold.HoldSelectUiState
import com.example.climbear.util.loadGrades

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    uiState: HoldSelectUiState,
) {
    val context = LocalContext.current
    val grades = loadGrades(context)
    val logos = listOf(
        R.drawable.logo_big_white,
        R.drawable.logo_big_yellow,
        R.drawable.logo_big_orange,
        R.drawable.logo_big_green,
        R.drawable.logo,
        R.drawable.logo_big_red,
        R.drawable.logo_big_purple,
        R.drawable.logo_big_gray,
        R.drawable.logo_big_pink
    )
    val levelTexts = listOf(
        "VB",
        "V0",
        "V1-2",
        "V2-3",
        "V3-4",
        "V4-5",
        "V5-6",
        "V6-7",
        "V7-8",
    )
    val levelImages = listOf(
        R.drawable.level_white,
        R.drawable.level_yellow,
        R.drawable.level_orange,
        R.drawable.level_green,
        R.drawable.level_blue,
        R.drawable.level_red,
        R.drawable.level_purple,
        R.drawable.level_gray,
        R.drawable.level_pink
    )

    val colorToLevelMap: Map<String, Int> = grades.associate { it.color.uppercase() to it.level }
    val problemColorIndex = colorToLevelMap[uiState.choiceColor] ?: 4
    val problemLevelIndex = colorToLevelMap[uiState.level] ?: 4

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.9f),
            Color.White.copy(alpha = 0.7f),
            Color.White.copy(alpha = 0.4f),
        ),
        startY = Float.POSITIVE_INFINITY,
        endY = 0f
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ){
        // 홀드 색상 확인
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(brush = gradient, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(120.dp)
        ) {
            Text(
                text = "Hold Color",
                color = Color(0xFF141478),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Image(
                painter = painterResource(logos[problemColorIndex]),
                contentDescription = "logo",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = uiState.choiceColor ?: "",
                color = Color(0xFF141478),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
        // 레벨 확인
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .background(brush = gradient, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(120.dp)
        ) {
            Text(
                text = "Level",
                color = Color(0xFF141478),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Image(
                painter = painterResource(levelImages[problemLevelIndex]),
                contentDescription = "logo",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = levelTexts[problemLevelIndex],
                color = Color(0xFF141478),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ConfirmDialogPreview() {
    val dummyUiState = HoldSelectUiState(
        choiceColor = "BLUE",
        level = "BLUE"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color(0xFFEFEFEF)
    ) {
        ConfirmDialog(uiState = dummyUiState)
    }
}
