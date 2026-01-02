package com.example.climbear.data.hold.model

import com.google.gson.annotations.SerializedName

data class HoldClassifyResponse(
    @SerializedName("selected")
    val selected: List<Int>,

    @SerializedName("not_selected")
    val notSelected: List<Int>
)