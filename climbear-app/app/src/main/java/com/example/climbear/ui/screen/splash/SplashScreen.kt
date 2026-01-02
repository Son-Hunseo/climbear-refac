package com.example.climbear.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme
import com.example.climbear.ui.theme.getZenDotsFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToInput: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        delay(1000)
        userInfoViewModel.getUserInfo(
            onSuccess = navigateToHome,
            onRequiredMoreInfo = navigateToInput
        )
    }

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
    }
}


@Preview(
    showBackground = true,
)
@Composable
fun UserLogInPreview() {
    ClimbearTheme {
        SplashScreen()
    }
}
