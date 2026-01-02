package com.example.climbear.ui.screen.userlogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climbear.R
import com.example.climbear.ui.screen.splash.UserInfoViewModel
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.getZenDotsFontFamily

@Composable
fun UserLogInScreen(
    userLogInViewModel: UserLogInViewModel = viewModel(),
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToInput: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 로고
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(130.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CLIMBEAR",
            fontSize = 18.sp,
            fontFamily = getZenDotsFontFamily()
            // fontFamily = ZenDots
        )
        Spacer(modifier = Modifier.height(160.dp))
        // 카카오 로그인
        Image(
            painter = painterResource(R.drawable.kakaologin),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(300.dp)
                .clickable {
                    userLogInViewModel.kakaoLogin(
                        context = context,
                        onResult = { token, error ->
                            userLogInViewModel.handleKakaoLoginResult(
                                token, error,
                                onLoginSuccess = {
                                    userInfoViewModel.getUserInfo(navigateToHome, navigateToInput)
                                }
                            )
                        }
                    )
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview(
    showBackground = true,
)
@Composable
fun UserLogInPreview() {
    ClimbearTheme {
        UserLogInScreen()
    }
}
