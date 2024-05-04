package com.joinflatshare.pojo.user

import com.google.gson.annotations.SerializedName

data class AdhaarOtp(
    @field:SerializedName("code")
    val otp: String? = null
)
