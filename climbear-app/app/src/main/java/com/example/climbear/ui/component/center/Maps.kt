package com.example.climbear.ui.component.center

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.climbear.R
import com.example.climbear.ui.screen.center.CenterDisplayData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.scale

@Composable
fun Maps(
    modifier: Modifier = Modifier,
    centers: List<CenterDisplayData> = emptyList(),
    selectedPoint: LatLng? = null,
    onMarkerClick: (CenterDisplayData) -> Unit
) {
    // 기본 위치: centers 가 있으면 경계의 중앙, 없으면 서울
    val defaultPosition = remember(centers) {
        if (centers.isNotEmpty()) {
            val avgLat = (centers.minOf { it.latitude } + centers.maxOf { it.latitude }) / 2
            val avgLng = (centers.minOf { it.longitude } + centers.maxOf { it.longitude }) / 2
            LatLng(avgLat, avgLng)
        } else {
            LatLng(37.5642135, 127.0016985)
        }
    }

    // 카메라 상태
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }

    // selectedLatLng 가 바뀌면 카메라 애니메이션
    LaunchedEffect(selectedPoint) {
        selectedPoint?.let { latLng ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 14f)
            )
        }
    }

    // 아이콘
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.solve_location)
    val resized = bitmap.scale(90, 90, false)

    Box(modifier = modifier
        .fillMaxWidth()
        .height(250.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            centers.forEach { center ->
                val position = LatLng(center.latitude, center.longitude)
                Marker(
                    icon = BitmapDescriptorFactory.fromBitmap(resized),
                    state = MarkerState(position = position),
                    title = center.name,
                    snippet = center.address,
                    onClick = {
                        onMarkerClick(center)
                        true  // 이벤트 소비: 다른 리스너나 기본 맵 동작이 발생하지 않도록
                    }
                )
            }
        }
    }
}
