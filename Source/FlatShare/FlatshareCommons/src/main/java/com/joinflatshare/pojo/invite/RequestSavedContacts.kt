package com.joinflatshare.pojo.invite

import com.google.gson.annotations.SerializedName

data class RequestSavedContacts(

	@field:SerializedName("ids")
	val ids: ArrayList<String> = ArrayList()
)
