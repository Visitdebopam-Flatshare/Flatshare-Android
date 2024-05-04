package com.joinflatshare.pojo.requests

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.user.Name

data class ConnectionRequestResponse(

    @field:SerializedName("data")
    val data: List<ConnectionItem> = ArrayList(),

    @field:SerializedName("message")
    val message: String = "",

    @field:SerializedName("status")
    val status: Int = 0
)

data class ConnectionItem(

    @field:SerializedName("requester")
    val requester: Requester? = null,

    @field:SerializedName("createdAt")
    val createdAt: String = "",

    @field:SerializedName("mutuals")
    val mutuals: List<MutualsItem> = ArrayList(),

    @field:SerializedName("flat")
    val flat: ConnectionFlat? = null,

    @field:SerializedName("requestee")
    val requestee: Requester? = null,

    @field:SerializedName("notiID")
    val notiID: String = "",

    @field:SerializedName("type")
    val type: String = ""
)

data class ConnectionFlat(
    @field:SerializedName("id")
    val id: String = "",

    @field:SerializedName("name")
    val name: String = ""
)

data class MutualsItem(

    @field:SerializedName("name")
    val name: Name? = null,

    @field:SerializedName("id")
    val id: String? = null
)