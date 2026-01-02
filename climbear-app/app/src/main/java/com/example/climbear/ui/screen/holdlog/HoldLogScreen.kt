package com.example.climbear.ui.screen.holdlog

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climbear.ui.component.CustomLongLoadingOverlay
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.selecthold.SelectHoldViewModel
import com.example.climbear.ui.screen.solutionhint.SolutionViewModel
import com.example.climbear.ui.theme.ClimbearTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HoldLogScreen(
    modifier: Modifier = Modifier,
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    solutionViewModel: SolutionViewModel,
    selectHoldViewModel: SelectHoldViewModel,
    holdLogViewModel: HoldLogViewModel,
    onBack: () -> Unit = {}
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val frames by holdLogViewModel.frames.collectAsState()
    val isLoaded by holdLogViewModel.isFramesLoaded.collectAsState()
    val solutionState by solutionViewModel.uiState.collectAsState()
    val holdState by selectHoldViewModel.uiState.collectAsState()

    val holdLog = solutionState.routeResult?.holdLog
    val holdMap = holdState.holdCoordinates.associate { hold ->
        hold.holdId to hold.coordinates
    }

    // 실제 디바이스 내에서 사용시
    LaunchedEffect(Unit, holdLog, holdMap) {
        holdLogViewModel.extractFramesFromVideo(context, sharedMediaUriViewModel.videoUri, holdLog, holdMap)
    }

    // 저장된 동영상 사용
//        LaunchedEffect(context) {
//            viewModel.extractFramesFromVideo(context)
//        }

    if (isLoaded) {
        HoldLogMain(
            modifier = modifier
                .fillMaxSize(),
            images = frames
        )
    } else {
        CustomLongLoadingOverlay()
    }
}

@Composable
fun HoldLogMain(
    modifier: Modifier = Modifier,
    images: List<Bitmap>,
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    val thumbnailState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 캐러셀 이미지 변경 → 썸네일 변경
    LaunchedEffect(pagerState.currentPage) {
        thumbnailState.animateScrollToItem(
            index = pagerState.currentPage,
        )
    }

    // 썸네일 스크롤 → 캐러셀 이미지 변경
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
//            .debounce(100)
            .collectLatest { centerIndex ->
                if (centerIndex != pagerState.currentPage) {
                    pagerState.scrollToPage(centerIndex)
                }
            }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
//            .padding(horizontal = 24.dp)
    ) {
        // 캐러셀 HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
        ) { page ->
            Box(
                modifier = Modifier
//                    .border(
//                        width = 8.dp,
//                        color = Color.White,
//                        shape = RoundedCornerShape(16.dp)
//                    )
            ) {
                Image(
                    bitmap = images[page].asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(3f / 4f),
                    contentScale = ContentScale.FillBounds
                )
            }
        }

        // 진행 바
        Spacer(modifier = Modifier.height(16.dp))
        PagerProgressBar(
            pagerState = pagerState,
            pageCount = images.size
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 썸네일 LazyRow
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
                items(images.size) { index ->
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
                            bitmap = images[index].asImageBitmap(),
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

@Composable
fun PagerProgressBar(pagerState: PagerState, pageCount: Int) {
    val progress by remember {
        derivedStateOf {
            if (pageCount <= 1) 1f
            else pagerState.currentPage.toFloat() / (pageCount - 1)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .padding(horizontal = 24.dp)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(Color(0xFF141478), RoundedCornerShape(4.dp))
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun HoldLogPreview() {
    ClimbearTheme {
//        HoldLogScreen()
    }
}