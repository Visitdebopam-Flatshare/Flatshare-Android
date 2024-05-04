package com.joinflatshare.chat.pojo;

import com.google.gson.annotations.SerializedName;
import com.joinflatshare.chat.metadata.UserMetadata;

public class ModelUserMetadataRequest {

    @SerializedName("metadata")
    private UserMetadata metadata;

    public void setMetadata(UserMetadata metadata) {
        this.metadata = metadata;
    }
}