package com.joinflatshare.pojo.requests

import com.google.gson.annotations.SerializedName

data class AllRequestCountResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("friendRequests")
	val friendRequests: Int=0,

	@field:SerializedName("FHT")
	val fHT: Int=0,

	@field:SerializedName("U2F")
	val u2F: Int=0,

	@field:SerializedName("F2U")
	val f2U: Int=0,

	@field:SerializedName("flatRequests")
	val flatRequests: Int=0,
)
