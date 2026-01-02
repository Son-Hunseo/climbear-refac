package com.example.climbear.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomInputForm(
    title: String,
    subtitle: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = "",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
            Text(
                text = subtitle,
                color = Color(0xFF141478)
            )
        }
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // 숫자만 필터링
                    if (
                        newValue.isEmpty() || // 입력 지울 때 허용
                        newValue.matches(Regex("^\\d*\\.?\\d*\$")) // 숫자 + 소수점 1회 허용
                    ) {
                        onValueChange(newValue)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF141478),
                    unfocusedBorderColor = Color.Black
                ),
                isError = isError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .width(150.dp)
                    .defaultMinSize(minHeight = 56.dp)
            )
            if (isError) {
                Text(
                    text = errorMessage,
                    modifier = Modifier
                        .padding(top = 4.dp), // 원하는 padding 직접 설정
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }
        Text(
            text = unit,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 2.dp)
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun CustomInputFormPreview() {
    ClimbearTheme {
        Column {
            CustomInputForm(
                title = "키",
                subtitle = "height",
                unit = "cm",
                value = "",
                onValueChange = {}
            )
            CustomInputForm(
                title = "리치",
                subtitle = "Reach",
                unit = "cm",
                value = "",
                onValueChange = {}
            )
        }
    }
}