package com.example.climbear.ui.component.selecthold

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.climbear.util.loadGrades
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.climbear.R


@Composable
fun SelectLevelDialog(
    userLevelText: String,
    onConfirm: (level: String) -> Unit = { },
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current

    val grades = loadGrades(context)
    val levelTexts = listOf(
        "VB",
        "V0",
        "V1-2",
        "V2-3",
        "V3-4",
        "V4-5",
        "V5-6",
        "V6-7",
        "V7-8",
    )
    val levelImages = listOf(
        R.drawable.level_white,
        R.drawable.level_yellow,
        R.drawable.level_orange,
        R.drawable.level_green,
        R.drawable.level_blue,
        R.drawable.level_red,
        R.drawable.level_purple,
        R.drawable.level_gray,
        R.drawable.level_pink
    )

    val colorToLevelMap: Map<String, Int> = grades.associate { it.color to it.level }
    val userLevel = colorToLevelMap[userLevelText] ?: 4

    var selectedLevelIndex by remember { mutableIntStateOf(userLevel) }
    val coroutineScope = rememberCoroutineScope()

    // LazyList state
    val levelListState = rememberLazyListState()

    // 아이템 항목 중앙에서 벗어나 있을 때 중앙으로 정렬
    val levelFling = rememberSnapFlingBehavior(
        lazyListState = levelListState,
        snapPosition = SnapPosition.Center
    )

    LaunchedEffect(Unit) {
        levelListState.animateScrollToItem(index = userLevel)
        Log.d("selectLevel", "$userLevelText, $userLevel")
    }

    // 첫 번째 아이템을 선택 (padding 적용으로 가운데 위치한 아이템 -> 첫 번째 아이템)
    LaunchedEffect(levelListState) {
        snapshotFlow { levelListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collectLatest { index -> selectedLevelIndex = index }
    }

    var columnWidthPx by remember { mutableIntStateOf(0) }
    val columnWidthDp = with(LocalDensity.current) { columnWidthPx.toDp() }

    Dialog(onDismissRequest = onDismiss) { // onDismissRequest: 사용자가 뒤로가기 누르거나 바깥 영역을 터치했을 때 호출됨
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                // Dismiss on background tap
                .clickable(
                    onClick = onDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .onGloballyPositioned { coordinates ->
                            columnWidthPx = coordinates.size.width // ← px 단위
                        }
                ) {
                    // 홀드 레벨 선택
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF141478)
                                )
                            ) {
                                append("레벨")
                            }
                            append(" 선택")
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    // 홀드 이미지
                    Image(
                        painter = painterResource(levelImages[selectedLevelIndex]),
                        contentDescription = "logo",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    LazyRow(
                        state = levelListState,
                        flingBehavior = levelFling,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(
                            horizontal = (columnWidthDp / 2 - 30.dp).coerceAtLeast(0.dp)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(levelTexts) { index, levelText ->
                            val isLevelSelected = index == selectedLevelIndex
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        coroutineScope.launch {
                                            levelListState.animateScrollToItem(index)
                                        }
                                    }
                            ) {
                                Text(
                                    text = levelTexts[index],
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (isLevelSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isLevelSelected) Color(0xFF141478) else Color.Black,
                                    modifier = Modifier
                                        .width(60.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { onConfirm(grades[selectedLevelIndex].color.uppercase()) },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
//                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFFFFF),
                            contentColor = Color(0xFF000000)
                        ),
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectLevelDialog() {
    SelectLevelDialog(
        userLevelText = "GREEN"
    )
}
