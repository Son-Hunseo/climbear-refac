package com.example.climbear.data.image.di

import com.example.climbear.data.image.api.ImageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageApi(retrofit: Retrofit): ImageApi =
        retrofit.create(ImageApi::class.java)
}