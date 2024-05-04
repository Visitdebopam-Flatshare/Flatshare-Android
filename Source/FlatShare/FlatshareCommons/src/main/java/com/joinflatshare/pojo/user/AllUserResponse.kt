package com.joinflatshare.pojo.user

import com.google.gson.annotations.SerializedName

data class AllUserResponse(

    @field:SerializedName("data")
    val data: List<User> = ArrayList(),

    @field:SerializedName("message")
    val message: String = "",

    @field:SerializedName("status")
    val status: Int = 0
)