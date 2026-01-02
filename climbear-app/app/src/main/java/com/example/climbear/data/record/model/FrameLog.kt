package com.example.climbear.data.record.model

data class FrameLog(
    val timeMs: Long,
    val left_hand: Int?,
    val right_hand: Int?,
    val left_foot: Int?,
    val right_foot: Int?
)