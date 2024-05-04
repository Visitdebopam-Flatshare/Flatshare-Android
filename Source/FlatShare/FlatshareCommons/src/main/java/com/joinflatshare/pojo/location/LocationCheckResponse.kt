package com.joinflatshare.pojo.location

import com.google.gson.annotations.SerializedName

data class LocationCheckResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("allowed")
	val allowed: Boolean? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("inBoundary")
	val inBoundary: Boolean? = null,

	@field:SerializedName("city")
	val city: String? = null
)
