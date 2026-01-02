package com.example.climbear.data.record.api

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.record.model.DetailRecord
import com.example.climbear.data.record.model.FailResponse
import com.example.climbear.data.record.model.RecordData
import com.example.climbear.data.record.model.RecordRequest
import com.example.climbear.data.record.model.RecordResponse
import com.example.climbear.data.record.model.SimilarRecordData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RecordApi {
    @GET("api/v1/records/list")
    suspend fun getRecordList(): Response<ApiResponse<List<RecordData>>>

    //회원 문제풀이 성공
    @POST("api/v1/records")
    suspend fun postRecordsMember(@Body request: RecordRequest): Response<ApiResponse<RecordResponse>>

    //비회원 문제풀이 성공
    @POST("/api/v1/records/non-member")
    suspend fun postRecords(): Response<ApiResponse<RecordResponse>>

    //문제 풀이 실패
    @PATCH("/api/v1/records/fail/{problemId}")
    suspend fun patchRecordsFail(
        @Path("problemId") problemId: Int
    ): Response<ApiResponse<FailResponse>>

    @GET("api/v1/records/similar/{categoryId}")
    suspend fun getSimilarRecords(@Path("categoryId") categoryId: Int): Response<ApiResponse<List<SimilarRecordData>>>

    @GET("api/v1/records/detail/{problemId}")
    suspend fun getDetailRecord(@Path("problemId") problemId: Int): Response<ApiResponse<List<DetailRecord>>>
}

