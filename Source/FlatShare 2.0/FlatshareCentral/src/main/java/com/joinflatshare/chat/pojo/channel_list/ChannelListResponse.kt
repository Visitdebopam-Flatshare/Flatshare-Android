package com.joinflatshare.chat.pojo.channel_list

import com.google.gson.annotations.SerializedName

data class ChannelListResponse(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("channels")
	val channels: ArrayList<ChannelsItem>? = null,

	@field:SerializedName("ts")
	val ts: Long? = null
)

data class Channel(

	@field:SerializedName("cover_url")
	val coverUrl: String? = null,

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("custom_type")
	val customType: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("max_length_message")
	val maxLengthMessage: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: Int? = null,

	@field:SerializedName("member_count")
	val memberCount: Int? = null,

	@field:SerializedName("channel_url")
	val channelUrl: String? = null
)

data class Translations(
	val any: Any? = null
)

data class File(
	val any: Any? = null
)

data class Metadata(
	val any: Any? = null
)

data class MembersItem(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("require_auth_for_profile_image")
	val requireAuthForProfileImage: Boolean? = null,

	@field:SerializedName("profile_url")
	val profileUrl: String? = null,

	@field:SerializedName("muted_end_at")
	val mutedEndAt: Int? = null,

	@field:SerializedName("is_muted")
	val isMuted: Boolean? = null,

	@field:SerializedName("muted_description")
	val mutedDescription: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("nickname")
	val nickname: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("is_online")
	val isOnline: Boolean? = null,

	@field:SerializedName("last_seen_at")
	val lastSeenAt: Int? = null
)

data class LastMessage(

	@field:SerializedName("silent")
	val silent: Boolean? = null,

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("custom_type")
	val customType: String? = null,

	@field:SerializedName("created_at")
	val createdAt: Long? = null,

	@field:SerializedName("is_removed")
	val isRemoved: Boolean? = null,

	@field:SerializedName("message_id")
	val messageId: Int? = null,

	@field:SerializedName("mention_type")
	val mentionType: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("channel_url")
	val channelUrl: String? = null,

	@field:SerializedName("req_id")
	val reqId: String? = null,

	@field:SerializedName("is_op_msg")
	val isOpMsg: Boolean? = null,

	@field:SerializedName("file")
	val file: File? = null,

	@field:SerializedName("message_retention_hour")
	val messageRetentionHour: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: Int? = null,

	@field:SerializedName("translations")
	val translations: Translations? = null,

	@field:SerializedName("mentioned_users")
	val mentionedUsers: List<Any?>? = null,

	@field:SerializedName("channel_type")
	val channelType: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("message_survival_seconds")
	val messageSurvivalSeconds: Int? = null
)

data class ChannelsItem(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("custom_type")
	val customType: String? = null,

	@field:SerializedName("invited_at")
	val invitedAt: Int? = null,

	@field:SerializedName("disappearing_message")
	val disappearingMessage: DisappearingMessage? = null,

	@field:SerializedName("channel")
	val channel: Channel? = null,

	@field:SerializedName("is_super")
	val isSuper: Boolean? = null,

	@field:SerializedName("created_at")
	val createdAt: Int? = null,

	@field:SerializedName("last_message")
	val lastMessage: LastMessage? = null,

	@field:SerializedName("is_discoverable")
	val isDiscoverable: Boolean? = null,

	@field:SerializedName("joined_member_count")
	val joinedMemberCount: Int? = null,

	@field:SerializedName("freeze")
	val freeze: Boolean? = null,

	@field:SerializedName("is_distinct")
	val isDistinct: Boolean? = null,

	@field:SerializedName("members")
	val members: List<MembersItem?>? = null,

	@field:SerializedName("max_length_message")
	val maxLengthMessage: Int? = null,

	@field:SerializedName("inviter")
	val inviter: Any? = null,

	@field:SerializedName("member_count")
	val memberCount: Int? = null,

	@field:SerializedName("message_survival_seconds")
	val messageSurvivalSeconds: Int? = null,

	@field:SerializedName("cover_url")
	val coverUrl: String? = null,

	@field:SerializedName("is_ephemeral")
	val isEphemeral: Boolean? = null,

	@field:SerializedName("is_exclusive")
	val isExclusive: Boolean? = null,

	@field:SerializedName("unread_mention_count")
	val unreadMentionCount: Int? = null,

	@field:SerializedName("joined_ts")
	val joinedTs: Any? = null,

	@field:SerializedName("unread_message_count")
	val unreadMessageCount: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: CreatedBy? = null,

	@field:SerializedName("channel_url")
	val channelUrl: String? = null,

	@field:SerializedName("is_broadcast")
	val isBroadcast: Boolean? = null,

	@field:SerializedName("is_public")
	val isPublic: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("sms_fallback")
	val smsFallback: SmsFallback? = null,

	@field:SerializedName("ignore_profanity_filter")
	val ignoreProfanityFilter: Boolean? = null,

	@field:SerializedName("is_access_code_required")
	val isAccessCodeRequired: Boolean? = null
)

data class User(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("require_auth_for_profile_image")
	val requireAuthForProfileImage: Boolean? = null,

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("profile_url")
	val profileUrl: String? = null,

	@field:SerializedName("nickname")
	val nickname: String? = null
)

data class DisappearingMessage(

	@field:SerializedName("is_triggered_by_message_read")
	val isTriggeredByMessageRead: Boolean? = null,

	@field:SerializedName("message_survival_seconds")
	val messageSurvivalSeconds: Int? = null
)

data class SmsFallback(

	@field:SerializedName("exclude_user_ids")
	val excludeUserIds: List<Any?>? = null,

	@field:SerializedName("wait_seconds")
	val waitSeconds: Int? = null
)

data class CreatedBy(

	@field:SerializedName("require_auth_for_profile_image")
	val requireAuthForProfileImage: Boolean? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("profile_url")
	val profileUrl: String? = null,

	@field:SerializedName("nickname")
	val nickname: String? = null
)
