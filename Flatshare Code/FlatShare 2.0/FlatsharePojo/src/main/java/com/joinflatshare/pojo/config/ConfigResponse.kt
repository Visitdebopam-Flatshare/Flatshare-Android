package com.joinflatshare.pojo.config

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConfigResponse(

    @field:SerializedName("data")
    val data: ConfigData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class ConfigData(
    @field:SerializedName("flatdefault")
    val flatData: FlatDefault? = null,

    @field:SerializedName("appAccess")
    val appAccess: Boolean = true,

    @field:SerializedName("userAccessDenied")
    val userAccessDenied: ArrayList<String> = ArrayList(),

    @field:SerializedName("cdn")
    val cdn: Cdn? = null,

    @field:SerializedName("allowedSkips")
    val allowedSkips: AllowedSkipOnBoarding? = null
)

data class FlatDefault(

    @field:SerializedName("furnishing")
    val furnishing: List<String> = ArrayList(),

    @field:SerializedName("amenities")
    val amenities: List<String> = ArrayList(),

    @field:SerializedName("vacantBeds")
    val vacantBeds: Int = 0,

    @field:SerializedName("flatSize")
    val flatSize: List<String> = ArrayList(),

    @field:SerializedName("gender")
    val gender: List<String> = ArrayList(),

    @field:SerializedName("totalBeds")
    val totalBeds: Int = 0,

    @field:SerializedName("roomType")
    val roomType: List<String> = ArrayList(),

    @field:SerializedName("rentRange")
    val rentRange: RentRange = RentRange(),

    @field:SerializedName("languages")
    val languages: ArrayList<String> = ArrayList(),

    @field:SerializedName("interests")
    val interests: ArrayList<String> = ArrayList()
)

data class RentRange(
    @field:SerializedName("start_range")
    var startRange: Int = 2000,

    @field:SerializedName("end_range")
    var endRange: Int = 200000,
) : Serializable


data class Cdn(

    @field:SerializedName("dev")
    val dev: String = "",

    @field:SerializedName("pro")
    val pro: String = ""
) : Serializable

data class AllowedSkipOnBoarding(
    @field:SerializedName("isSkippingInterestsAllowed")
    val isSkippingInterestsAllowed: Boolean = true,

    @field:SerializedName("isSkippingLanguagesAllowed")
    val isSkippingLanguagesAllowed: Boolean = true,

    @field:SerializedName("isSkippingProfilePictureAllowed")
    val isSkippingProfilePictureAllowed: Boolean = true,

    @field:SerializedName("isSkippingLocationAccessAllowed")
    val isSkippingLocationAccessAllowed: Boolean = true,

    @field:SerializedName("isSkippingDealBreakersAllowed")
    val isSkippingDealBreakersAllowed: Boolean = true,

    @field:SerializedName("isSkippingAboutAllowed")
    val isSkippingAboutAllowed: Boolean = true
) : Serializable

