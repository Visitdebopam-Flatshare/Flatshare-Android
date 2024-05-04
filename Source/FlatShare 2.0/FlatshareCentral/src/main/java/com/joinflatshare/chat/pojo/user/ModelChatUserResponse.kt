package com.joinflatshare.chat.pojo.user

import com.google.gson.annotations.SerializedName

data class ModelChatUserResponse(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("require_auth_for_profile_image")
	val requireAuthForProfileImage: Boolean? = null,

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("preferred_languages")
	val preferredLanguages: List<Any?>? = null,

	@field:SerializedName("profile_url")
	val profileUrl: String? = null,

	@field:SerializedName("discovery_keys")
	val discoveryKeys: List<Any?>? = null,

	@field:SerializedName("created_at")
	val createdAt: Int? = null,

	@field:SerializedName("has_ever_logged_in")
	val hasEverLoggedIn: Boolean? = null,

	@field:SerializedName("locale")
	val locale: String? = null,

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("is_shadow_blocked")
	val isShadowBlocked: Boolean? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("nickname")
	val nickname: String? = null,

	@field:SerializedName("session_tokens")
	val sessionTokens: List<Any?>? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("is_online")
	val isOnline: Boolean = false,

	@field:SerializedName("is_hide_me_from_friends")
	val isHideMeFromFriends: Boolean? = null,

	@field:SerializedName("last_seen_at")
	val lastSeenAt: Int? = null
)

data class Metadata(
	val any: Any? = null
)
