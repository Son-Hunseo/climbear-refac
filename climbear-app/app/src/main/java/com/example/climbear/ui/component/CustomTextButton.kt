package com.example.climbear.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.getZenDotsFontFamily

enum class ButtonType {
    PRIMARY,
    SECONDARY
}

@Composable
fun CustomTextButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit = {},
    message: String = "RETRY",
    buttonType: ButtonType = ButtonType.PRIMARY
) {
    val (containerColor, contentColor) = when (buttonType) {
        ButtonType.PRIMARY -> Pair(Color(0xFF141478), Color(0xFFFFFFFF))
        ButtonType.SECONDARY -> Pair(Color(0xFFFFFFFF), Color(0xFF141478))
    }

    Button(
        modifier = modifier
            .width(150.dp)
            .height(36.dp),

//            .shadow(
//                elevation = 3.dp,
//                clip = false
//            ),
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            fontFamily = getZenDotsFontFamily()
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomTextButtonPreview() {
    ClimbearTheme {
        CustomTextButton()
    }
}