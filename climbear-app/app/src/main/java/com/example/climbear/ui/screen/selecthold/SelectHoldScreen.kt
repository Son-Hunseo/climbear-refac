package com.example.climbear.ui.screen.selecthold

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import com.example.climbear.R
import com.example.climbear.ui.component.CustomCameraMessage
import com.example.climbear.ui.component.CustomLongLoadingOverlay
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.selectHold.ConfirmDialog
import com.example.climbear.ui.component.selecthold.SelectColorDialog
import com.example.climbear.ui.component.selecthold.SelectLevelDialog
import com.example.climbear.ui.screen.ImageState
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.home.HomeViewModel
import com.example.climbear.ui.theme.ClimbearTheme
import com.google.android.gms.location.LocationServices

@Composable
fun SelectHoldScreen(
    onHomeButtonClicked: () -> Unit = {},
    navigateToSolutionHint: () -> Unit = {},
    navigateToBack: () -> Unit = {},
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    selectHoldViewModel: SelectHoldViewModel,
    homeViewModel: HomeViewModel,
) {
    val uiState by selectHoldViewModel.uiState.collectAsState()
    val holdStepState by selectHoldViewModel.holdStepState.collectAsState()
    val imageState by sharedMediaUriViewModel.imageState.collectAsState()
    val rankState by homeViewModel.rankState.collectAsState()

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val message = holdStepState.warningMessage ?: holdStepState.guideMessage
    val messageColor =
        if (holdStepState.warningMessage == null) Color(0xFF141478) else Color(0xFFB84D4F)

    val pictureUrl =
        "${sharedMediaUriViewModel.S3_BASE_URL}${sharedMediaUriViewModel.fileNameWithUuid}"

    BackHandler {
        if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
            navigateToBack()
        } else {
            selectHoldViewModel.goToPreviousStep()
        }
    }

    // 상태 관리, pictureUrl 변경 필요 (현재, Compsose 그려질 때)
    LaunchedEffect(Unit) {
        // 미디어 스토어에 저장된 사진이 아닐 때
        if (!sharedMediaUriViewModel.isPickedInMediastore) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            selectHoldViewModel.updateLocation(
                                it.latitude,
                                it.longitude
                            ) // ViewModel에 전달
                            Log.d("selecthold", "위치 저장")
                        }
                    }
            }
        }

        // 테스트 이미지
//        selectHoldViewModel.detectHold("https://climbear-bucket.s3.ap-northeast-2.amazonaws.com/hold_image/raw_image/test1.jpg")
//        selectHoldViewModel.detectHold("${sharedMediaUriViewModel.S3_BASE_URL}${sharedMediaUriViewModel.fileNameWithUuid}")
        selectHoldViewModel.detectHold(pictureUrl)
//        Log.d("hold", "홀드 호출 시작 ${sharedMediaUriViewModel.S3_BASE_URL}${sharedMediaUriViewModel.fileNameWithUuid}")
    }

    Scaffold(
        topBar = {
            CustomToolBar(
                onLogoClick = onHomeButtonClicked
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            SelectHoldMain(
                sharedMediaUriViewModel = sharedMediaUriViewModel,
                selectHoldViewModel = selectHoldViewModel,
                uiState = uiState,
                imageState = imageState,
                holdStepState = holdStepState
            )

            // 바텀 바
            SelectHoldBottomBar(
                message = message,
                backgroundColor = messageColor,
                selectHoldViewModel = selectHoldViewModel,
                holdStepState = holdStepState,
                navigateToBack = navigateToBack,
                navigateToSolutionHint = navigateToSolutionHint,
                pictureUrl = pictureUrl
            )
        }
    }

    // 로딩 오버레이
    if (!holdStepState.isHoldLoaded) {
        CustomLongLoadingOverlay()
    }

    // 레벨 선택
    if (holdStepState.currentStep == SelectHoldStep.SELECT_COLOR) {
        SelectColorDialog (
            onConfirm = { color ->
                selectHoldViewModel.setColor(color)
                selectHoldViewModel.goToNextStep()
            },
            onDismiss = selectHoldViewModel::goToPreviousStep
        )
    }

    // 레벨 선택
    if (holdStepState.currentStep == SelectHoldStep.SELECT_LEVEL) {
        SelectLevelDialog (
            userLevelText = rankState.levelName,
            onConfirm = { level ->
                selectHoldViewModel.setLevel(level)
                selectHoldViewModel.goToNextStep()
            },
            onDismiss = selectHoldViewModel::goToPreviousStep
        )
    }
}

@Composable
fun SelectHoldMain(
    modifier: Modifier = Modifier,
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    selectHoldViewModel: SelectHoldViewModel,
    uiState: HoldSelectUiState,
    holdStepState: HoldStepState,
    imageState: ImageState,
) {
    val context = LocalContext.current
    val zendots = ResourcesCompat.getFont(context, R.font.zendots_regular)

    val originalImageWidth = imageState.originalWidth
    val originalImageHeight = imageState.originalHeight
    var imageSizePx by remember { mutableStateOf(IntSize.Zero) }

    val imageToOriginalScaleX = imageSizePx.width.toFloat() / originalImageWidth
    val imageToOriginalScaleY = imageSizePx.height.toFloat() / originalImageHeight

    val reScaleX = originalImageWidth / imageSizePx.width.toFloat()
    val reScaleY = originalImageHeight / imageSizePx.height.toFloat()

    val zoomScale = if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) 3f else 1f
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(holdStepState.currentStep) {
        if (holdStepState.currentStep != SelectHoldStep.SELECT_GRID) {
            offset = Offset.Zero
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clipToBounds()
            .pointerInput(holdStepState.currentStep) {
                if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
                    detectTransformGestures { _, pan, _, _ ->
                        val newOffset = offset + pan

                        offset = Offset(
                            newOffset.x.coerceIn(
                                -imageSizePx.width.toFloat(),
                                imageSizePx.width.toFloat()
                            ),
                            newOffset.y.coerceIn(
                                -imageSizePx.height.toFloat(),
                                imageSizePx.height.toFloat()
                            )
                        )
                    }
                } else {
                    detectTapGestures { tapOffset ->
                        val x = tapOffset.x * reScaleX
                        val y = tapOffset.y * reScaleY
                        when (holdStepState.currentStep) {
                            SelectHoldStep.DETECT_HOLDS -> selectHoldViewModel.toggleSelectedHold(x, y)
                            SelectHoldStep.SELECT_START -> selectHoldViewModel.toggleStartHold(x, y)
                            SelectHoldStep.SELECT_TOP -> selectHoldViewModel.toggleTopHold(x, y)
                            else -> {}
                        }
                    }
                }
            }
            .pointerInput(holdStepState.currentStep) {
                if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
                    detectTapGestures { tapOffset ->
                        Log.d("StepCheck", "tapoffset ${tapOffset.x} ${tapOffset.y}")
                        Log.d("StepCheck", "offset ${offset.x} ${offset.y}")
                        val transformedX =
                            (tapOffset.x + imageSizePx.width - offset.x) / zoomScale * reScaleX
                        val transformedY =
                            (tapOffset.y + imageSizePx.height - offset.y) / zoomScale * reScaleY
                        selectHoldViewModel.addGridPoint(transformedX, transformedY)
                    }
                }
            }
            .graphicsLayer {
                scaleX = zoomScale
                scaleY = zoomScale
                translationX = offset.x
                translationY = offset.y
            },
    ) {
        sharedMediaUriViewModel?.let {
            AsyncImage(
                model = sharedMediaUriViewModel.imageUri,
                contentDescription = "암벽 사진",
                modifier = Modifier
                    .matchParentSize()
                    .onGloballyPositioned { coordinates ->
                        imageSizePx = coordinates.size
                    },
//                    .graphicsLayer {
//                        scaleX = zoomScale
//                        scaleY = zoomScale
//                        translationX = offset.x
//                        translationY = offset.y
//                    },
                contentScale = ContentScale.FillBounds,
            )
        } ?: Text("뒤로 가기 후 재촬영해 주세요.")

        val holdMap = uiState.holdCoordinates.associateBy { it.holdId }

        Canvas(
            modifier = Modifier
                .matchParentSize()
//                .graphicsLayer {
//                    scaleX = zoomScale
//                    scaleY = zoomScale
//                    translationX = offset.x
//                    translationY = offset.y
//                }
        ) {
//            if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
//                uiState.gridPoints.forEach { point ->
//                    drawCircle(
//                        color = Color.White,
//                        radius = 5f,
//                        center = Offset(
//                            point.x * imageToOriginalScaleX,
//                            point.y * imageToOriginalScaleY
//                        )
//                    )
//                }
//            }

            uiState.holdCoordinates
                .filterNot { it.holdId in uiState.selectedHoldIds }
                .forEach { polygon ->
                    val path = Path()
                    val points = polygon.coordinates.map {
                        Offset(it.x * imageToOriginalScaleX, it.y * imageToOriginalScaleY)
                    }

                    if (points.isNotEmpty()) {
                        path.moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach {
                            path.lineTo(it.x, it.y)
                        }
                        path.close()

                        drawPath(
                            path = path,
                            color = Color(0x80FFFFFF),
                            style = Stroke(width = 5f)
                        )
                    }
                }

            uiState.selectedHoldIds.forEach { id ->
                val hold = holdMap[id] ?: return@forEach
                val points = hold.coordinates.map {
                    Offset(it.x * imageToOriginalScaleX, it.y * imageToOriginalScaleY)
                }
                if (points.isNotEmpty()) {
                    val path = Path()
                    path.moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach {
                        path.lineTo(it.x, it.y)
                    }
                    path.close()
                    drawPath(path, Color.White, style = Stroke(width = 10f))
                }
            }

//            uiState.startHoldIds.forEach { id ->
//                val hold = holdMap[id] ?: return@forEach
//                if (hold.average == null) return@forEach
//                val center = Offset(
//                    hold.average.x * imageToOriginalScaleX,
//                    hold.average.y * imageToOriginalScaleY
//                )
//                drawContext.canvas.nativeCanvas.drawText(
//                    "S", center.x, center.y,
//                    android.graphics.Paint().apply {
//                        color = Color.White.toArgb()
//                        textSize = 40f
//                        isFakeBoldText = true
//                        textAlign = android.graphics.Paint.Align.CENTER
//                        typeface = zendots
//                    }
//                )
//            }
//
//            uiState.topHoldId?.let { id ->
//                val hold = holdMap[id] ?: return@let
//                if (hold.average == null) return@let
//                val center = Offset(
//                    hold.average.x * imageToOriginalScaleX,
//                    hold.average.y * imageToOriginalScaleY
//                )
//                drawContext.canvas.nativeCanvas.drawText(
//                    "T", center.x, center.y,
//                    android.graphics.Paint().apply {
//                        color = Color.White.toArgb()
//                        textSize = 50f
//                        isFakeBoldText = true
//                        textAlign = android.graphics.Paint.Align.CENTER
//                        typeface = zendots
//                    }
//                )
//            }
        }

        // 이미지
        // 그리드 포인트
        if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
            uiState.gridPoints.forEach { point ->
                val center = Offset(
                    point.x * imageToOriginalScaleX,
                    point.y * imageToOriginalScaleY
                )
                val offsetX = center.x - with(LocalDensity.current) { 5.dp.toPx() }
                val offsetY = center.y - with(LocalDensity.current) { 5.dp.toPx() }
                Image(
                    painter = painterResource(id = R.drawable.spot),
                    contentDescription = null,
                    modifier = Modifier
                        .size(10.dp)
                        .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                )
            }
        }
        // 스타트 홀드
        uiState.startHoldIds.forEach { id ->
            val hold = holdMap[id] ?: return@forEach
            if (hold.average == null) return@forEach
            val center = Offset(
                hold.average.x * imageToOriginalScaleX,
                hold.average.y * imageToOriginalScaleY
            )
            val offsetX = center.x - with(LocalDensity.current) { 20.dp.toPx() }
            val offsetY = center.y - with(LocalDensity.current) { 20.dp.toPx() }
            Image(
                painter = painterResource(id = R.drawable.startcircle),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            )
        }
        // 탑 홀드
        uiState.topHoldId?.let { id ->
            val hold = holdMap[id] ?: return@let
            if (hold.average == null) return@let
            val center = Offset(
                hold.average.x * imageToOriginalScaleX,
                hold.average.y * imageToOriginalScaleY
            )
            val offsetX = center.x - with(LocalDensity.current) { 20.dp.toPx() }
            val offsetY = center.y - with(LocalDensity.current) { 20.dp.toPx() }
            Image(
                painter = painterResource(id = R.drawable.topcircle),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            )
        }
        if (holdStepState.currentStep == SelectHoldStep.COMPLETE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                ConfirmDialog(uiState = uiState)
            }
        }
    }
}


@Composable
fun SelectHoldBottomBar(
    selectHoldViewModel: SelectHoldViewModel,
    backgroundColor: Color,
    holdStepState: HoldStepState,
    message: String = "홀드를 선택해 주세요.",
    navigateToBack: () -> Unit = {},
    navigateToSolutionHint: () -> Unit = {},
    pictureUrl: String
) {
    // 하단 버튼
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 안내 메시지
        CustomCameraMessage(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            message = message,
            backgroundColor = backgroundColor,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 이전 & 다음 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // 좌측 버튼
            IconButton(
                onClick = {
                    if (holdStepState.currentStep == SelectHoldStep.SELECT_GRID) {
                        navigateToBack()
                    } else {
                        selectHoldViewModel.goToPreviousStep()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
//                    .background(Color(0xFFFFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color(0xFF141478),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            // AI 버튼 조건
            val isAiEnabled = (holdStepState.currentStep == SelectHoldStep.DETECT_HOLDS && holdStepState.isHoldLoaded)
            Button(
                onClick = { selectHoldViewModel.classifyColor(pictureUrl) },
                modifier = Modifier
                    .height(48.dp),
//                        .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFFFFF),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFFFFFFFF)
                ),
                enabled = isAiEnabled
            ) {
                Icon(
                    painter = if (isAiEnabled) painterResource(id = R.drawable.ailong) else painterResource(id = R.drawable.centerbar),
                    contentDescription = "AI",
                    modifier = Modifier
//                        .fillMaxHeight()
                        .width(120.dp)
                        .padding(4.dp),
                    tint = Color(0xFF141478)
                )
            }

            // 우측 버튼
            IconButton(
                onClick = {
                    if (holdStepState.currentStep == SelectHoldStep.COMPLETE) {
                        selectHoldViewModel.postProblem(onSuccess = navigateToSolutionHint)
                    } else {
                        selectHoldViewModel.goToNextStep()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
//                    .background(Color(0xFFFFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color(0xFF141478),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun SelectHoldPreview() {
    ClimbearTheme {
//        SelectHoldButtomBar()
    }
}

