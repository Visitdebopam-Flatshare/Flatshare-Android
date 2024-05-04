package com.joinflatshare.pojo.invite

import com.google.gson.annotations.SerializedName

data class InvitedRequest(

    @field:SerializedName("ids")
    val ids: ArrayList<String> = ArrayList(),

    @field:SerializedName("type")
    var type: String = "0",

    @field:SerializedName("requester")
    var requester: String = ""
)
