package com.example.climbear.ui.component.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R

@Preview
@Composable
fun RankCard(
    modifier: Modifier = Modifier,
    myRank: String = "WHITE",
    logoResId: Int = R.drawable.logo,
    color: Color = Color.Black,
    rankMessage: String = "현재 등급",
    lockResId: Int? = null
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0xFFBDBDD6),
                spotColor = Color(0xFFBDBDD6)
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(width = 2.dp, color = color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (lockResId != null) {
                            Modifier.alpha(0.5f)
                        } else Modifier
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(logoResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                )
                Text(rankMessage, fontSize = 8.sp)
                if (myRank.lowercase() == "white") {
                    Text(
                        text = myRank.uppercase(),
                        color = Color(0xFF6468AB) // 반드시 #6468AB로 한 번만 출력
                    )
                } else {
                    Text(
                        text = myRank.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
            if (lockResId != null)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),

                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(lockResId),
                        contentDescription = null,
                    )
                }
        }
    }
}
