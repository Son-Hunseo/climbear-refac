package com.example.climbear.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.example.climbear.ui.theme.getZenDotsFontFamily

@Composable
fun CustomLargeButton (
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val image = painterResource(R.drawable.logoscratch)

    Button(
        modifier = modifier,
        onClick = onButtonClick,
        enabled = enabled,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(130.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SAVE",
                fontSize = 18.sp,
                fontFamily = getZenDotsFontFamily()
                // fontFamily = ZenDots
            )
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomLargeButtonPreview() {
    ClimbearTheme {
        CustomLargeButton()
    }
}