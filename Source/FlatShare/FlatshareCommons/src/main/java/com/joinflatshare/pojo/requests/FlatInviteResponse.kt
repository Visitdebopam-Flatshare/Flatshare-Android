package com.joinflatshare.pojo.requests

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.User

data class FlatInviteResponse(

	@field:SerializedName("data")
	val data: List<FlatInviteItem> = ArrayList(),

	@field:SerializedName("message")
	val message: String="",

	@field:SerializedName("status")
	val status: Int=0
)

data class Flat(

	@field:SerializedName("data")
	val data: MyFlatData? = null,

	@field:SerializedName("flatMates")
	val flatMates: List<User> = ArrayList()
)



data class FlatInviteItem(

	@field:SerializedName("requester")
	val requester: Requester? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("requestee")
	val requestee: Requester? = null,

	@field:SerializedName("notiID")
	val notiID: String? = null,

	@field:SerializedName("flat")
	val flat: Flat? = null,

	@field:SerializedName("type")
	val type: String? = null,

)