package com.example.climbear.ui.screen

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.climbear.data.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject

data class ImageState(
    val originalWidth: Int = 3024,
    val originalHeight: Int = 4032
)

enum class MediaType {
    CAMERA,
    RECORD
}

@HiltViewModel
class SharedMediaUriViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        object Success : UploadState()
        data class Error(val message: String) : UploadState()
    }

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _fromMedia = MutableStateFlow<MediaType>(MediaType.CAMERA)
    val fromMedia: StateFlow<MediaType> = _fromMedia

    fun updateFromMedia(mediaType: MediaType) {
        _fromMedia.value = mediaType
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    var imageUri by mutableStateOf<Uri?>(null)
        private set
    var fileNameWithUuid by mutableStateOf<String?>(null)
        private set

    var isPickedInMediastore by mutableStateOf(false)
        private set

    var videoUri by mutableStateOf<Uri?>(null)
        private set

    val S3_BASE_URL =
        "https://climbear-bucket.s3.ap-northeast-2.amazonaws.com/hold_image/raw_image/"

    fun updateImageUri(uri: Uri, isPicked: Boolean = false) {
        imageUri = uri
        isPickedInMediastore = isPicked
    }

    fun updateVideoUri(uri: Uri, isPicked: Boolean = false) {
        videoUri = uri
        isPickedInMediastore = isPicked
    }

    private val _imageState = MutableStateFlow<ImageState>(ImageState())
    val imageState: StateFlow<ImageState> = _imageState

    suspend fun getPresignedUrl(context: Context, uri: Uri, inAppMedia: Boolean): String? {
        val fileName = getFileName(context, uri)

        if (fileName == null) {
            return null
        }

        if (inAppMedia) {
            fileNameWithUuid = fileName
        } else {
            val uuid = UUID.randomUUID().toString()
            fileNameWithUuid = uuid + fileName
        }

        val url = imageRepository.getPresignedUrl(fileNameWithUuid!!).getOrNull()?.url

        return url.toString()
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun copyImageToClimbearFolder(context: Context, uri: Uri, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/climbear")
        }

        val contentResolver = context.contentResolver
        val targetUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (targetUri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = contentResolver.openOutputStream(targetUri)

                inputStream?.use { input ->
                    outputStream?.use { output ->
                        input.copyTo(output)
                    }
                }
                return targetUri
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    // uuid가 붙은 이미지 파일 생성
    private fun copyFile(context: Context, uri: Uri, fileName: String, inAppMedia: Boolean): File? {
        var targetUri: Uri? = null

        if (!inAppMedia) {
            targetUri = copyImageToClimbearFolder(context, uri, fileName)
        } else {
            targetUri = uri
        }

        if (targetUri == null) {
            return null
        }

        val newFile = File(context.cacheDir, fileName)

        return try {
            context.contentResolver.openInputStream(targetUri)?.use { input ->
                newFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            newFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun uploadPicture(context: Context, uri: Uri) {
        _uploadState.value = UploadState.Loading

        try {
            // DCIM/climbear에 존재하는 파일인지 확인
            val inAppMedia = isFromClimbearFolder(context, uri)

            // 1. presignedUrl을 받아오기
            val presignedUrl =
                getPresignedUrl(context, uri, inAppMedia) ?: throw Exception("url 조회 실패")

            // 2. uuid 붙은 이미지 파일 생성
            val imageFile =
                copyFile(context, uri, fileNameWithUuid!!, inAppMedia)
                    ?: throw IOException("파일 복사 실패")

            // 3. presignedUrl에 이미지 저장
            val uploadSuccess = uploadFileToPresignedUrl(presignedUrl, imageFile)

            if (uploadSuccess) {
                _uploadState.value = UploadState.Success
                val imageSize = getImageSize(context, uri)
                if (imageSize != null) {
                    _imageState.value = ImageState(
                        originalWidth = imageSize.first,
                        originalHeight = imageSize.second
                    )
                }
            } else {
                _uploadState.value = UploadState.Error("업로드 실패")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _uploadState.value = UploadState.Error(e.message ?: "오륲")
        }
    }

    suspend fun uploadFrame(file: File, fileName: String) {
        try {
            val presignedUrl = imageRepository.getPresignedUrl(fileName).getOrNull()?.url
                ?: throw Exception("url 조회 실패")
            val uploadSuccess = uploadFileToPresignedUrl(presignedUrl.toString(), file)
            // upload 성공, 실패했을때 분기 필요
            if (uploadSuccess) {
                fileNameWithUuid = fileName
                val uri = (S3_BASE_URL + fileName).toUri()
                imageUri = uri
                Log.d("hold", "$uri")

                val imageSize = getImageSize(file)
                if (imageSize != null) {
                    _imageState.value = ImageState(
                        // 90도 회전
                        originalWidth = imageSize.first,
                        originalHeight = imageSize.second
                    )

                    Log.d("hold", "사이즈 - original ${imageSize.second} x ${imageSize.first}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // DCIM/climbear 폴더에 있는지 확인
    private fun isFromClimbearFolder(context: Context, uri: Uri): Boolean {
        val resolver = context.contentResolver

        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            if (split.size == 2 && split[0] == "image") {
                val imageId = split[1].toLong()
                val mediaUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )

                val projection = arrayOf(MediaStore.Images.Media.RELATIVE_PATH)
                resolver.query(
                    mediaUri,
                    projection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val index = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
                    if (cursor.moveToFirst() && index != -1) {
                        val path = cursor.getString(index)
                        return path?.contains("DCIM/climbear") == true
                    }
                }
            }
        }

        val projection = arrayOf(
            MediaStore.Images.Media.RELATIVE_PATH
        )

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val relativePathIndex = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
            if (cursor.moveToFirst() && relativePathIndex != -1) {
                val relativePath = cursor.getString(relativePathIndex)
                return relativePath?.contains("DCIM/climbear") == true
            }
        }

        return false
    }

    suspend fun uploadFileToPresignedUrl(presignedUrl: String, file: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val mediaType = "image/jpeg"
                val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(presignedUrl)
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun getImageSize(context: Context, uri: Uri): Pair<Int, Int>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val width = options.outWidth
            val height = options.outHeight

            if (width > 0 && height > 0) {
                if (width > height) Pair(height, width)
                else Pair(width, height)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getImageSize(file: File): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            FileInputStream(file).use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            val width = options.outWidth
            val height = options.outHeight

            if (width > 0 && height > 0) {
                if (width > height) Pair(height, width)
                else Pair(width, height)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}