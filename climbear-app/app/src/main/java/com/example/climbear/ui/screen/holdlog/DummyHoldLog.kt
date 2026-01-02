package com.example.climbear.ui.screen.holdlog

import com.example.climbear.data.holdlog.model.HoldLog
import com.example.climbear.data.hold.model.HoldPoint

val DummyLogList = listOf(
    HoldLog(500L, listOf("0", "1", "2", "3")),
    HoldLog(1000L, listOf("0", "2", "2", "4")),
    HoldLog(1500L, listOf("6", "6", "2", "3")),
    HoldLog(500L, listOf("0", "1", "2", "3")),
    HoldLog(1000L, listOf("0", "2", "2", "4")),
    HoldLog(1500L, listOf("6", "6", "2", "3")),
    HoldLog(500L, listOf("0", "1", "2", "3")),
    HoldLog(1000L, listOf("0", "2", "2", "4")),
    HoldLog(1500L, listOf("6", "6", "2", "3"))
)

val dummyHoldMap: Map<String, HoldPoint> = listOf(
    HoldPoint(0, listOf(93 to 503, 210 to 503, 210 to 600, 93 to 600)),
    HoldPoint(1, listOf(80 to 604, 156 to 604, 156 to 687, 80 to 687)),
    HoldPoint(2, listOf(310 to 600, 370 to 600, 370 to 670, 310 to 670)),
    HoldPoint(3, listOf(200 to 530, 270 to 530, 270 to 600, 200 to 600)),
    HoldPoint(4, listOf(500 to 530, 570 to 530, 570 to 600, 500 to 600)),
    HoldPoint(5, listOf(800 to 530, 870 to 530, 870 to 600, 800 to 600)),
    HoldPoint(6, listOf(200 to 730, 270 to 730, 270 to 800, 200 to 800))
).associateBy { it.id.toString() }