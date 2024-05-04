package com.joinflatshare.pojo.requests

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.user.Name

data class Requester(

    @field:SerializedName("joinedAt")
    val joinedAt: String? = null,

    @field:SerializedName("name")
    val name: Name?,

    @field:SerializedName("dp")
    val dp: String? = null,

    @field:SerializedName("exists")
    val exists: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = null
)
