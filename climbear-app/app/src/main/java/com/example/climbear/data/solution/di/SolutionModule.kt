package com.example.climbear.data.solution.di

import com.example.climbear.data.solution.api.SolutionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SolutionModule {

    @Provides
    @Singleton
    fun provideSolutionApi(retrofit: Retrofit): SolutionApi =
        retrofit.create(SolutionApi::class.java)
}