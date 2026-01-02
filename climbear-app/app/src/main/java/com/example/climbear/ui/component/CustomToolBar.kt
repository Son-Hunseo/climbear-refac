package com.example.climbear.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.ZenDots
import com.example.climbear.ui.theme.getZenDotsFontFamily

@Composable
fun CustomToolBar (
    modifier: Modifier = Modifier,
    startSlot: (@Composable () -> Unit)? = null,
    endSlot: (@Composable () -> Unit)? = null,
    onLogoClick: () -> Unit = {}
) {
    val image = painterResource(R.drawable.logo)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 20.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomStart
        ) {
            startSlot?.invoke()
        }
        // 로고
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onLogoClick,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "CLIMBEAR",
                        fontSize = 20.sp,
                        fontFamily = getZenDotsFontFamily()
                        // fontFamily = ZenDots
                    )
                }
            }
        }
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomEnd
        ) {
            endSlot?.invoke()
        }
    }
}

@Composable
fun TempButton(
    onButtonClick: () -> Unit = {}
) {
    Button(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDCDCDC),
            contentColor = Color.Black
        ),
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .background(color = Color(0xFFDCDCDC))
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomToolBarPreview() {
    ClimbearTheme {
        CustomToolBar(
            startSlot = { TempButton() },
            endSlot = { TempButton() }
        )
    }
}