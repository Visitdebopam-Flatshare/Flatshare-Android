package com.joinflatshare.pojo.notification

import com.google.gson.annotations.SerializedName

data class NotificationResponse(

    @field:SerializedName("data")
    val data: List<NotificationItem> = ArrayList(),

    @field:SerializedName("message")
    val message: String = "",

    @field:SerializedName("lastIndex")
    val lastIndex: Int = 0,

    @field:SerializedName("status")
    val status: Int = 0
)

data class NotificationItem(

    @field:SerializedName("from")
    val from: String = "",

    @field:SerializedName("id")
    val id: String = "",

    @field:SerializedName("dp")
    val dp: String? = null,

    @field:SerializedName("msg")
    val msg: String = "",

    @field:SerializedName("uid")
    val uid: String = "",

    @field:SerializedName("type")
    var type: String = "",

    @field:SerializedName("createdAt")
    val createdAt: String = ""
)