package com.example.climbear.ui.screen.solutionguide

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climbear.ui.component.CustomCameraBottomBar
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.TempBottomButton
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun SolutionGuideScreen(
    onHomeButtonClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CustomToolBar(
                modifier = Modifier
                    .padding(start = 48.dp, top = 48.dp, end = 48.dp),
                onLogoClick = onHomeButtonClicked
            )
        },
        bottomBar = {
            CustomCameraBottomBar(
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, bottom = 48.dp),
                startSlot = { TempBottomButton() },
                centerSlot = { TempBottomButton() },
                endSlot = { TempBottomButton() }
            )
        }
    ) { innerPadding ->
        SolutionGuideMain(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
fun SolutionGuideMain(
    modifier: Modifier = Modifier
) {

}


@Preview(
    showBackground = true,
)
@Composable
fun SolutionGuidePreview() {
    ClimbearTheme {
        SolutionGuideScreen()
    }
}