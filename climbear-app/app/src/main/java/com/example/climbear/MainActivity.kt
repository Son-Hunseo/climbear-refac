package com.example.climbear

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.climbear.data.UserPreferencesRepository
import com.example.climbear.data.auth.TokenManager
import com.example.climbear.ui.theme.ClimbearTheme
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClimbearTheme(darkTheme = false) {
                ClimbearApp()
            }
        }
    }
}

@HiltAndroidApp
class ClimbearApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        // sharedPreferences 초기화
        TokenManager.init(this)
        TokenManager.loadTokens()
    }
}