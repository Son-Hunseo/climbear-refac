package com.example.climbear.ui.screen.solutionhint

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.climbear.R
import com.example.climbear.background.VideoAnalysisWorker
import com.example.climbear.data.record.model.SimilarRecordData
import com.example.climbear.data.solution.model.SolutionData
import com.example.climbear.ui.component.CustomLoadingOverlay
import com.example.climbear.ui.component.CustomLongLoadingOverlay
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.home.ImageNavigationButton
import com.example.climbear.ui.component.solutionhint.SolutionTabBar
import com.example.climbear.ui.screen.MediaType
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.holdlog.HoldLogScreen
import com.example.climbear.ui.screen.holdlog.HoldLogViewModel
import com.example.climbear.ui.screen.holdlog.PagerProgressBar
import com.example.climbear.ui.screen.selecthold.SelectHoldViewModel
import com.example.climbear.ui.screen.splash.UserInfoViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun SolutionHintScreen(
    onHomeButtonClicked: () -> Unit = {},
    solutionViewModel: SolutionViewModel = viewModel(),
    selectHoldViewModel: SelectHoldViewModel,
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    holdLogViewModel: HoldLogViewModel,
    userInfoViewModel: UserInfoViewModel,
    moveToTakePicture: () -> Unit = {},
    moveToRecord: () -> Unit = {},
) {
    val localContext = LocalContext.current

    val selectHoldState by selectHoldViewModel.uiState.collectAsState()
    val url by selectHoldViewModel.previousUrl.collectAsState()

    val imageState by sharedMediaUriViewModel.imageState.collectAsState()
    val fromMedia by sharedMediaUriViewModel.fromMedia.collectAsState()

    val bitmapState by solutionViewModel.bitmaps.collectAsState()

    val userInfoState by userInfoViewModel.userUiState.collectAsState()

    BackHandler {
        solutionViewModel.selectTab("hint")
        if (fromMedia == MediaType.RECORD) {
            moveToRecord()
        } else {
            moveToTakePicture()
        }
    }

    val holds = selectHoldState.holdCoordinates

    LaunchedEffect(selectHoldState.problemId, selectHoldState.categoryId) {
        if (selectHoldState.problemId != null && selectHoldState.categoryId != null) {
            solutionViewModel.loadSolutionState(
                problemId = selectHoldState.problemId!!,
                categoryId = selectHoldState.categoryId!!
            )
        }
    }

    val solutionUiState by solutionViewModel.uiState.collectAsState()
    val selectedHint by solutionViewModel.selectedHint.collectAsState()

    val workManager = WorkManager.getInstance(localContext)

    val holdsJson = GsonBuilder().create().toJson(selectHoldState.holdCoordinates)
    val holdsFile = File(localContext.filesDir, "hold_coords.json")
    holdsFile.bufferedWriter().use { it.write(holdsJson) }

    val selectedIdsArray = selectHoldState.selectedHoldIds.toIntArray()

    // Worker 요청 설정 (videoUri 사용)
    val workRequest = OneTimeWorkRequestBuilder<VideoAnalysisWorker>()
        .setInputData(
            workDataOf(
                VideoAnalysisWorker.KEY_VIDEO_URI to sharedMediaUriViewModel.videoUri.toString(),
                VideoAnalysisWorker.KEY_HOLD_FILE to holdsFile.name,    // 파일 이름만 전달
                VideoAnalysisWorker.KEY_SELECTED_IDS to selectedIdsArray
            )
        )
        .build()

    var workState by remember { mutableStateOf<WorkInfo?>(null) }

    // Worker 실행
    LaunchedEffect(sharedMediaUriViewModel.videoUri, selectHoldState.problemId) {
        // videoUri나 problemId가 null이면 실행하지 않음
        sharedMediaUriViewModel.videoUri ?: return@LaunchedEffect
        selectHoldState.problemId ?: return@LaunchedEffect

        if (fromMedia == MediaType.RECORD) {
            solutionViewModel.startLogLoad()
            //고유한 작업 이름 생성 (영상 URI와 문제 ID 기반)
            //val uniqueWorkName = "video_analysis_${currentVideoUri}_${currentProblemId}"
            val uniqueWorkName = "video_analysis_work"

            //enqueueUniqueWork 사용
            workManager.enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE, //어떤 작업이 들어오든 새로 들어온 작업으로 대체
                workRequest
            )
        }
    }

    // Worker 상태 감시
    LaunchedEffect(workRequest.id) {
        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { info ->
            workState = info
        }
    }

    // Worker 완료 시 ViewModel 메서드 호출
    LaunchedEffect(workState?.state) {
        if (workState?.state == WorkInfo.State.SUCCEEDED) {
            solutionViewModel.postRecordFromLog(
                context = localContext,
                problemId = selectHoldState.problemId,
                startHoldIds = selectHoldState.startHoldIds,
                topHoldId = selectHoldState.topHoldId,
                height = userInfoState.height ?: 160.0  // 키 없으면 기본 값(160.0) 설정
            )
        }
    }

    Scaffold(topBar = {
        CustomToolBar(
            modifier = Modifier.padding(start = 48.dp, end = 48.dp),
            onLogoClick = {
                solutionViewModel.resetState()
                onHomeButtonClicked()
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (fromMedia == MediaType.RECORD) {
                SolutionTabBar(
                    selectedTab = solutionUiState.selectedTab,
                    isLogLoaded = solutionUiState.isLogLoaded,
                    onSelectHint = { solutionViewModel.selectTab("hint") },
                    onSelectLog = { solutionViewModel.selectTab("holdLog") },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (solutionUiState.selectedTab) {
                "hint" -> {
                    when (solutionUiState.loadState) {
                        SolutionLoadState.Success -> {
                            AnimatedContent(selectedHint) { hint ->
                                if (hint == null) {
                                    if (solutionUiState.solutionData?.leftHand!!.isEmpty()) {
                                        ErrorScreen(
                                            modifier = Modifier,
                                            moveToHome = {
                                                solutionViewModel.resetState()
                                                onHomeButtonClicked()
                                            },
                                            title = "솔루션 찾기 실패",
                                            message = "솔루션을 찾지 못했어요!"
                                        )
                                    } else {
                                        SolutionHintList(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(),
                                            onClick = { problemId ->
                                                solutionViewModel.selectHint(
                                                    context = localContext,
                                                    problemId = problemId,
                                                    url = url ?: "",
                                                    holds = holds,
                                                    originalWidth = imageState.originalWidth,
                                                    originalHeight = imageState.originalHeight
                                                )
                                            },
                                            similarSolutions = solutionUiState.similarRecordList
                                        )
                                    }
                                } else {
                                    if (bitmapState.isLoading) {
                                        CustomLongLoadingOverlay()
                                    } else {
                                        SolutionHintMain(
                                            modifier = Modifier
                                                .padding()
                                                .fillMaxSize(),
                                            onBack = {
                                                solutionViewModel.selectHint(
                                                    context = localContext,
                                                    problemId = -1
                                                )
                                            },
                                            hint = hint,
                                            bitmaps = bitmapState.bitmaps
                                        )
                                    }
                                }
                            }
                        }

                        SolutionLoadState.Error -> {
                            ErrorScreen(
                                modifier = Modifier,
                                moveToHome = {
                                    solutionViewModel.resetState()
                                    onHomeButtonClicked()
                                },
                                title = "불러오기 실패",
                                message = "데이터를 불러오는데 실패했어요!"
                            )
                        }

                        else -> Unit
                    }
                }

                "holdLog" -> HoldLogScreen(
                    modifier = Modifier
                        .padding()
                        .fillMaxSize(),
                    sharedMediaUriViewModel = sharedMediaUriViewModel,
                    solutionViewModel = solutionViewModel,
                    selectHoldViewModel = selectHoldViewModel,
                    holdLogViewModel = holdLogViewModel,
                    onBack = { solutionViewModel.selectTab("hint") }
                )
            }
        }
    }

    if (solutionUiState.loadState == SolutionLoadState.Loading) {
        CustomLoadingOverlay()
    }
}

@Composable
fun SolutionHintList(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    similarSolutions: List<SimilarRecordData> = emptyList()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(text = "HINT", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(10.dp))
        LazyColumn(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentSize(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    onClick = { onClick(0) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "추천 풀이",
                            modifier = Modifier
                                .padding(12.dp)
                                .background(
                                    color = Color(0xFFFEFEFF),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                                .align(Alignment.Start),
                            color = Color(0xFF14147B),
                            fontSize = 12.sp
                        )
                        Image(
                            painter = painterResource(R.drawable.folder),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            "나의 체형 기반 추천 풀이가 도착했어요.",
                            color = Color(0xFF14147B)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                    }
                }
            }

            items(similarSolutions) { solution ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentSize(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    onClick = { onClick(solution.problemId) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "유사 체형",
                                modifier = Modifier
                                    .padding(12.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF2AABE2),
                                                Color(0xFF6200D2)
                                            )
                                        ),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 3.dp),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = parseDateText(solution.solvedDate),
                                color = Color(0xFF14147B),
                                fontSize = 10.sp
                            )
                        }
                        Image(
                            painter = painterResource(R.drawable.body),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            "유사 체형의 클라이머 풀이를 확인하세요.",
                            color = Color(0xFF14147B)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

fun parseDateText(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        val outputFormat = SimpleDateFormat("yyyy년 M월 d일 HH:mm", Locale.KOREA)
        val currentDate = inputFormat.parse(date)
        currentDate?.let { outputFormat.format(it) } ?: date
    } catch (e: Exception) {
        Log.e("parsing", "$date 파싱 오류 ", e)
        date
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SolutionHintMain(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    hint: SolutionData,
    bitmaps: List<Bitmap>?
) {
    BackHandler {
        onBack()
    }
    val pagerState = rememberPagerState(pageCount = { hint.leftHand.size })
    val thumbnailState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        thumbnailState.animateScrollToItem(
            index = pagerState.currentPage
        )
    }

    LaunchedEffect(thumbnailState) {
        snapshotFlow { thumbnailState.layoutInfo.visibleItemsInfo }
            .filter { thumbnailState.isScrollInProgress }
            .mapNotNull { visibleItems ->
                if (visibleItems.isEmpty()) return@mapNotNull null

                with(thumbnailState.layoutInfo) {
                    val viewportCenter = (viewportStartOffset + viewportEndOffset) / 2

                    visibleItems.minByOrNull { item ->
                        val itemCenter = item.offset + item.size / 2
                        abs(itemCenter - viewportCenter)
                    }?.index
                }
            }
            .distinctUntilChanged()
            .collectLatest { centerIndex ->
                if (centerIndex != pagerState.currentPage) {
                    pagerState.scrollToPage(centerIndex)
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (bitmaps != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
//                    .padding(horizontal = 24.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                ) { page ->
                    Box(
                        modifier = Modifier
//                            .border(
//                                width = 8.dp,
//                                color = Color.White,
//                                shape = RoundedCornerShape(16.dp)
//                            )
                    ) {
                        Image(
                            bitmap = bitmaps[page].asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
//                                .clip(RoundedCornerShape(16.dp))
                                .aspectRatio(3f / 4f),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                PagerProgressBar(
                    pagerState = pagerState,
                    pageCount = hint.leftHand.size
                )
                Spacer(modifier = Modifier.height(16.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
                ) {
                    val rowWidth = maxWidth
                    val rowHeight = maxHeight
                    val thumbWidth = rowHeight * (3f / 4f)
                    val thumbSpacing = 8.dp
                    val itemTotalWidth = thumbWidth + thumbSpacing

                    LazyRow(
                        state = thumbnailState,
                        contentPadding = PaddingValues(
                            start = (rowWidth - itemTotalWidth) / 2,
                            end = (rowWidth - itemTotalWidth) / 2
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(hint.leftHand.size) { index ->
                            val isSelected = index == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) Color(0xFF141478) else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                            ) {
                                Image(
                                    bitmap = bitmaps[index].asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(3f / 4f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    moveToHome: () -> Unit = {},
    title: String = "에러",
    message: String = "알 수 없는 오류가 발생했습니다."
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = title,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = message,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(16.dp))
        ImageNavigationButton(
            text = "홈으로 돌아가기...",
            onClick = moveToHome,
            fontSize = 20.sp
        )
    }
}