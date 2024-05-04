package com.joinflatshare.pojo.invite

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.user.Name

data class InvitedResponse(

    @field:SerializedName("data")
    val data: List<InvitedItem> = ArrayList(),

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class InvitedItem(

    @field:SerializedName("id")
    val id: String = "",

    @field:SerializedName("dp")
    val dp: String? = null,

    @field:SerializedName("name")
    val name: Name?,

    // APP
    @field:SerializedName("isRegistered")
    val isRegistered: Boolean = false,

    @field:SerializedName("isInvited")
    val isInvited: Boolean = false,

    // Friend request
    @field:SerializedName("isRequested")
    val isRequested: Boolean = false,

    @field:SerializedName("isFriend")
    val isFriend: Boolean = false,

    // Flat request
    @field:SerializedName("isFlatmate")
    val isFlatmate: Boolean = false,

    // Connection
    @field:SerializedName("isConnected")
    val isConnected: Boolean = false,

    @field:SerializedName("hasSentConnection")
    val hasSentConnection: Boolean = false,

    @field:SerializedName("hasReceivedConnection")
    val hasReceivedConnection: Boolean = false

)
