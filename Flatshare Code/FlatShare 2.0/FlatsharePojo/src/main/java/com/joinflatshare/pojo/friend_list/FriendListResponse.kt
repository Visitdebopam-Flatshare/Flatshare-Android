package com.joinflatshare.pojo.friend_list

import com.joinflatshare.pojo.user.User

data class FriendListResponse(
	val data: List<User> = ArrayList(),
	val message: String? = null,
	val status: Int? = null
)

