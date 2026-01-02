package com.example.climbear.data.problem.di

import com.example.climbear.data.problem.api.ProblemApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProblemModule {

    @Provides
    @Singleton
    fun provideProblemApi(retrofit: Retrofit): ProblemApi =
        retrofit.create(ProblemApi::class.java)
}