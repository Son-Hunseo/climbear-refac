package com.example.climbear.data.hold.model

import com.google.gson.annotations.SerializedName

data class HoldResponse(
    @SerializedName("hold_id")
    val holdId: Int,
    val average: Coordinate?,
    @SerializedName("coordinates")
    val coordinates: List<Coordinate>
)

data class Coordinate(
    val x: Int,
    val y: Int
)

