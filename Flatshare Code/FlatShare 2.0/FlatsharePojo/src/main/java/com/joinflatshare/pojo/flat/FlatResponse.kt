package com.joinflatshare.pojo.flat

import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FlatResponse(

    @field:SerializedName("data")
    var data: MyFlatData? = null,

    @field:SerializedName("flatMates")
    var flatMates: ArrayList<User> = ArrayList(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0,

    @field:SerializedName("isLiked")
    var isLiked: Boolean = false,

    @field:SerializedName("distance")
    var distance: Double = 0.0
)

data class Beds(

    @field:SerializedName("total")
    var total: Int = 0,

    @field:SerializedName("vacant")
    var vacant: Int = 0
) : Serializable

data class MyFlatData(

    @field:SerializedName("_id")
    var mongoId: String = "",

    @field:SerializedName("deepLink")
    var deepLink: String? = null,

    @field:SerializedName("norms")
    var norms: String? = null,

    @field:SerializedName("isVerified")
    var isVerified: Boolean = false,

    @field:SerializedName("score")
    var score: Int = 0,

    @field:SerializedName("name")
    var name: String = "",

    @field:SerializedName("completed")
    val completed: Double = 0.0,

    @field:SerializedName("flatProperties")
    var flatProperties: FlatProperties = FlatProperties(),

    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("isMateSearch")
    val isMateSearch: IsMateSearch = IsMateSearch(),

    @field:SerializedName("images")
    var images: ArrayList<String> = ArrayList(),

    @field:SerializedName("verifier")
    var verifier: Verifier = Verifier(),

    @field:SerializedName("flatMates")
    var flatMates: ArrayList<User> = ArrayList(),

    ) : Serializable

data class IsMateSearch(

    @field:SerializedName("value")
    var value: Boolean = false
) : Serializable

data class FlatProperties(

    @field:SerializedName("furnishing")
    var furnishing: List<String> = ArrayList(),

    @field:SerializedName("dealBreakers")
    var dealBreakers: DealBreakers? = null,

    @field:SerializedName("amenities")
    var amenities: List<String> = ArrayList(),

    @field:SerializedName("gender")
    var gender: String = "",

    @field:SerializedName("isVerifiedOnly")
    var isVerifiedOnly: Boolean = false,

    @field:SerializedName("rentPerPerson")
    var rentperPerson: Int = 0,

    @field:SerializedName("moveinDate")
    var moveinDate: String? = null,

    @field:SerializedName("flatSize")
    var flatsize: String? = null,

    @field:SerializedName("depositPerPerson")
    var depositperPerson: Int = 0,

    @field:SerializedName("society")
    var society: ModelLocation? = null,

    @field:SerializedName("location")
    var location: ModelLocation? = null,

    @field:SerializedName("beds")
    var beds: Beds = Beds(),

    @field:SerializedName("roomType")
    var roomType: String? = null,

    @field:SerializedName("interests")
    var interests: ArrayList<String>? = null,

    @field:SerializedName("languages")
    var languages: ArrayList<String>? = null
) : Serializable

data class Verifier(

    @field:SerializedName("distance")
    var distance: Double = 0.0,

    @field:SerializedName("coordinates")
    var coordinates: ArrayList<Double> = ArrayList(),

    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("timestamp")
    var timestamp: String?=null
) : Serializable

data class DealBreakers(
    @field:SerializedName("smoking")
    var smoking: Int = 0,

    @field:SerializedName("eggs")
    var eggs: Int = 0,

    @field:SerializedName("nonveg")
    var nonveg: Int = 0,

    @field:SerializedName("party")
    var flatparty: Int = 0,

    @field:SerializedName("pets")
    var pets: Int = 0,

    @field:SerializedName("workout")
    var workout: Int = 0,
) : Serializable
