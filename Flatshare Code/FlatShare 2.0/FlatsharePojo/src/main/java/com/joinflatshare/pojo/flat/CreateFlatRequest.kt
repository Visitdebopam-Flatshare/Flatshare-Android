package com.joinflatshare.pojo.flat

import com.joinflatshare.pojo.user.ModelLocation
import com.google.gson.annotations.SerializedName

data class CreateFlatRequest(
    @field:SerializedName("norms")
    var norms: String? = null,

    @field:SerializedName("name")
    var name: String = "",

    @field:SerializedName("flatProperties")
    var flatProperties: FlatProps = FlatProps(),

    @field:SerializedName("id")
    var id: String = "",

    @field:SerializedName("images")
    var images: ArrayList<String> = ArrayList()
)

data class FlatProps(

    @field:SerializedName("flatsize")
    var flatsize: String? = null,

    @field:SerializedName("society")
    var society: ModelLocation? = null,

    @field:SerializedName("beds")
    var beds: Beds = Beds(),

    @field:SerializedName("roomType")
    var roomType: String? = null,

    @field:SerializedName("rentPerPerson")
    var rentperPerson: Int = 0,

    @field:SerializedName("moveinDate")
    var moveinDate: String? = null
)