package com.example.climbear.data.hold.di

import com.example.climbear.data.hold.api.HoldApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HoldModule {

    @Provides
    @Singleton
    fun provideHoldApi(retrofit: Retrofit): HoldApi =
        retrofit.create(HoldApi::class.java)
}