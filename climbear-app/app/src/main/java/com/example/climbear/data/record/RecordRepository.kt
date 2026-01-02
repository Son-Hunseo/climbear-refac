package com.example.climbear.data.record

import com.example.climbear.data.ApiResponse
import com.example.climbear.data.record.api.RecordApi
import com.example.climbear.data.record.model.DetailRecord
import com.example.climbear.data.record.model.FailResponse
import com.example.climbear.data.record.model.RecordData
import com.example.climbear.data.record.model.RecordRequest
import com.example.climbear.data.record.model.RecordResponse
import com.example.climbear.data.record.model.SimilarRecordData
import javax.inject.Inject

class RecordRepository @Inject constructor(
    private val recordApi: RecordApi
) {
    suspend fun getRecordList(): Result<ApiResponse<List<RecordData>>> {
        return try {
            val response = recordApi.getRecordList()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message ?: "api 통신 실패"))
                    }
                } else {
                    Result.failure(Exception("No response"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSimilarRecordList(categoryId: Int): Result<ApiResponse<List<SimilarRecordData>>> {
        return try {
            val response = recordApi.getSimilarRecords(categoryId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message))
                    }
                } else {
                    Result.failure(Exception("No response body"))
                }
            } else {
                Result.failure(Exception("Api failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailRecord(problemId: Int): Result<ApiResponse<List<DetailRecord>>> {
        return try {
            val response = recordApi.getDetailRecord(problemId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val status = body.status
                    if (status == "SUCCESS") {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error?.message))
                    }
                } else {
                    Result.failure(Exception("No response body"))
                }
            } else {
                Result.failure(Exception("Api failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**회원용 경로 저장*/
    suspend fun postRecordsMember(request: RecordRequest): Result<ApiResponse<RecordResponse>> {
        return try {
            val response = recordApi.postRecordsMember(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == "SUCCESS") {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "API 통신 실패"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 비회원 경로 저장*/
    suspend fun postRecords(): Result<ApiResponse<RecordResponse>> {
        return try {
            val response = recordApi.postRecords()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == "SUCCESS") {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "API 통신 실패"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**회원 문제 풀이 실패*/
    suspend fun patchRecordsFail(problemId: Int): Result<ApiResponse<FailResponse>> {
        return try {
            val response = recordApi.patchRecordsFail(problemId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == "SUCCESS") {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "API 통신 실패"))
                }
            } else {
                Result.failure(Exception("Api response error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}