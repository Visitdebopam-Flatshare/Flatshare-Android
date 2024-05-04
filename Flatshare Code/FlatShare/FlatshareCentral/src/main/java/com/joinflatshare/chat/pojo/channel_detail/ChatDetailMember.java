package com.joinflatshare.chat.pojo.channel_detail;

import com.google.gson.annotations.SerializedName;

public class ChatDetailMember {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("is_muted")
    private Boolean isMuted;

    @SerializedName("is_active")
    private Boolean isActive;

    @SerializedName("state")
    private String state;

    @SerializedName("role")
    private String role;

    @SerializedName("is_online")
    private Boolean isOnline;

    @SerializedName("last_seen_at")
    private Long lastSeenAt;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("profile_url")
    private String profileUrl;

//    @SerializedName("metadata")
//        private Metadata metadata;

    public String getUserId() {
        return userId;
    }

    public Boolean getMuted() {
        return isMuted;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getState() {
        return state;
    }

    public String getRole() {
        return role;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public Long getLastSeenAt() {
        return lastSeenAt;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
