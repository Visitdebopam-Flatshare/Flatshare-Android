package com.joinflatshare.pojo;


import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("matched")
    private Boolean matched;

    @SerializedName("shouldShowReview")
    private Boolean shouldShowReview;

    @SerializedName("success")
    private Boolean success;

    // For sendbird
    @SerializedName("error")
    private Boolean error = false;

    public int getStatus() {
        return status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getMatched() {
        return matched;
    }

    public Boolean shouldShowReview() {
        return shouldShowReview;
    }

    public Boolean getError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
