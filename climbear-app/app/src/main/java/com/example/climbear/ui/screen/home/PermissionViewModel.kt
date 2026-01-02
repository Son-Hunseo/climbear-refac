package com.example.climbear.ui.screen.home

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private var _cameraPermission =
        MutableStateFlow(checkPermission(Manifest.permission.CAMERA))
    val cameraPermission: StateFlow<Boolean> = _cameraPermission

    private var _locationPermission =
        MutableStateFlow(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    val locationPermission: StateFlow<Boolean> = _locationPermission

    var hasShownLocationRationale by mutableStateOf(false)
        private set

    fun makrLocationRationaleShown() {
        hasShownLocationRationale = true
    }

    fun refreshPermissions() {
        _cameraPermission.value = checkPermission(Manifest.permission.CAMERA)
        _locationPermission.value = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            getApplication(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}