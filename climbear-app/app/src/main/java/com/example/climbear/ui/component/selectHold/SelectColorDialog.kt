package com.example.climbear.ui.component.selecthold

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.climbear.util.innerShadow
import com.example.climbear.util.loadGrades
import com.example.climbear.util.toColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.climbear.R

@Composable
fun SelectColorDialog(
    onConfirm: (color: String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current

    val grades = loadGrades(context)
    val logos = listOf(
        R.drawable.logo_big_white,
        R.drawable.logo_big_yellow,
        R.drawable.logo_big_orange,
        R.drawable.logo_big_green,
        R.drawable.logo,
        R.drawable.logo_big_red,
        R.drawable.logo_big_purple,
        R.drawable.logo_big_gray,
        R.drawable.logo_big_pink
    )

    var selectedColorIndex by remember { mutableIntStateOf(4) }
    val coroutineScope = rememberCoroutineScope()

    // LazyList state
    val colorListState = rememberLazyListState()

    // 아이템 항목 중앙에서 벗어나 있을 때 중앙으로 정렬
    val colorFling = rememberSnapFlingBehavior(
        lazyListState = colorListState,
        snapPosition = SnapPosition.Center
    )

    // 시작 인덱스 설정
    LaunchedEffect(Unit) {
        colorListState.animateScrollToItem(index = 4)
    }

    // 첫 번째 아이템을 선택 (padding 적용으로 가운데 위치한 아이템 -> 첫 번째 아이템)
    LaunchedEffect(colorListState) {
        snapshotFlow { colorListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collectLatest { index -> selectedColorIndex = index }
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
                    // 홀드 색상 선택
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF141478)
                                )
                            ) {
                                append("홀드 색상")
                            }
                            append(" 선택")
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    // 홀드 이미지
                    Image(
                        painter = painterResource(logos[selectedColorIndex]),
                        contentDescription = "logo",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    // 색상 스크롤
                    LazyRow(
                        state = colorListState,
                        flingBehavior = colorFling,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(
                            horizontal = (columnWidthDp / 2 - 15.dp).coerceAtLeast(0.dp)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(grades) { index, grade ->
                            val isColorSelected = index == selectedColorIndex
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(grade.hex.toColor(), shape = CircleShape)
                                    .innerShadow(
                                        CircleShape,
                                        Color(0X80000000),
                                        4.dp,
                                        2.dp,
                                        0.dp,
                                        0.dp
                                    )
                                    .clickable {
                                        coroutineScope.launch {
                                            colorListState.animateScrollToItem(index)
                                        }
                                    }
                            ) {
                                if (isColorSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selected",
                                        tint = if (index == 0) Color.Black else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // 확인 버튼
                    Button(
                        onClick = { onConfirm(grades[selectedColorIndex].color.uppercase()) },
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
fun PreviewSelectColorDialog() {
    SelectColorDialog()
}
