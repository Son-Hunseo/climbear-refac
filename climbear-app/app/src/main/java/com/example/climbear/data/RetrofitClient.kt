package com.example.climbear.data

import com.example.climbear.data.auth.AuthInterceptor
import com.example.climbear.data.auth.TokenAuthenticator
import com.example.climbear.data.auth.api.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.climbear.site/"

    // refresh 전용 AuthApi Retrofit
    private val authApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val retrofitClient: Retrofit by lazy {
        // OkHttpClient 설정
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(authApi))
            .connectTimeout(180, TimeUnit.SECONDS)  // 연결 시도 최대 30초
            .readTimeout(180, TimeUnit.SECONDS)     // 서버로부터 데이터 읽기 최대 30초
            .writeTimeout(180, TimeUnit.SECONDS)    // 서버로 데이터 전송 최대 30초
            .build()

        // Retrofit 설정
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofitClient.create(service)
}