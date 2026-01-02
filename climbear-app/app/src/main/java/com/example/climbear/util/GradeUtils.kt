package com.example.climbear.util

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.climbear.data.dashboard.model.BoulderingGrade
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadGrades(context: Context): List<BoulderingGrade> {
    val json = context.assets.open("bouldering_grades.json").bufferedReader().use { it.readText() }
    val listType = object : TypeToken<List<BoulderingGrade>>() {}.type
    return Gson().fromJson(json, listType)
}

fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        Color.Gray
    }
}