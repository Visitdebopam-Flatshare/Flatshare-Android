package com.joinflatshare.ui.admin.features.model

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.user.Name

data class SendbirdUserResponseItem(

    @field:SerializedName("name")
    val name: Name? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("dp")
    val dp: String? = null
)
