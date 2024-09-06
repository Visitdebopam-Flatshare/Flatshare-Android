package com.joinflatshare.chat.pojo.user_list

import com.google.gson.annotations.SerializedName

data class ModelUserListResponse(

    @field:SerializedName("next")
    val next: String? = null,

    @field:SerializedName("users")
    val users: List<UsersItem>? = null
)

data class Metadata(

    @field:SerializedName("font_color")
    val fontColor: String? = null,

    @field:SerializedName("font_preference")
    val fontPreference: String? = null
)

data class UsersItem(

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("profile_url")
    val profileUrl: String? = null,

    @field:SerializedName("nickname")
    val nickname: String? = null,

    )
