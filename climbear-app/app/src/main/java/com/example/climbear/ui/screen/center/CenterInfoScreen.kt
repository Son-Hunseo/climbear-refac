package com.example.climbear.ui.screen.center

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.climbear.ui.component.CustomToolBar
import com.example.climbear.ui.component.center.CalendarHeatmap
import com.example.climbear.ui.component.center.CenterItem
import com.example.climbear.ui.component.center.Maps
import com.example.climbear.ui.component.center.RecordGraph
import com.example.climbear.util.loadGrades
import com.example.climbear.util.toColor
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@Composable
fun CenterInfoScreen(
    moveToHome: () -> Unit = {},
    centerInfoViewModel: CenterInfoViewModel = hiltViewModel()
) {
    val centerUiState by centerInfoViewModel.centerUiState.collectAsState()
    val centerMyUiState by centerInfoViewModel.centerMyUiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val grades = loadGrades(context)
    val colors = grades.map { it.hex.toColor() }

    val merged = remember(
        centerUiState.centerList,
        centerMyUiState.centerData
    ) {
        centerUiState.centerList
            .map { display ->
                val my = centerMyUiState.centerData
                    .find { it.centerName == display.name }
                display to my
            }
            .sortedWith(
                compareBy<Pair<CenterDisplayData, CenterVisitWithCounts?>> { pair ->
                    pair.second == null
                }
                    .thenByDescending { pair ->
                        pair.second?.visitDates?.size ?: 0
                    }
                    .thenBy { pair ->
                        pair.first.rawDist ?: Float.MAX_VALUE
                    }
            )
    }

    var selectedName by remember { mutableStateOf<String?>(null) }
    var selectedPoint by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        centerInfoViewModel.updateLocation(it.latitude, it.longitude)
                    }
                }
        }
        centerInfoViewModel.getCenterList()
        centerInfoViewModel.getCenterMy(context)
    }

    Scaffold(
        topBar = {
            CustomToolBar(
                onLogoClick = moveToHome
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .fillMaxSize()
        ) {

            // 지도
            Maps(
                centers = merged.map { it.first },
                selectedPoint = selectedPoint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) { center ->
                selectedPoint = LatLng(center.latitude, center.longitude)
                selectedName = center.name
            }

            Spacer(modifier = Modifier.padding(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(merged) { index, (display, my) ->
                    CenterItem(
                        name = display.name,
                        address = display.address,
                        distance = display.distanceMeters,
                        isSelected = display.name == selectedName,
                        isMy = my != null,
                        onClick = {
                            selectedName = display.name
                            selectedPoint = LatLng(display.latitude, display.longitude)
                        }
                    ) {
                        if (my != null && display.name == selectedName) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp), // 내부 여백
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RecordGraph(
                                        problemCounts = my.problemCounts,
                                        colors = colors,
                                        modifier = Modifier
                                            .weight(2f)
                                            .fillMaxHeight()
                                    )
                                    CalendarHeatmap(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        visitDates = my.visitDates,
                                        centerInfoViewModel = centerInfoViewModel
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}