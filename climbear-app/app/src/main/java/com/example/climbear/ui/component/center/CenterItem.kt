package com.example.climbear.ui.component.center

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CenterItem(
    modifier: Modifier = Modifier,
    name: String,
    distance: String = "",
    address: String,
    isMy: Boolean = true,
    isSelected: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF2F2FB) else Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 55.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF141478)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF141478)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141478)
                    )


                }
                if (isMy) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .background(
                                color = Color(0xFF141478),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 8.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 8.dp
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MY",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (isSelected && isMy) {
                Text(
                    text = "최근 방문 기록",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF141478),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 4.dp)
                )
                content()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}