package com.example.climbear.data.record.model

data class RecordData(
    val problemId: Int,
    val level: String,
    val successCount: Int,
    val tryCount: Int,
    val minTime: Int,
    val lastSolvesDate: String,
    val centerName: String?
)