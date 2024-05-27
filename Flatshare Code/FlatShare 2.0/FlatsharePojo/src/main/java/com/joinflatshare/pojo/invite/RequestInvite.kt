package com.joinflatshare.pojo.invite

import com.google.gson.annotations.SerializedName

data class RequestInvite(

    @field:SerializedName("from")
    val from: String = "",

    @field:SerializedName("id")
    val id: String = ""
)
