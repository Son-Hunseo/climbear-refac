package com.example.climbear.data.center.model

data class CenterMyData (
    val centerName: String,
    val dailyProblem: List<DailyProblemItem>,
)

data class DailyProblemItem (
    val date: String,
    val levelCount: List<LevelCountItem>,
)

data class LevelCountItem (
    val level: String,
    val count: Int
)