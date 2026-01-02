package com.example.climbear.data.record.model

import com.example.climbear.data.hold.model.Coordinate
import com.example.climbear.data.hold.model.HoldResponse
import com.example.climbear.data.solution.model.SolutionData

data class DetailRecord(
    val problemId: Int = 0,
    val successRound: Int = 0,
    val route: SolutionData = SolutionData(emptyList(), emptyList(), emptyList(), emptyList()),
    val time: Int = 0,
    val height: Double = 0.0,
    val createdAt: String = "",
    val imageName: String = "",
    val selected: List<DetailHoldResponse> = emptyList()
)

data class DetailHoldResponse(
    val holdId: Int,
    val average: Coordinate?,
    val coordinates: List<Coordinate>
)

fun DetailHoldResponse.toHoldResponse(): HoldResponse {
    return HoldResponse(
        holdId = this.holdId,
        average = this.average,
        coordinates = this.coordinates
    )
}