package com.joinflatshare.pojo.likes

import com.google.gson.annotations.SerializedName

data class LikeRequest(

    @field:SerializedName("coordinates")
    val coordinates: List<Double> = ArrayList()
)
