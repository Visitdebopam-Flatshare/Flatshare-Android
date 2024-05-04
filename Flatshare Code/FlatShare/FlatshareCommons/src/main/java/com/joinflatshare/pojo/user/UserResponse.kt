package com.joinflatshare.pojo.user

import com.google.gson.annotations.SerializedName
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.config.RentRange
import com.joinflatshare.pojo.flat.DealBreakers
import java.io.Serializable

data class UserResponse(
    @field:SerializedName("invite")
    val invite: Invite? = null,

    @field:SerializedName("data")
    var data: User? = null,

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0,

    @field:SerializedName("isLiked")
    var isLiked: Boolean = false,

    @field:SerializedName("distance")
    var distance: Double = 0.0,

    @field:SerializedName("friends")
    var friends: ArrayList<User> = ArrayList(),

    val token: String = ""
) : Serializable


data class Invite(
    @field:SerializedName("name")
    var name: Name?,

    @field:SerializedName("inv")
    val inv: Inv? = null,
) : Serializable

data class Inv(

    @field:SerializedName("inviter")
    val inviter: String? = null,

    @field:SerializedName("invitee")
    val invitee: String? = null,

    @field:SerializedName("dp")
    val dp: String? = null
) : Serializable

data class User(
    @field:SerializedName("college")
    var college: ModelLocation? = ModelLocation(),

    @field:SerializedName("hometown")
    var hometown: ModelLocation? = ModelLocation(),

    @field:SerializedName("location")
    var location: ModelLocation = ModelLocation(),

    @field:SerializedName("verification")
    val verification: Verification? = null,

    @field:SerializedName("work")
    var work: String? = "",

    @field:SerializedName("dp")
    var dp: String? = null,

    @field:SerializedName("score")
    var score: Int = 0,

    @field:SerializedName("fsCoins")
    var fsCoins: Int = 0,

    @field:SerializedName("hangout")
    var hangout: ModelLocation? = ModelLocation(),

    @field:SerializedName("gender")
    var gender: String = "",

    @field:SerializedName("website")
    var website: String = "",

    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("dob")
    var dob: String = "",

    @field:SerializedName("name")
    var name: Name? = null,

    @field:SerializedName("status")
    var status: String? = "",

    @field:SerializedName("deepLinks")
    var deepLinks: Deeplinks? = null,

    @field:SerializedName("invites")
    var invites: Int = 0,

    @field:SerializedName("completed")
    val completed: Double = 0.0,

    @field:SerializedName("isFlatSearch")
    var isFlatSearch: IsFlatSearch = IsFlatSearch(),

    @field:SerializedName("isDateSearch")
    var isDateSearch: IsDateSearch = IsDateSearch(),

    @field:SerializedName("isFHTSearch")
    var isFHTSearch: IsFlatSearch = IsFlatSearch(),

    @field:SerializedName("flatProperties")
    val flatProperties: FlatProperties = FlatProperties(),

    @field:SerializedName("dateProperties")
    var dateProperties: DateProperties = DateProperties(),

    @field:SerializedName("images")
    var images: ArrayList<String> = ArrayList(),

    @field:SerializedName("createdAt")
    val createdAt: String = "",

    @field:SerializedName("deviceToken")
    var deviceToken: String = ""
) : Serializable

data class IsFlatSearch(

    @field:SerializedName("value")
    var value: Boolean = false
) : Serializable

data class IsDateSearch(
    @field:SerializedName("value")
    var value: Boolean = false
) : Serializable

data class DateProperties(
    @field:SerializedName("gender")
    var gender: String? = null,

    @field:SerializedName("dealBreakers")
    var dealBreakers: DealBreakers? = null,

    @field:SerializedName("isVerifiedOnly")
    var isVerifiedOnly: Boolean = false,

    @field:SerializedName("dateType")
    var dateType: Int = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,

    @field:SerializedName("activities")
    var activities: ArrayList<String> = ArrayList(),

    @field:SerializedName("plans")
    var plans: ArrayList<String> = ArrayList()
) : Serializable

data class Name(
    @field:SerializedName("firstName")
    var firstName: String = "",

    @field:SerializedName("lastName")
    var lastName: String = "",
) : Serializable

data class ModelLocation(

    @field:SerializedName("name")
    var name: String = "",

    @field:SerializedName("loc")
    val loc: Loc = Loc(),
) : Serializable

data class Loc(

    @field:SerializedName("coordinates")
    val coordinates: ArrayList<Double> = ArrayList(),

    @field:SerializedName("type")
    val type: String = "Point"
) : Serializable

data class Deeplinks(
    @field:SerializedName("sfs")
    var sfs: String? = null,

    @field:SerializedName("fht")
    var fht: String? = null,
) : Serializable

data class FlatProperties(

    @field:SerializedName("furnishing")
    var furnishing: ArrayList<String> = ArrayList(),

    @field:SerializedName("amenities")
    var amenities: ArrayList<String> = ArrayList(),

    @field:SerializedName("gender")
    var gender: String = "",

    @field:SerializedName("dealBreakers")
    var dealBreakers: DealBreakers? = null,

    @field:SerializedName("isVerifiedOnly")
    var isVerifiedOnly: Boolean = false,

    @field:SerializedName("languages")
    var languages: ArrayList<String> = ArrayList(),

    @field:SerializedName("moveinDate")
    var moveinDate: String? = null,

    @field:SerializedName("flatSize")
    var flatSize: List<String> = ArrayList(),

    @field:SerializedName("interests")
    var interests: ArrayList<String> = ArrayList(),

    @field:SerializedName("roomType")
    var roomType: String = "",

    @field:SerializedName("rentRange")
    var rentRange: RentRange? = null,

    @field:SerializedName("preferredLocation")
    var preferredLocation: ArrayList<ModelLocation> = ArrayList(),
) : Serializable


data class Verification(

    @field:SerializedName("aadhaarNo")
    val aadhaarNo: String? = null,

    @field:SerializedName("isVerified")
    val isVerified: Boolean? = null
) : java.io.Serializable