package com.example.climbear.data.record.model

data class SimilarRecordData(
    val problemId: Int = 1,
    val userId: Int = 1,
    val height: Double = 160.0,
    val successRound: Int = 0,
    val time: Int = 0,
    val solvedDate: String = ""
)
