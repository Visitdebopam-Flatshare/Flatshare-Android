package com.joinflatshare.chat.pojo.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TokenResponse {

    @SerializedName("tokens")
    private List<String> tokens;

    public List<String> getTokens() {
        return tokens;
    }
}