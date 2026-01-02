package com.example.climbear.ui.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.data.dashboard.model.Rank
import com.example.climbear.data.local.model.LogoData
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.dashboard.SolutionCalendars
import com.example.climbear.ui.component.home.RankCard
import com.example.climbear.ui.screen.home.HomeViewModel
import com.example.climbear.ui.screen.home.RecordUiState
import com.example.climbear.ui.screen.home.RecordViewModel
import com.example.climbear.ui.screen.splash.UserInfoViewModel

@Composable
fun DashboardScreen(
    moveToHome: () -> Unit = {},
    moveToInput: () -> Unit = {},
    recordViewModel: RecordViewModel,
    homeViewModel: HomeViewModel,
    userInfoViewModel: UserInfoViewModel
) {
    val recordUiState by recordViewModel.uiState.collectAsState()

    val myRankLogo by homeViewModel.myRankLogo.collectAsState()
    val nextRankLogo by homeViewModel.nextRankLogo.collectAsState()

    val rankState by homeViewModel.rankState.collectAsState()

    val userInfoUiState by userInfoViewModel.userUiState.collectAsState()

    Scaffold(
        topBar = {
            CustomToolBar(
                onLogoClick = moveToHome
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            DashboardContent(
                modifier = Modifier
                    .fillMaxSize(),
                uiState = recordUiState,
                myRankLogo = myRankLogo,
                nextRankLogo = nextRankLogo,
                rankState = rankState,
                height = userInfoUiState.height,
                reach = userInfoUiState.armSpan,
                moveToInput = moveToInput
            )
        }
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    uiState: RecordUiState = RecordUiState(),
    myRankLogo: LogoData,
    nextRankLogo: LogoData,
    rankState: Rank,
    height: Double?,
    reach: Double?,
    moveToInput: () -> Unit
) {
    val progress = rankState.exp.toFloat() / rankState.maxExp.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (rankState.nextLevelName != null) {
                RankCard(
                    modifier = Modifier
                        .weight(1f),
                    myRank = rankState.levelName.uppercase(),
                    logoResId = myRankLogo.bigLogoResId,
                    color = myRankLogo.color
                )
                Image(
                    painter = painterResource(myRankLogo.arrowResId!!),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
                RankCard(
                    modifier = Modifier
                        .weight(1f),
                    myRank = rankState.nextLevelName.uppercase(),
                    logoResId = nextRankLogo.bigLogoResId,
                    color = nextRankLogo.color,
                    lockResId = nextRankLogo.nextResId
                )
            } else {
                RankCard(
                    modifier = Modifier
                        .weight(1f),
                    myRank = rankState.levelName.uppercase(),
                    logoResId = myRankLogo.bigLogoResId,
                    color = myRankLogo.color
                )
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(myRankLogo.smallLogoResId),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = "현재 등급",
                    fontSize = 8.sp
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "문제를 풀이하며 나의 등급을 올려 보세요.",
                    fontSize = 10.sp
                )
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        // [수정1] 실린더 양쪽에 여백을 주기 위해 fillMaxWidth(0.8f)로 너비 축소 (원하는 비율로 조정)
                        .fillMaxWidth(0.8f) // ← 실린더 길이 줄이기
                        // [수정2] 테두리 두께를 1.dp로 줄이고, 색상을 #6468AB로 변경
                        .border(
                            width = 1.dp, // ← 테두리 두께 줄임
                            color = Color(0xFF6468AB), // ← 테두리 색상 6468AB로 변경
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        myRankLogo.color,
                                        nextRankLogo.color
                                    )
                                )
                            )
                    )
                }
                Text(
                    "클라이밍 등급 기준을 자세히 알고 싶어요.",
                    fontSize = 8.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(nextRankLogo.smallLogoResId),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = "다음 등급",
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.padding(30.dp))

        SolutionCalendars(
            height = height,
            reach = reach,
            modifier = Modifier.fillMaxWidth(),
            uiState = uiState,
            moveToInput = moveToInput
        )
    }
}