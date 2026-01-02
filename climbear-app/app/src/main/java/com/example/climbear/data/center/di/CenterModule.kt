package com.example.climbear.data.center.di

import com.example.climbear.data.center.api.CenterApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CenterModule {

    @Provides
    @Singleton
    fun provideCenterApi(retrofit: Retrofit): CenterApi =
        retrofit.create(CenterApi::class.java)
}