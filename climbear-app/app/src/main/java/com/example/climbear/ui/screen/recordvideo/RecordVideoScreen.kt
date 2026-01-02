package com.example.climbear.ui.screen.recordvideo

import android.content.ContentValues
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climbear.R
import com.example.climbear.ui.component.CustomCameraBottomBar
import com.example.climbear.ui.component.CustomCameraMessage
import com.example.climbear.ui.component.CustomLongLoadingOverlay
import com.example.climbear.ui.component.CustomOnClickButton
import com.example.climbear.ui.component.CustomOnOffButton
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.ThumbnailButton
import com.example.climbear.ui.component.camera.CameraZoomControl
import com.example.climbear.ui.component.getLatestVideoThumbnail
import com.example.climbear.ui.screen.MediaType
import com.example.climbear.ui.screen.SharedMediaUriViewModel
import com.example.climbear.ui.screen.takepicture.CameraViewModel
import com.example.climbear.ui.theme.ClimbearTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

@Composable
fun RecordVideoScreen(
    onHomeButtonClicked: () -> Unit = {},
    onCenterButtonClicked: () -> Unit = {},
    sharedMediaUriViewModel: SharedMediaUriViewModel,
    cameraViewModel: CameraViewModel
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    val recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
        .build()

    LaunchedEffect(Unit) {
        sharedMediaUriViewModel.updateFromMedia(MediaType.RECORD)
    }

    val videoCapture = VideoCapture.withOutput(recorder)

    var isRecording by remember { mutableStateOf(false) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    var recordingTime by remember { mutableLongStateOf(0L) }

    val timerCoroutineScope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }

    fun onRecord() {
        recordingTime = 0L
        timerJob = timerCoroutineScope.launch {
            while (isActive) {
                delay(1000L)
                recordingTime += 1L
            }
        }
    }

    fun onStop() {
        timerJob?.cancel()
        timerJob = null
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                isLoading = true
                sharedMediaUriViewModel.updateVideoUri(uri, true)

                lifeCycleOwner.lifecycleScope.launch {
                    val retriever = MediaMetadataRetriever()
                    val uuid = UUID.randomUUID().toString()
                    val fileName = "${uuid}image_${System.currentTimeMillis()}.jpg"
                    val photoFile = File(localContext.cacheDir, fileName)

                    try {
                        retriever.setDataSource(localContext, uri)
                        val frameBitmap = retriever.getFrameAtTime(0)

                        if (frameBitmap != null) {
                            withContext(Dispatchers.IO) {
                                FileOutputStream(photoFile).use { out ->
                                    frameBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                                }
                            }

                            sharedMediaUriViewModel.uploadFrame(photoFile, fileName)

                            onCenterButtonClicked()
                        } else {
                            Log.e("Media", "프레임 추출 실패")
                        }
                    } catch (e: Exception) {
                        Log.e("Media", "첫 프레임 추출 실패", e)
                    } finally {
                        retriever.release()
                        photoFile.delete()
                        isLoading = false
                    }
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
                    VideoThumbnailButton(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                    )
                },
                centerSlot = {
                    CustomOnOffButton(
                        activeImageRes = R.drawable.record_active,
                        inactiveImageRes = R.drawable.record,
                        contentDescription = "촬영",
                        active = isRecording,
                        onClick = {
                            if (!isRecording) {
                                onRecord()

                                val name = "video_${System.currentTimeMillis()}.mp4"
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                                    put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/climbear")
                                }

                                val mediaStoreOutput = MediaStoreOutputOptions.Builder(
                                    localContext.contentResolver,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                ).setContentValues(contentValues).build()

                                val recording = videoCapture.output
                                    .prepareRecording(localContext, mediaStoreOutput)
                                    .start(ContextCompat.getMainExecutor(localContext)) { event ->
                                        when (event) {
                                            is VideoRecordEvent.Start -> {
                                                isRecording = true
                                            }

                                            is VideoRecordEvent.Finalize -> {
                                                isRecording = false
                                                if (event.hasError()) {
                                                    Log.e(
                                                        "TakeVideo",
                                                        "Recording error: ${event.error}"
                                                    )
                                                } else {
                                                    val uri = event.outputResults.outputUri

                                                    if (uri != Uri.EMPTY) {
                                                        sharedMediaUriViewModel.updateVideoUri(uri)

                                                        lifeCycleOwner.lifecycleScope.launch {
                                                            val retriever = MediaMetadataRetriever()
                                                            val uuid = UUID.randomUUID().toString()
                                                            val fileName =
                                                                "${uuid}image_${System.currentTimeMillis()}.jpg"
                                                            val photoFile = File(
                                                                localContext.cacheDir,
                                                                fileName
                                                            )

                                                            try {
                                                                retriever.setDataSource(
                                                                    localContext,
                                                                    uri
                                                                )
                                                                val frameBitmap =
                                                                    retriever.getFrameAtTime(0)

                                                                if (frameBitmap != null) {
                                                                    withContext(Dispatchers.IO) {
                                                                        FileOutputStream(photoFile).use { out ->
                                                                            frameBitmap.compress(
                                                                                Bitmap.CompressFormat.JPEG,
                                                                                100,
                                                                                out
                                                                            )
                                                                        }
                                                                    }

                                                                    sharedMediaUriViewModel.uploadFrame(
                                                                        photoFile,
                                                                        fileName
                                                                    )

                                                                } else {
                                                                    Log.e("Media", "프레임 추출 실패")
                                                                }
                                                            } catch (e: Exception) {
                                                                Log.e("Media", "첫 프레임 추출 중 오류", e)
                                                            } finally {
                                                                retriever.release()
                                                                photoFile.delete()
                                                                onCenterButtonClicked()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                activeRecording = recording
                            } else {
                                onStop()
                                activeRecording?.stop()
                                activeRecording = null
                            }
                        }
                    )
                },
                endSlot = {
                    CustomOnClickButton(
                        imageRes = R.drawable.turn,
                        contentDescription = "촬영 방향 전환",
                        modifier = Modifier,
                        onClick = { cameraViewModel.toggleLensFacing() }
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        CameraScreen(
            isRecording = isRecording,
            recordingTime = recordingTime,
            modifier = Modifier.padding(innerPadding),
            videoCapture = videoCapture,
            cameraViewModel = cameraViewModel
        )
    }

    if (isLoading) {
        CustomLongLoadingOverlay()
    }
}


@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun CameraScreen(
    isRecording: Boolean,
    recordingTime: Long,
    modifier: Modifier = Modifier,
    videoCapture: VideoCapture<Recorder>? = null,
    cameraViewModel: CameraViewModel
) {
    val cameraState by cameraViewModel.cameraState.collectAsState()

    var previousZoomLevel by remember { mutableFloatStateOf(cameraState.zoomLevel) }

    val previewUseCase = remember { androidx.camera.core.Preview.Builder().build() }

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

    fun bindCameraProvider() {
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
                previewUseCase, videoCapture
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
            bindCameraProvider()
        } else {
            if (cameraState.zoomLevel < 1.0f) {
                cameraControl?.setZoomRatio(cameraState.wideZoomLevel)
            } else {
                cameraControl?.setZoomRatio(cameraState.zoomLevel)
            }
        }
        previousZoomLevel = cameraState.zoomLevel
    }

    // 렌즈 방향이 바뀌면 다시 binding
    LaunchedEffect(cameraState.lensFacing) {
        bindCameraProvider()
    }

    LaunchedEffect(Unit) {
        val providerFuture = ProcessCameraProvider.getInstance(localContext)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            bindCameraProvider()
        }, ContextCompat.getMainExecutor(localContext))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(3f / 4f)
            .clipToBounds()
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
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
        if (isRecording) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format(
                        Locale.KOREA,
                        "%02d:%02d",
                        recordingTime / 60,
                        recordingTime % 60
                    ),
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Red, shape = RoundedCornerShape(32.dp))
                        .padding(start = 8.dp, end = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                CustomCameraMessage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 24.dp),
                    message = "녹화 시작 후 문제를 풀이해 주세요.",
                    backgroundColor = Color(0xFF000000).copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
        if (cameraState.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraZoomControl(
                zoomLevel = cameraState.zoomLevel,
                onZoomChange = { newZoom ->
                    cameraViewModel.setZoomLevel(newZoom)
                }
            )
        }
    }
}

@Composable
fun VideoThumbnailButton(onClick: () -> Unit) {
    val context = LocalContext.current
    var videoThumbnail by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        videoThumbnail = getLatestVideoThumbnail(context)
    }

    ThumbnailButton(
        bitmap = videoThumbnail,
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
fun RecordVideoPreview() {
    ClimbearTheme {
        RecordVideoScreen(
            sharedMediaUriViewModel = viewModel(),
            cameraViewModel = CameraViewModel()
        )
    }
}