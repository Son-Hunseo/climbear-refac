package com.example.climbear.ui.component.center

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import androidx.compose.ui.tooling.preview.Preview
import com.example.climbear.ui.screen.center.CenterDisplayData
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.style.MapStyle

@Composable
fun Mapbox(
    modifier: Modifier = Modifier,
    centers: List<CenterDisplayData> = emptyList(),
    selectedPoint: Point? = null,
    onMarkerClick: (CenterDisplayData) -> Unit
) {
    val seoul = Point.fromLngLat(127.0016985, 37.5642135)
    val viewportState = rememberMapViewportState {
        if (centers.isNotEmpty()) {
            val lats = centers.map { it.latitude }
            val lons = centers.map { it.longitude }
            val north = lats.maxOrNull()!!
            val south = lats.minOrNull()!!
            val east  = lons.maxOrNull()!!
            val west  = lons.minOrNull()!!
            setCameraOptions {
                center(Point.fromLngLat((east + west) / 2, (north + south) / 2))
                zoom(if (east - west > north - south) 10.0 else 11.0)
            }
        } else {
            setCameraOptions {
                zoom(10.0)
                center(seoul)
            }
        }
    }
    // 마커 아이콘 등록
    val markerIcon = rememberIconImage(
        key = "seoul_marker",
        painter = painterResource(id = com.example.climbear.R.drawable.logo_used)
    )

    // 선택된 포인트가 바뀌면 카메라 이동
    LaunchedEffect(selectedPoint) {
        selectedPoint?.let { point ->
            viewportState.setCameraOptions(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(14.0)
                    .build()
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState,
            // 아래에 style 파라미터로 내 스타일 적용
            style = { MapStyle(style = "mapbox://styles/sharmx1am/cmaw13d0b01n301sp47vabjx3") } // ← 여기에 본인 스타일 URL 입력
        ) {

            centers.forEach { center ->
                val point = Point.fromLngLat(center.longitude, center.latitude)
                PointAnnotation(point = point) {
                    iconImage = markerIcon
                    textField = center.name
                    interactionsState.onClicked {
                        // 여기서 부모로 클릭 이벤트 전달
                        onMarkerClick(center)
                        true  // 이벤트 소비
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapboxPreview() {
//    Mapbox()
}


//@Composable
//fun MapWithCenters(
//    centers: List<CenterDisplayData>,
//    selectedPoint: Point?,
//    modifier: Modifier = Modifier,
//    onMarkerClick: (CenterDisplayData) -> Unit
//) {
//    // 초기 뷰포트: 모든 마커가 보이도록 설정
//    val viewportState = rememberMapViewportState {
//        if (centers.isNotEmpty()) {
//            val lats = centers.map { it.latitude }
//            val lons = centers.map { it.longitude }
//            val north = lats.maxOrNull()!!
//            val south = lats.minOrNull()!!
//            val east  = lons.maxOrNull()!!
//            val west  = lons.minOrNull()!!
//            setCameraOptions {
//                center(Point.fromLngLat((east + west) / 2, (north + south) / 2))
//                zoom(if (east - west > north - south) 10.0 else 11.0)
//            }
//        }
//    }
//
//    // 선택된 포인트가 바뀌면 카메라 이동
//    LaunchedEffect(selectedPoint) {
//        selectedPoint?.let { point ->
//            viewportState.setCameraOptions(
//                CameraOptions.Builder()
//                    .center(point)
//                    .zoom(14.0)
//                    .build()
//            )
//        }
//    }
//
//    Box(modifier) {
//        MapboxMap(
//            modifier = Modifier.fillMaxSize(),
//            mapViewportState = viewportState,
//            style = { MapStyle(style = "mapbox://styles/your_username/your_style_id") }
//        ) {
//            centers.forEach { center ->
//                val point = Point.fromLngLat(center.longitude, center.latitude)
//                val icon = rememberIconImage(
//                    key = "marker_${center.centerId}",
//                    painter = painterResource(id = R.drawable.logo_used)
//                )
//                PointAnnotation(point = point) {
//                    iconImage = icon
//                    textField = center.name
//                    onClick = {
//                        onMarkerClick(center)
//                        true
//                    }
//                }
//            }
//        }
//    }
//}