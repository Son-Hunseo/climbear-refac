package com.example.climbear.ui.screen.userinput

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.climbear.ui.component.CustomInputForm
import com.example.climbear.ui.component.CustomLargeButton
import com.example.climbear.ui.component.CustomTitleDivider
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.screen.splash.UserInfoViewModel

@Composable
fun UserInputScreen(
    onStartButtonClicked: () -> Unit = {},
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    userInputViewModel: UserInputViewModel = hiltViewModel()
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val userInfoState by userInfoViewModel.userUiState.collectAsState()
    val userInputUiState by userInputViewModel.uiState.collectAsState()

    LaunchedEffect(userInfoState.height, userInfoState.armSpan) {
        val height = userInfoState.height
        val reach = userInfoState.armSpan
        Log.d("userInput", "$height $reach")

        if (height != null && reach != null) {
            userInputViewModel.setUserInputState(height, reach)
        }
    }

    fun changeErrorMessage(message: String) {
        errorMessage = message
    }

    Scaffold(
        topBar = {
            CustomToolBar(
                modifier = Modifier
                    .padding(start = 48.dp, top = 48.dp, end = 48.dp),
                onLogoClick = { }
            )
        },
    ) { innerPadding ->
        UserInputMain(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            userInputViewModel = userInputViewModel,
            userInputUiState = userInputUiState,
            errorMessage = errorMessage,
            onDismissError = { errorMessage = null },
            onStartButtonClicked = onStartButtonClicked,
            isLoggedIn = userInfoState.isLoggedIn,
            changeErrorMessage = { message -> changeErrorMessage(message) }
        )
    }
}

@Composable
fun UserInputMain(
    modifier: Modifier = Modifier,
    userInputViewModel: UserInputViewModel,
    userInputUiState: UserInputUiState,
    errorMessage: String? = null,
    onDismissError: () -> Unit = {},
    onStartButtonClicked: () -> Unit,
    isLoggedIn: Boolean,
    changeErrorMessage: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTitleDivider()
        Spacer(modifier = Modifier.height(48.dp))
        CustomInputForm(
            title = "키",
            subtitle = "Height",
            unit = "cm",
            value = userInputUiState.heightInput,
            onValueChange = { userInputViewModel.onHeightInputChanged(it) },
            isError = userInputUiState.heightError != null,
            errorMessage = userInputUiState.heightError ?: ""
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomInputForm(
            title = "리치",
            subtitle = "Reach",
            unit = "cm",
            value = userInputUiState.reachInput,
            onValueChange = { userInputViewModel.onReachInputChanged(it) },
            isError = userInputUiState.reachError != null,
            errorMessage = userInputUiState.reachError ?: ""
        )
        Spacer(modifier = Modifier.height(54.dp))
        CustomLargeButton(
            modifier = Modifier,
            onButtonClick = {
                val message = userInputViewModel.validateInput()
                if (message != null) {
                    changeErrorMessage(message)
                } else {
                    userInputViewModel.onSave(
                        isLoggedIn = isLoggedIn,
                        onSuccess = onStartButtonClicked
                    )
                }
            },
            enabled = userInputUiState.canSave
        )
    }
    // 유효 값 확인 알림
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(64.dp)
                ) {
                    Image(
                        painter = painterResource(com.example.climbear.R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            },
            title = {
                Text(
                    "입력 오류",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                HighlightedDialogMessage(errorMessage)
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                        color = Color(0xFFDDDDDD),
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = onDismissError,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "확인",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        )
    }
}

@Composable
private fun HighlightedDialogMessage(message: String?) {
    val msg = message ?: ""
    if (msg.endsWith("입력되어야 합니다.")) {
        val keywords = listOf("키", "리치")
        val keyword = keywords.firstOrNull { msg.startsWith(it) }
        if (keyword != null) {
            HighlightedText(
                prefix = keyword,
                rest = msg.removePrefix(keyword)
            )
            return
        }
    }
    if (msg.endsWith("가능합니다.")) {
        val highlightText = "50~250 사이"
        val start = msg.indexOf(highlightText)
        if (start != -1) {
            DefaultAndHighlightText(
                prefix = msg.substring(0, start),
                highlight = highlightText,
                rest = msg.substring(start + highlightText.length)
            )
            return
        }
    }
    // 그 외는 일반 텍스트
    DefaultDialogText(msg)
}

@Composable
private fun HighlightedText(
    prefix: String = "",
    highlight: String = "",
    rest: String = ""
) {
    Text(
        buildAnnotatedString {
            if (prefix.isNotEmpty()) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141478),
                        fontSize = 18.sp
                    )
                ) {
                    append(prefix)
                }
            }
            if (highlight.isNotEmpty()) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141478),
                        fontSize = 18.sp
                    )
                ) {
                    append(highlight)
                }
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            ) {
                append(rest)
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DefaultAndHighlightText(
    prefix: String = "",
    highlight: String = "",
    rest: String = ""
) {
    Text(
        buildAnnotatedString {
            if (prefix.isNotEmpty()) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                ) {
                    append(prefix)
                }
            }
            if (highlight.isNotEmpty()) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141478),
                        fontSize = 18.sp
                    )
                ) {
                    append(highlight)
                }
            }
            if (rest.isNotEmpty()) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                ) {
                    append(rest)
                }
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DefaultDialogText(message: String) {
    Text(
        message,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun UserInputPreview() {
    Scaffold(
        topBar = {
            CustomToolBar(
                modifier = Modifier
                    .padding(start = 48.dp, top = 48.dp, end = 48.dp),
                onLogoClick = { }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 48.dp)
                .background(color = Color.Red),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTitleDivider()
            Spacer(modifier = Modifier.height(48.dp))
            CustomInputForm(
                title = "키",
                subtitle = "Height",
                unit = "cm",
                value = "",
                onValueChange = { },
                isError = false,
                errorMessage = ""
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomInputForm(
                title = "리치",
                subtitle = "Reach",
                unit = "cm",
                value = "",
                onValueChange = { },
                isError = false,
                errorMessage = ""
            )
            Spacer(modifier = Modifier.height(54.dp))
            CustomLargeButton(
                modifier = Modifier
                    .background(Color.Blue),
                onButtonClick = {
                },
                enabled = false
            )
        }
    }
}
