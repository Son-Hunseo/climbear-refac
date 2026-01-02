package com.example.climbear.data.hold.model

import com.google.gson.annotations.SerializedName

data class HoldRequest(
    @SerializedName(value = "picture_url")
    val pictureUrl: String
)

