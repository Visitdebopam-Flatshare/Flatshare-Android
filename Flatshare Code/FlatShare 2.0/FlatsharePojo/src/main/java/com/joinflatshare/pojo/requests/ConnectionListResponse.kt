package com.joinflatshare.pojo.requests

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.user.User

data class ConnectionListResponse(

	@field:SerializedName("data")
	val data: ArrayList<User>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

