package com.example.climbear


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.climbear.data.auth.TokenManager
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.center.CenterInfoScreen
import com.example.climbear.ui.screen.center.CenterInfoViewModel
import com.example.climbear.ui.screen.dashboard.DashboardScreen
import com.example.climbear.ui.screen.holdlog.HoldLogScreen
import com.example.climbear.ui.screen.holdlog.HoldLogViewModel
import com.example.climbear.ui.screen.home.HomeScreen
import com.example.climbear.ui.screen.home.HomeViewModel
import com.example.climbear.ui.screen.home.PermissionViewModel
import com.example.climbear.ui.screen.home.RecordViewModel
import com.example.climbear.ui.screen.playvideo.PlayVideoScreen
import com.example.climbear.ui.screen.recordvideo.RecordVideoScreen
import com.example.climbear.ui.screen.selecthold.SelectHoldScreen
import com.example.climbear.ui.screen.selecthold.SelectHoldViewModel
import com.example.climbear.ui.screen.solutionguide.SolutionGuideScreen
import com.example.climbear.ui.screen.solutionhint.SolutionHintScreen
import com.example.climbear.ui.screen.solutionhint.SolutionViewModel
import com.example.climbear.ui.screen.splash.SplashScreen
import com.example.climbear.ui.screen.splash.UserInfoViewModel
import com.example.climbear.ui.screen.takepicture.CameraViewModel
import com.example.climbear.ui.screen.takepicture.TakePictureScreen
import com.example.climbear.ui.screen.userinput.UserInputScreen
import com.example.climbear.ui.screen.userlogin.UserLogInScreen
import com.example.climbear.ui.screen.userlogin.UserLogInViewModel

enum class ClimbearScreen() {
    Splash,
    UserLogIn,
    UserInput,
    TakePhoto,
    SelectHold,
    SolutionHint,
    RecordVideo,
    PlayVideo,
    SolutionGuide,
    HoldLog,
    Home,
    Dashboard,
    Center
}

@Composable
fun ClimbearApp(
    navController: NavHostController = rememberNavController()
) {
    val selectHoldViewModel: SelectHoldViewModel = hiltViewModel()
    val userInfoViewModel: UserInfoViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        TokenManager.loginRequired.collect {
            userInfoViewModel.setLoginStateFalse()
            navController.navigate(ClimbearScreen.UserLogIn.name) {
                popUpTo("main_graph")   // 스택 모두 제거하고 로그인으로 이동 (선택사항)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = ClimbearScreen.Splash.name,
        modifier = Modifier.fillMaxSize(),
        route = "main_graph"
    ) {
        // 스플래시 화면
        composable(route = ClimbearScreen.Splash.name) {
            SplashScreen(
                userInfoViewModel = userInfoViewModel,
                navigateToHome = {
                    navController.navigate(ClimbearScreen.Home.name) {
                        popUpTo("main_graph")
                    }
                },
                navigateToInput = { navController.navigate(ClimbearScreen.UserInput.name) }
            )
        }
        // 사용자 로그인
        composable(route = ClimbearScreen.UserLogIn.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val userLogInViewModel: UserLogInViewModel = hiltViewModel(mainEntry)

            UserLogInScreen(
                userLogInViewModel = userLogInViewModel,
                userInfoViewModel = userInfoViewModel,
                navigateToHome = {
                    navController.navigate(ClimbearScreen.Home.name) {
                        popUpTo("main_graph")
                    }
                },
                navigateToInput = { navController.navigate(ClimbearScreen.UserInput.name) }
            )
        }
        // 사용자 신체 정보 입력
        composable(route = ClimbearScreen.UserInput.name) {
            UserInputScreen(
                userInfoViewModel = userInfoViewModel,
                onStartButtonClicked = { navController.navigate(ClimbearScreen.Home.name) }
            )
        }
        // 사진 촬영
        composable(route = ClimbearScreen.TakePhoto.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val cameraViewModel: CameraViewModel = hiltViewModel(mainEntry)
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)

            TakePictureScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
                onCenterButtonClicked = { navController.navigate(ClimbearScreen.SelectHold.name) },
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                cameraViewModel = cameraViewModel
            )
        }
        // 홀드 선택
        composable(
            route = ClimbearScreen.SelectHold.name
        ) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)
            val homeViewModel: HomeViewModel = hiltViewModel(mainEntry)

            SelectHoldScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
                navigateToSolutionHint = { navController.navigate(ClimbearScreen.SolutionHint.name) },
                navigateToBack = { navController.popBackStack() },
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                selectHoldViewModel = selectHoldViewModel,
                homeViewModel = homeViewModel
            )
        }
        // 솔루션 힌트
        composable(
            route = ClimbearScreen.SolutionHint.name
        ) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)
            val solutionViewModel: SolutionViewModel = hiltViewModel(mainEntry)
            val holdLogViewModel: HoldLogViewModel = hiltViewModel(mainEntry)

            SolutionHintScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                solutionViewModel = solutionViewModel,
                selectHoldViewModel = selectHoldViewModel,
                holdLogViewModel = holdLogViewModel,
                userInfoViewModel = userInfoViewModel,
                moveToTakePicture = { navController.navigate(ClimbearScreen.TakePhoto.name) },
                moveToRecord = { navController.navigate(ClimbearScreen.RecordVideo.name) }
            )
        }
        // 동영상 촬영
        composable(route = ClimbearScreen.RecordVideo.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val cameraViewModel: CameraViewModel = hiltViewModel(mainEntry)
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)

            RecordVideoScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
                onCenterButtonClicked = { navController.navigate(ClimbearScreen.PlayVideo.name) },
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                cameraViewModel = cameraViewModel
            )
        }
        // 동영상 재생
        composable(
            route = ClimbearScreen.PlayVideo.name
        ) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)
            PlayVideoScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
                moveToSelectHold = { navController.navigate(ClimbearScreen.SelectHold.name) },
                moveToPrev = { navController.popBackStack() },
                sharedMediaUriViewModel = sharedMediaUriViewModel
            )
        }
        // 솔루션
        composable(route = ClimbearScreen.SolutionGuide.name) {
            SolutionGuideScreen(
                onHomeButtonClicked = { navController.navigate(ClimbearScreen.Home.name) },
            )
        }
        // 홀드 로그
        composable(
            route = ClimbearScreen.HoldLog.name
        ) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }
            val sharedMediaUriViewModel: SharedMediaUriViewModel = hiltViewModel(mainEntry)
            val solutionViewModel: SolutionViewModel = hiltViewModel(mainEntry)
            val holdLogViewModel: HoldLogViewModel = hiltViewModel(mainEntry)
            HoldLogScreen(
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                selectHoldViewModel = selectHoldViewModel,
                solutionViewModel = solutionViewModel,
                holdLogViewModel = holdLogViewModel
                )
        }
        composable(route = ClimbearScreen.Home.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }

            val recordViewModel: RecordViewModel = hiltViewModel(mainEntry)
            val homeViewModel: HomeViewModel = hiltViewModel(mainEntry)
            val permissionViewModel: PermissionViewModel = hiltViewModel(mainEntry)
            val userLogInViewModel: UserLogInViewModel = hiltViewModel(mainEntry)

            HomeScreen(
                moveToTakePhoto = { navController.navigate(ClimbearScreen.TakePhoto.name) },
                moveToRecord = { navController.navigate(ClimbearScreen.RecordVideo.name) },
                moveToDashboard = { navController.navigate(ClimbearScreen.Dashboard.name) },
                moveToCenter = { navController.navigate(ClimbearScreen.Center.name) },
                recordViewModel = recordViewModel,
                homeViewModel = homeViewModel,
                userInfoViewModel = userInfoViewModel,
                permissionViewModel = permissionViewModel,
                userLogInViewModel = userLogInViewModel,
                logout = { navController.navigate(ClimbearScreen.UserLogIn.name) }
            )
        }
        composable(route = ClimbearScreen.Dashboard.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }

            val recordViewModel: RecordViewModel = hiltViewModel(mainEntry)
            val homeViewModel: HomeViewModel = hiltViewModel(mainEntry)
            DashboardScreen(
                moveToHome = { navController.navigate(ClimbearScreen.Home.name) },
                moveToInput = { navController.navigate(ClimbearScreen.UserInput.name) },
                recordViewModel = recordViewModel,
                homeViewModel = homeViewModel,
                userInfoViewModel = userInfoViewModel
            )
        }
        composable(route = ClimbearScreen.Center.name) { navBackStackEntry ->
            val mainEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry("main_graph")
            }

            val centerInfoViewModel: CenterInfoViewModel = hiltViewModel(mainEntry)
            CenterInfoScreen(
                moveToHome = { navController.navigate(ClimbearScreen.Home.name) },
                centerInfoViewModel = centerInfoViewModel
            )
        }
    }
}