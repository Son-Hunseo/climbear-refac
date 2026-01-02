package com.example.climbear.ui.screen.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climbear.R
import com.example.climbear.data.dashboard.model.BoulderingGrade
import com.example.climbear.data.dashboard.model.Rank
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.home.DailyGraphCard
import com.example.climbear.ui.component.home.ImageNavigationButton
import com.example.climbear.ui.component.home.RankCard
import com.example.climbear.ui.component.permission.PermissionDialog
import com.example.climbear.ui.component.permission.PermissionDialogData
import com.example.climbear.ui.screen.splash.UserInfoViewModel
import com.example.climbear.ui.screen.userlogin.UserLogInViewModel
import com.example.climbear.util.loadGrades
import com.example.climbear.util.toColor

sealed class CameraAccessTarget {
    object Photo : CameraAccessTarget()
    object Record : CameraAccessTarget()
}

@Composable
fun HomeScreen(
    moveToTakePhoto: () -> Unit = {},
    moveToRecord: () -> Unit = {},
    recordViewModel: RecordViewModel,
    moveToDashboard: () -> Unit = {},
    moveToCenter: () -> Unit = {},
    homeViewModel: HomeViewModel,
    userInfoViewModel: UserInfoViewModel,
    permissionViewModel: PermissionViewModel,
    userLogInViewModel: UserLogInViewModel,
    logout: () -> Unit = {}
) {
    val context = LocalContext.current
    val grades = loadGrades(context)

    val userInfoUiState by userInfoViewModel.userUiState.collectAsState()

    val cameraPermission by permissionViewModel.cameraPermission.collectAsState()
    val locationPermission by permissionViewModel.locationPermission.collectAsState()

    var targetAfterPermission by remember { mutableStateOf<CameraAccessTarget?>(null) }
    var showCameraPermissionRationale by remember { mutableStateOf(false) }

    var showLocationPermissionRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionViewModel.refreshPermissions()
        if (granted) {
            when (targetAfterPermission) {
                CameraAccessTarget.Photo -> moveToTakePhoto()
                CameraAccessTarget.Record -> moveToRecord()
                null -> {}
            }
            targetAfterPermission = null
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionViewModel.refreshPermissions()
    }

    val recordUiState by recordViewModel.uiState.collectAsState()
    val rankState by homeViewModel.rankState.collectAsState()

    LaunchedEffect(Unit) {
        permissionViewModel.refreshPermissions()
        if (!locationPermission && !permissionViewModel.hasShownLocationRationale) {
            showLocationPermissionRationale = true
            permissionViewModel.makrLocationRationaleShown()
        }
        recordViewModel.getRecordList()
        homeViewModel.getExp()
    }

    Scaffold(
        topBar = {
            CustomToolBar()
        }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding(),
            onRequestPhoto = {
                targetAfterPermission = CameraAccessTarget.Photo
                if (cameraPermission) {
                    moveToTakePhoto()
                } else {
                    showCameraPermissionRationale = true
                }
            },
            onRequestRecord = {
                targetAfterPermission = CameraAccessTarget.Record
                if (cameraPermission) {
                    moveToRecord()
                } else {
                    showCameraPermissionRationale = true
                }
            },
            grades = grades,
            uiState = recordUiState,
            onRequestDashboard = moveToDashboard,
            onRequestCenter = moveToCenter,
            homeViewModel = homeViewModel,
            rankState = rankState,
            name = userInfoUiState.nickname,
            logout = {
                userLogInViewModel.logOut(onSuccess = {
                    logout()
                    userInfoViewModel.setLoginStateFalse()
                })
            }
        )
    }

    if (showCameraPermissionRationale) {
        PermissionDialog(
            PermissionDialogData(
                title = stringResource(R.string.camera_permission_title),
                permissionType = stringResource(R.string.camera_permission_type),
                descriptionPrefix = stringResource(R.string.camera_permission_description_prefix),
                descriptionHighlight = stringResource(R.string.camera_permission_description_highlight),
                descriptionSuffix = stringResource(R.string.camera_permission_description_suffix),
                manualGuideTitle = stringResource(R.string.camera_permission_manual_title),
                manualGuide = stringResource(R.string.camera_permission_manual),
                imageResourceId = R.drawable.solve_modal_camera
            ),
            onConfirm = {
                showCameraPermissionRationale = false
                launcher.launch(android.Manifest.permission.CAMERA)
            },
            onDismiss = {
                showCameraPermissionRationale = false
            }
        )
    }

    if (showLocationPermissionRationale) {
        PermissionDialog(
            PermissionDialogData(
                title = stringResource(R.string.location_permission_title),
                permissionType = stringResource(R.string.location_permission_type),
                descriptionPrefix = stringResource(R.string.location_permission_description_prefix),
                descriptionHighlight = stringResource(R.string.location_permission_description_highlight),
                descriptionSuffix = stringResource(R.string.location_permission_description_suffix),
                manualGuideTitle = stringResource(R.string.location_permission_manual_title),
                manualGuide = stringResource(R.string.location_permission_manual),
                imageResourceId = R.drawable.home_modal_location
            ),
            onConfirm = {
                showLocationPermissionRationale = false
                locationPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismiss = {
                showLocationPermissionRationale = false
            }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onRequestPhoto: () -> Unit,
    onRequestRecord: () -> Unit,
    grades: List<BoulderingGrade>,
    homeViewModel: HomeViewModel,
    uiState: RecordUiState,
    onRequestDashboard: () -> Unit = {},
    onRequestCenter: () -> Unit = {},
    rankState: Rank,
    name: String?,
    logout: () -> Unit = {}
) {
    val myRankLogo by homeViewModel.myRankLogo.collectAsState()
    val nextLogo by homeViewModel.nextRankLogo.collectAsState()

    val myRank = rankState.levelName
    val context = LocalContext.current

    homeViewModel.updateRankColor(myRank, context)

    val problemCounts = arrayOf<Int>(0, 0, 0, 0, 0, 0, 0, 0, 0)
    val levelOrder =
        arrayOf("white", "yellow", "orange", "green", "blue", "red", "purple", "gray", "pink")

    for (record in uiState.todayRecords) {
        val index = levelOrder.indexOf(record.level.lowercase())
        if (index != -1) {
            problemCounts[index] += 1
        }
    }

    val colors = grades.map { it.hex.toColor() }

    val progress = rankState.exp.toFloat() / rankState.maxExp.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0x14, 0x14, 0x78)
                            )
                        ) {
                            append(name)
                        }
                        append(stringResource(R.string.home_welcome))
                    }
                )
                Text(
                    stringResource(R.string.home_message_1)
                )
            }
            Image(
                painter = painterResource(R.drawable.home_logout),
                contentDescription = "로그아웃",
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = logout),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (myRank.lowercase() != "pink") {
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
                    verticalArrangement = Arrangement.Center,
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
                                            nextLogo.color
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
                        painter = painterResource(nextLogo.smallLogoResId),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = "다음 등급",
                        fontSize = 8.sp
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_small_gray),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = "이전 등급",
                        fontSize = 8.sp
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "문제를 풀이하며 나의 등급을 올려 보세요.",
                        fontSize = 10.sp
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                    Text(
                        "클라이밍 등급 기준을 자세히 알고 싶어요.",
                        fontSize = 8.sp
                    )
                }
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
            }
        }
        Row(
            modifier = Modifier
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DailyGraphCard(
                problemCounts = problemCounts,
                colors = colors,
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            )
            RankCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                myRank = myRank,
                logoResId = myRankLogo.bigLogoResId,
                color = myRankLogo.color
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImageNavigationButton(
                text = stringResource(R.string.climbing_dashboard_navigation),
                onClick = onRequestDashboard,
                modifier = Modifier.weight(1f),
                imageResId = R.drawable.home_dashboard
            )
            ImageNavigationButton(
                text = stringResource(R.string.climbing_center_navigation),
                onClick = onRequestCenter,
                modifier = Modifier.weight(1f),
                imageResId = R.drawable.destination
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.home_picturevideo),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "솔루션 탐색",
                fontSize = 16.sp
            )
            Text(
                text = "안 풀리는 문제를 도와드릴게요."
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImageNavigationButton(
                    text = stringResource(R.string.take_photo_navigation),
                    onClick = onRequestPhoto,
                    imageResId = R.drawable.home_picture
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xF2, 0XF2, 0xFB),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "· 문제별 사용 가능 홀드 시각화\n· 문제 풀이 예시 경로 추천",
                        fontSize = 10.sp,
                        color = Color(0xFF12177B)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImageNavigationButton(
                    text = stringResource(R.string.record_navigation),
                    onClick = onRequestRecord,
                    imageResId = R.drawable.home_video
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xF2, 0XF2, 0xFB),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "· 나의 로그 단계별 뷰 제공\n· 어려운 문제 풀이 솔루션 제안",
                        fontSize = 10.sp,
                        color = Color(0xFF12177B)
                    )
                }
            }
        }
    }
}