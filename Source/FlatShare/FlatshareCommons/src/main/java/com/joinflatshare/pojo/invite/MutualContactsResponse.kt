package com.joinflatshare.pojo.invite

import com.google.gson.annotations.SerializedName

data class MutualContactsResponse(

    @field:SerializedName("data")
    val mutualContacts: ArrayList<String>? = null,

    @field:SerializedName("lastIndex")
    val lastIndex: Int = -1,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
