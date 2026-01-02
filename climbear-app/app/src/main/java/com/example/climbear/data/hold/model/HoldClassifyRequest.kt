package com.example.climbear.data.hold.model

import com.google.gson.annotations.SerializedName

data class HoldClassifyRequest(
    @SerializedName(value = "image_url")
    val imageUrl: String,

    @SerializedName(value = "selected_hold_id_list")
    val selectedHoldIdList: List<Int>,

    val holds: List<HoldResponse>
)