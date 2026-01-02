package com.example.climbear.data.problem.model

import com.example.climbear.data.hold.model.Coordinate

data class ProblemRequest(
    val latitude: Double?,
    val longitude: Double?,
    val heightDiff: Int,
    val widthDiff: Int,
    val selected: List<HoldCoordinates>,
    val startHold: List<Int>,
    val endHold: List<Int>,
    val choiceColor: String?,
    val level: String?,
    val pixelGrid: Int,
    val imageName: String
)

data class HoldCoordinates(
    val holdId: Int,
    val coordinates: List<Coordinate>
)