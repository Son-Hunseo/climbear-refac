package com.example.climbear.ui.component

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ThumbnailButton(
    uri: Uri? = null,
    bitmap: Bitmap? = null,
    placeholder: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RectangleShape)
            .clickable { onClick() }
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "갤러리 바로가기",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "갤러리 바로가기",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            placeholder()
        }
    }
}

/**
 * 최근 이미지 Uri 가져오기
 */
fun getLatestImageUri(context: Context): Uri? {
    val projection = arrayOf(MediaStore.Images.Media._ID)

    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Images.Media.DATE_ADDED} DESC"
    )?.use { cursor ->
        if (cursor.moveToNext()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageId = cursor.getLong(idColumn)

            return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId
            )
        } else {
            return null
        }
    }

    return null
}

/**
 * 최근 비디오 썸네일 가져오기
 */
fun getLatestVideoThumbnail(context: Context): Bitmap? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)

    context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Video.Media.DATE_ADDED} DESC"
    )?.use { cursor ->
        if (cursor.moveToNext()) {
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            val videoPath = cursor.getString(dataColumn)

            // API 29 이상은 ThumbnailUtils 사용
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bitmap = ThumbnailUtils.createVideoThumbnail(
                    File(videoPath),
                    Size(512, 512),
                    null
                )
                return bitmap
            } else {
                // 29 미만
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(context, videoPath.toUri())
                return mediaMetadataRetriever.getFrameAtTime(0)
            }
        } else {
            return null
        }
    }

    return null
}