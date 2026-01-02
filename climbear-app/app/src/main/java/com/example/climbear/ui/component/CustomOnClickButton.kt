package com.example.climbear.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.climbear.R
import com.example.climbear.ui.theme.ClimbearTheme

@Composable
fun CustomOnClickButton(
    imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size)
        )
    }
}

@Preview
@Composable
fun CustomOnClickButtonPreview() {
    ClimbearTheme {
        Column {
            CustomOnClickButton(
                imageRes = R.drawable.turn,
                contentDescription = "카메라 방향 전환"
            )
            CustomOnClickButton(
                imageRes = R.drawable.solution,
                contentDescription = "솔루션"
            )
            CustomOnClickButton(
                imageRes = R.drawable.retry,
                contentDescription = "선택 취소",
                size = 40.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.log_icon,
                contentDescription = "로그 보기"
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_white,
                contentDescription = "white 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_yellow,
                contentDescription = "yellow 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_orange,
                contentDescription = "orange 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_green,
                contentDescription = "green 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo,
                contentDescription = "blue 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_red,
                contentDescription = "red 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_purple,
                contentDescription = "purple 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_gray,
                contentDescription = "gray 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_small_pink,
                contentDescription = "pink 60",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_white,
                contentDescription = "white 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_yellow,
                contentDescription = "yellow 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_orange,
                contentDescription = "orange 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_green,
                contentDescription = "green 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo,
                contentDescription = "blue 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_red,
                contentDescription = "red 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_purple,
                contentDescription = "purple 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_gray,
                contentDescription = "gray 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.logo_big_pink,
                contentDescription = "pink 150",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.solve_modal_camera,
                contentDescription = "카메라 권한",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.solve_calendar,
                contentDescription = "날짜 자동 입력",
                size = 40.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.solve_location,
                contentDescription = "위치 아이콘",
                size = 40.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_pen,
                contentDescription = "내용 수정",
                size = 20.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_sigma,
                contentDescription = "월별 풀이 수 총합",
                size = 20.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_under_arrow,
                contentDescription = "월별 캘린더 펼치기",
                size = 20.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_toyellow,
                contentDescription = "arrow_white에서 yellow",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_toorange,
                contentDescription = "arrow_yellow에서 orange",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_togreen,
                contentDescription = "arrow_orange에서 green",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_toblue,
                contentDescription = "arrow_green에서 blue",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_tored,
                contentDescription = "arrow_blue에서 red",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_topurple,
                contentDescription = "arrow_red에서 purple",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_togray,
                contentDescription = "arrow_purple에서 gray",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_topink,
                contentDescription = "arrow_gray에서 pink",
                size = 60.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextyellow,
                contentDescription = "lock_white에서 yellow",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextorange,
                contentDescription = "lock_yellow에서 orange",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextgreen,
                contentDescription = "lock_orange에서 green",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextblue,
                contentDescription = "lock_green에서 blue",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextred,
                contentDescription = "lock_blue에서 red",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextpurple,
                contentDescription = "lock_red에서 purple",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextgray,
                contentDescription = "lock_purple에서 gray",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.dashboard_nextpink,
                contentDescription = "lock_gray에서 pink",
                size = 100.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.system_lock,
                contentDescription = "기능 잠금",
                size = 45.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_modal_location,
                contentDescription = "위치 권한",
                size = 150.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_right_arrow,
                contentDescription = "이동 버튼",
                size = 20.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_logout,
                contentDescription = "로그아웃",
                size = 40.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.homeguide_live_chat,
                contentDescription = "가이드 아이콘",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_dashboard,
                contentDescription = "대시보드 아이콘",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.homehint_questionmark,
                contentDescription = "추가 안내",
                size = 20.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_picture,
                contentDescription = "촬영 솔루션 아이콘",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_video,
                contentDescription = "영상 솔루션 아이콘",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.home_picturevideo,
                contentDescription = "솔루션 기능 소개",
                size = 70.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.gym_empty,
                contentDescription = "암장 정보 없음",
                size = 70.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide_loading,
                contentDescription = "답변 생성 중",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide1_rules,
                contentDescription = "가이드 1",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide2_meditation,
                contentDescription = "가이드 2",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide3_safety_helmet,
                contentDescription = "가이드 3",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide4_push,
                contentDescription = "가이드 4",
                size = 25.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.guide_underbar,
                contentDescription = "가이드 하단",
                size = 400.dp
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip1_jug,
                contentDescription = "홀드 1"
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip2_pocket,
                contentDescription = "홀드 2"
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip3_sloper,
                contentDescription = "홀드 3"
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip4_pinch,
                contentDescription = "홀드 4"
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip5_undercut,
                contentDescription = "홀드 5"
            )
            CustomOnClickButton(
                imageRes = R.drawable.holdgrip6_crimp,
                contentDescription = "홀드 6"
            )
        }
    }
}
