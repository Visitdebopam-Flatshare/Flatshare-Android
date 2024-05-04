package com.joinflatshare.chat.pojo.channel_detail;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChannelDetailResponse {

    @SerializedName("data")
    private String data;

    @SerializedName("custom_type")
    private String customType;

    @SerializedName("is_super")
    private boolean isSuper;

    @SerializedName("created_at")
    private int createdAt;

    @SerializedName("is_discoverable")
    private boolean isDiscoverable;

    @SerializedName("joined_member_count")
    private int joinedMemberCount;

    @SerializedName("freeze")
    private boolean freeze;

    @SerializedName("is_distinct")
    private boolean isDistinct;

    @SerializedName("max_length_message")
    private int maxLengthMessage;

    @SerializedName("member_count")
    private int memberCount;

    @SerializedName("message_survival_seconds")
    private int messageSurvivalSeconds;

    @SerializedName("cover_url")
    private String coverUrl;

    @SerializedName("is_ephemeral")
    private boolean isEphemeral;

    @SerializedName("unread_mention_count")
    private int unreadMentionCount;

    @SerializedName("unread_message_count")
    private int unreadMessageCount;

    @SerializedName("channel_url")
    private String channelUrl;

    @SerializedName("is_broadcast")
    private boolean isBroadcast;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("name")
    private String name;

    @SerializedName("ignore_profanity_filter")
    private boolean ignoreProfanityFilter;

    @SerializedName("members")
    private List<ChatDetailMember> members = new ArrayList<>();

    @SerializedName("is_access_code_required")
    private boolean isAccessCodeRequired;

    public String getChannelUrl() {
        return channelUrl;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public List<ChatDetailMember> getMembers() {
        return members;
    }
}