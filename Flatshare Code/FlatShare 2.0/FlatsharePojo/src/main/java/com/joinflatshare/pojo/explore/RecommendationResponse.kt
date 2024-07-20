package com.joinflatshare.pojo.explore

import com.google.gson.annotations.SerializedName
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.User

data class RecommendationResponse(

    @field:SerializedName("userData")
    val userData: List<UserRecommendationItem> = ArrayList(),

    @field:SerializedName("flatData")
    val flatData: List<FlatRecommendationItem> = ArrayList(),

    @field:SerializedName("message")
    val message: String = "",

    @field:SerializedName("lastIndex")
    val lastIndex: Int = 0,

    @field:SerializedName("status")
    val status: Int = 0,

    @field:SerializedName("count")
    val count: Int = 0,

    @field:SerializedName("showingUsers")
    val showingUsers: String = ""
)

data class UserRecommendationItem(

    @field:SerializedName("data")
    var data: User? = null,

    @field:SerializedName("details")
    val details: Details = Details()
)


data class FlatRecommendationItem(

    @field:SerializedName("data")
    var flat: MyFlatData = MyFlatData(),

    @field:SerializedName("details")
    val details: Details = Details()
)

data class Details(

    @field:SerializedName("distance")
    val distance: Double = 0.0,

    @field:SerializedName("flatLocation")
    val flatLocation: List<Double> = ArrayList(),

    @field:SerializedName("isLiked")
    var isLiked: Boolean = false,

    @field:SerializedName("chatRequestSent")
    var chatRequestSent: Boolean = false,

    @field:SerializedName("likedOn")
    val likedOn: String? = null,

    @field:SerializedName("coordinates")
    var coordinates: List<Double> = ArrayList(),

    // For flat
    @field:SerializedName("preferredLocation")
    var preferredLocation: List<Double> = ArrayList(),

    // For user
    @field:SerializedName("flatLocationMatch")
    var flatLocationMatch: List<Double> = ArrayList(),

    @field:SerializedName("userLocationMatch")
    var userLocationMatch: List<Double> = ArrayList(),

    @field:SerializedName("revealed")
    var revealed: Boolean = true
)


