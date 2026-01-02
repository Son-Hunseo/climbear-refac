package com.example.climbear.data.record.model

/**
 * 경로 저장 요청 DTO
 */
data class RecordRequest(
    val problemId: Int,
    val route: Route,
    val time: Int,
    val height: Double?
)

data class Route(
    val leftHand: List<Int?>,
    val rightHand: List<Int?>,
    val leftFoot: List<Int?>,
    val rightFoot: List<Int?>
)
