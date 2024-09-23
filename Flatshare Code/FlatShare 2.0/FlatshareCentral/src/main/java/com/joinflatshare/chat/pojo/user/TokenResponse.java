package com.joinflatshare.chat.pojo.user;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("tokens")
    private List<String> tokens;

    public List<String> getTokens() {
        return tokens;
    }
}