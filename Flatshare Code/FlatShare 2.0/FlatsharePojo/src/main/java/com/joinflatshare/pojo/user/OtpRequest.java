package com.joinflatshare.pojo.user;


import com.google.gson.annotations.SerializedName;

public class OtpRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("deviceToken")
    private String deviceToken;

    @SerializedName("code")
    private String code;

    @SerializedName("statusCheck")
    private boolean statusCheck;

    @SerializedName("byPassOTP")
    private String byPassOTP;


    public OtpRequest(String id, String deviceToken, String otp, boolean statusCheck, String byPassOTP) {
        this.id = id;
        this.code = otp;
        this.deviceToken = deviceToken;
        this.statusCheck = statusCheck;
        this.byPassOTP = byPassOTP;
    }
}