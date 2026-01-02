package com.example.climbear.data.record.di

import com.example.climbear.data.record.api.RecordApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecordModule {

    @Provides
    @Singleton
    fun provideRecordApi(retrofit: Retrofit): RecordApi =
        retrofit.create(RecordApi::class.java)
}