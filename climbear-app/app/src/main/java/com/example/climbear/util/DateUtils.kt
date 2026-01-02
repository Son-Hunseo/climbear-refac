package com.example.climbear.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun String.toYear(): Int {
    val date = dateFormat.parse(this) ?: return -1
    val cal = Calendar.getInstance().apply { time = date }
    return cal.get(Calendar.YEAR)
}

fun String.toMonth(): Int {
    val date = dateFormat.parse(this) ?: return -1
    val cal = Calendar.getInstance().apply { time = date }
    return cal.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
}

fun String.toDay(): Int {
    val date = dateFormat.parse(this) ?: return -1
    val cal = Calendar.getInstance().apply { time = date }
    return cal.get(Calendar.DAY_OF_MONTH)
}

fun String.isToday(): Boolean {
    val date = dateFormat.parse(this) ?: return false
    val today = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { time = date }

    return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
}

fun String.toYearMonthPair(): Pair<Int, Int> {
    return this.toYear() to this.toMonth()
}