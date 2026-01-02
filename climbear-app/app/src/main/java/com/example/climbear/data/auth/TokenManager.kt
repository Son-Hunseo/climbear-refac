package com.example.climbear.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object TokenManager {
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"

    private lateinit var sharedPreferences: SharedPreferences

    // EncryptedSharedPreferences 초기화
    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // SharedPreferences에서 토큰 로드
    fun loadTokens() {
        _accessToken.value = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    // 토큰 저장
    fun saveTokens(access: String, refresh: String) {
        _accessToken.value = access

        sharedPreferences.edit().apply {
            putString(ACCESS_TOKEN_KEY, access)
            putString(REFRESH_TOKEN_KEY, refresh)
            apply()
        }
    }

    // 토큰 초기화
    fun clearTokens() {
        _accessToken.value = null
        sharedPreferences.edit {
            clear()
        }
    }

    // refresh 토큰 로드
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    // 로그인 필요 발생 시 1번만 실행하게 함
    private val _loginRequired = MutableSharedFlow<Unit>(replay = 0)
    val loginRequired = _loginRequired.asSharedFlow()

    fun emitLoginRequired() {
        CoroutineScope(Dispatchers.Main).launch {
            _loginRequired.emit(Unit)
        }
    }
}