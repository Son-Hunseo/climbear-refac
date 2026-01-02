package com.example.climbear.ui.screen.takepicture

import android.content.ContentValues
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climbear.R
import com.example.climbear.ui.component.CustomCameraBottomBar
import com.example.climbear.ui.component.CustomCameraMessage
import com.example.climbear.ui.component.CustomLongLoadingOverlay
import com.example.climbear.ui.component.CustomOnClickButton
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.ThumbnailButton
import com.example.climbear.ui.component.camera.CameraZoomControl
import com.example.climbear.ui.component.getLatestImageUri
import com.example.climbear.ui.screen.MediaType
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.theme.ClimbearTheme
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun TakePictureScreen(
    onHomeButtonClicked: () -> Unit = {},
    onCenterButtonClicked: (Uri?) -> Unit = {},
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    cameraViewModel: CameraViewModel
) {
    val uploadState by sharedMediaUriViewModel.uploadState.collectAsState()

    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(
            AspectRatioStrategy(
                AspectRatio.RATIO_4_3,
                AspectRatioStrategy.FALLBACK_RULE_AUTO
            )
        )
        .build()

    val imageCaptureUseCase = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(Surface.ROTATION_0)
            .setResolutionSelector(resolutionSelector)
            .build()
    }
    val localContext = LocalContext.current

    LaunchedEffect(Unit) {
        sharedMediaUriViewModel.updateFromMedia(MediaType.CAMERA)
    }

    val coroutineScope = rememberCoroutineScope()

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                sharedMediaUriViewModel.updateImageUri(uri, true)

                coroutineScope.launch {
                    sharedMediaUriViewModel.uploadPicture(localContext, uri)
                }
            } else {
                Log.d("PhotoPicker", "uri 없음")
            }
        }

    Scaffold(
        topBar = {
            CustomToolBar(
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp),
                onLogoClick = onHomeButtonClicked
            )
        },
        bottomBar = {
            CustomCameraBottomBar(
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, bottom = 48.dp),
                startSlot = {
                    ImageThumbnailButton(onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    })
                },
                centerSlot = {
                    CustomOnClickButton(
                        imageRes = R.drawable.capture,
                        contentDescription = "촬영",
                        onClick = {
                            // 이미지를 저장할 임시 파일
                            val uuid = UUID.randomUUID().toString()
                            val name = uuid + "image_${System.currentTimeMillis()}.jpg"
                            val tempFile = File(localContext.cacheDir, name)

                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(tempFile).build()

                            imageCaptureUseCase.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(localContext),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        // MediaStore에 복사
                                        val contentValues = ContentValues().apply {
                                            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                            put(
                                                MediaStore.Images.Media.RELATIVE_PATH,
                                                "DCIM/climbear"
                                            )
                                        }

                                        val contentResolver = localContext.contentResolver
                                        val galleryUri = contentResolver.insert(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            contentValues
                                        )

                                        galleryUri?.let { uri ->
                                            contentResolver.openOutputStream(uri)
                                                ?.use { outputStream ->
                                                    tempFile.inputStream().copyTo(outputStream)
                                                }
                                            sharedMediaUriViewModel.updateImageUri(uri)
                                            coroutineScope.launch {
                                                sharedMediaUriViewModel.uploadPicture(
                                                    localContext,
                                                    uri
                                                )
                                            }
                                        } ?: run {
                                            Log.e(
                                                "TakePicture",
                                                "Failed to insert image to MediaStore"
                                            )
                                        }

                                        tempFile.delete()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e(
                                            "TakePicture",
                                            "Image Save Failed: ${exception.message}",
                                            exception
                                        )
                                    }
                                }
                            )
                        }
                    )
                },
                endSlot = {
                    CustomOnClickButton(
                        imageRes = R.drawable.turn,
                        contentDescription = "카메라 회전",
                        onClick = { cameraViewModel.toggleLensFacing() }
                    )
                }
            )
        }
    ) { innerPadding ->
        CameraScreen(
            imageCaptureUseCase = imageCaptureUseCase,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            resolutionSelector = resolutionSelector,
            cameraViewModel = cameraViewModel
        )
    }

    when (uploadState) {
        is SharedMediaUriViewModel.UploadState.Loading -> {
            CustomLongLoadingOverlay()
        }

        is SharedMediaUriViewModel.UploadState.Success -> {
            onCenterButtonClicked(null)

            sharedMediaUriViewModel.resetUploadState()
        }

        is SharedMediaUriViewModel.UploadState.Error -> {
            val error = (uploadState as SharedMediaUriViewModel.UploadState.Error).message
            LaunchedEffect(error) {
                Toast.makeText(localContext, error, Toast.LENGTH_SHORT).show()
                sharedMediaUriViewModel.resetUploadState()
            }
        }

        else -> Unit
    }
}

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun CameraScreen(
    imageCaptureUseCase: ImageCapture,
    modifier: Modifier = Modifier,
    permissionGranted: Boolean = true,
    resolutionSelector: ResolutionSelector,
    cameraViewModel: CameraViewModel
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()

    var previousZoomLevel by remember { mutableFloatStateOf(cameraState.zoomLevel) }

    val previewUseCase = remember {
        androidx.camera.core.Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
    }

    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    var cameraControl by remember {
        mutableStateOf<CameraControl?>(null)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    val localContext = LocalContext.current

    fun rebindCameraProvider() {
        cameraProvider?.let { cameraProvider ->
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraState.lensFacing)
                .addCameraFilter { cameraInfos ->
                    val mutableCameraInfos = ArrayList(cameraInfos)
                    val filteredCameraInfos = cameraInfos.filter { cameraInfo ->
                        val camera2Info = Camera2CameraInfo.from(cameraInfo)
                        val characteristics = camera2Info.getCameraCharacteristic(
                            CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
                        )
                        if (cameraState.lensFacing == CameraSelector.LENS_FACING_BACK && cameraState.zoomLevel < 1.0f) {
                            characteristics?.any { it < 2.0f } == true
                        } else {
                            characteristics?.any { it < 2.0f } == false
                        }
                    }
                    filteredCameraInfos.ifEmpty {
                        mutableCameraInfos
                    }
                }
                .build()
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                localContext as LifecycleOwner,
                cameraSelector,
                previewUseCase, imageCaptureUseCase
            )
            cameraControl = camera?.cameraControl
            if (cameraState.zoomLevel < 1.0f) {
                cameraControl?.setZoomRatio(cameraState.wideZoomLevel)
            } else {
                cameraControl?.setZoomRatio(cameraState.zoomLevel)
            }
        }
    }

    LaunchedEffect(cameraState.wideZoomLevel, cameraState.zoomLevel) {
        if (cameraViewModel.shouldRebindCamera(previousZoomLevel, cameraState.zoomLevel)) {
            rebindCameraProvider()
        } else {
            // 광각 카메라는 Linear조절
            // 기본 카메라는 zoomRatio 조절
            if (cameraState.zoomLevel < 1.0f) {
                cameraControl?.setZoomRatio(cameraState.wideZoomLevel)
            } else {
                cameraControl?.setZoomRatio(cameraState.zoomLevel)
            }
        }
        previousZoomLevel = cameraState.zoomLevel
    }

    LaunchedEffect(cameraState.lensFacing) {
        rebindCameraProvider()
    }

    LaunchedEffect(Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(localContext)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            rebindCameraProvider()
        }, ContextCompat.getMainExecutor(localContext))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(3f / 4f)
            .clipToBounds()
    ) {
        if (permissionGranted) {
            AndroidView(
                modifier = Modifier
                    .matchParentSize(),
                factory = { context ->
                    val previewView = PreviewView(context).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val scaleGestureDetector = ScaleGestureDetector(
                        context,
                        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                val scaleFactor = detector.scaleFactor
                                var newZoom = cameraState.zoomLevel * scaleFactor

                                // Ensure zoom stays within a reasonable range
                                newZoom = newZoom.coerceIn(0.5f, 4f)

                                // Update zoom level and handle camera switch
                                if (newZoom != cameraState.zoomLevel) {
                                    cameraViewModel.setZoomLevel(newZoom)
                                }

                                return true
                            }
                        }
                    )

                    previewView.setOnTouchListener { view, event ->
                        scaleGestureDetector.onTouchEvent(event)

                        if (event.action == MotionEvent.ACTION_UP) {
                            view.performClick()
                        }

                        true
                    }

                    previewUseCase.surfaceProvider = previewView.surfaceProvider
                    previewView
                }
            )
            if (cameraState.lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraZoomControl(
                    zoomLevel = cameraState.zoomLevel,
                    onZoomChange = { newZoom ->
                        cameraViewModel.setZoomLevel(newZoom)
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                CustomCameraMessage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 24.dp),
                    message = "문제 섹터를 촬영하거나 불러와 주세요.",
                    backgroundColor = Color(0xFF000000).copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        } else {
            Text(text = "카메라 권한이 필요합니다")
        }
    }
}

@Composable
fun ImageThumbnailButton(onClick: () -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        imageUri = getLatestImageUri(context)
    }

    ThumbnailButton(
        uri = imageUri,
        placeholder = {
            Text("갤러리", fontSize = 12.sp, color = Color.White)
        },
        onClick = onClick
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun TakePicturePreview() {
    ClimbearTheme {
        TakePictureScreen(
            cameraViewModel = CameraViewModel(),
            sharedMediaUriViewModel = viewModel()
        )
    }
}
