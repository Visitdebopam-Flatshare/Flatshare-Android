package com.joinflatshare.utils.deeplink

import android.text.TextUtils
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils

/**
 * Created by debopam on 25/03/23
 */
object FlatShareMessageGenerator {

    fun generateFlatShareMessage(flat: MyFlatData?): String {
        var flatShareText = ""
        if (isFlatDataAvailableToShare(flat)) {
            val currency = FlatShareApplication.instance.resources.getString(R.string.currency)
            flatShareText =
                "Checkout this ${flat?.flatProperties?.roomType} available for rent at $currency" +
                        "${flat?.flatProperties?.rentperPerson}/person in a ${flat?.flatProperties?.flatsize} flat in ${flat?.flatProperties?.location?.name}.\n\n"
            // Furnishing
            var furnishing = ""
            if (!flat?.flatProperties?.furnishing.isNullOrEmpty())
                furnishing += "${flat?.flatProperties?.furnishing!![0]} flat. "
            // Amenities
            if (!flat?.flatProperties?.amenities.isNullOrEmpty()) {
                var interests = TextUtils.join(
                    ", ",
                    flat?.flatProperties?.amenities!!
                )
                if (interests.contains(",")) {
                    val lastIndex = interests.lastIndexOf(",")
                    interests = interests.substring(
                        0, lastIndex
                    ) + " and" + interests.substring(lastIndex + 1)
                }
                furnishing += "$interests included."
            }
            if (furnishing.isNotEmpty())
                flatShareText += furnishing + "\n\n"

            var hasGenderOrDate = false
            // Gender
            if (!flat?.flatProperties?.gender.isNullOrEmpty()) {
                hasGenderOrDate = true
                val gender =
                    if (flat?.flatProperties?.gender.equals("Both")) "Gender-Neutral" else flat?.flatProperties?.gender + " only"
                flatShareText += "$gender flat. "
            }
            // Date
            if (!flat?.flatProperties?.moveinDate.isNullOrEmpty()) {
                hasGenderOrDate = true
                flatShareText += "Available from ${
                    DateUtils.convertToShortMonthFormat(
                        flat?.flatProperties?.moveinDate
                    )
                }."
            }
            if (hasGenderOrDate)
                flatShareText += "\n\n"

            if (!flat?.flatProperties?.interests.isNullOrEmpty()) {
                var interests = TextUtils.join(
                    ", ",
                    flat?.flatProperties?.interests!!
                )
                if (interests.contains(",")) {
                    val lastIndex = interests.lastIndexOf(",")
                    interests = interests.substring(
                        0,
                        lastIndex
                    ) + " and" + interests.substring(lastIndex + 1)

                }
                flatShareText += "We are into $interests"

                if (!flat.flatProperties.languages.isNullOrEmpty()) {
                    interests = TextUtils.join(
                        ", ",
                        flat.flatProperties.languages!!
                    )
                    if (interests.contains(",")) {
                        val lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                    }
                    flatShareText += ", speak $interests.\n"
                } else flatShareText += ".\n"
            } else {
                if (!flat?.flatProperties?.languages.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        flat?.flatProperties?.languages!!
                    )
                    if (interests.contains(",")) {
                        val lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                    }
                    flatShareText += "We speak $interests.\n"
                }
            }

            val likes = getLikes(flat)
            val dislikes = getDisLikes(flat)
            if (likes.size > 0) {
                flatShareText += "We enjoy ${TextUtils.join(", ", likes)}"
                if (dislikes.size > 0) {
                    flatShareText += " and would prefer no ${
                        TextUtils.join(
                            ", ",
                            dislikes
                        )
                    } in the flat please!"
                } else flatShareText += "."
            } else if (dislikes.size > 0) {
                flatShareText += "We would prefer no ${
                    TextUtils.join(
                        ", ",
                        dislikes
                    )
                } in the flat please!"
            }
            CommonMethod.makeLog("Share", flatShareText)
        }
        return flatShareText
    }


    fun isFlatDataAvailableToShare(flat: MyFlatData?): Boolean {
        if (flat == null || flat.completed < 15)
            return false
        if (flat.flatProperties == null)
            return false
        val properties = flat.flatProperties
        if (properties.roomType.isNullOrEmpty() ||
            properties.rentperPerson == 0 ||
            properties.flatsize.isNullOrEmpty() ||
            properties.location == null ||
            properties.location!!.loc == null ||
            properties.location!!.loc.coordinates.isNullOrEmpty()
        )
            return false
        return true
    }


    private fun getLikes(flat: MyFlatData?): ArrayList<String> {
        val likes = ArrayList<String>()
        val dealBreakers = flat?.flatProperties?.dealBreakers
        if (dealBreakers?.smoking == 3)
            likes.add("Smoking")
        if (dealBreakers?.nonveg == 3)
            likes.add("Non-Veg")
        if (dealBreakers?.flatparty == 3)
            likes.add("Drinking Alcohol")
        if (dealBreakers?.eggs == 3)
            likes.add("Eggs")
        if (dealBreakers?.pets == 3)
            likes.add("Pets")
        if (dealBreakers?.workout == 3)
            likes.add("Workout")
        return likes
    }

    private fun getDisLikes(flat: MyFlatData?): ArrayList<String> {
        val dislikes = ArrayList<String>()
        val dealBreakers = flat?.flatProperties?.dealBreakers
        if (dealBreakers?.smoking == 1)
            dislikes.add("Smoking")
        if (dealBreakers?.nonveg == 1)
            dislikes.add("Non-Veg")
        if (dealBreakers?.flatparty == 1)
            dislikes.add("Drinking Alcohol")
        if (dealBreakers?.eggs == 1)
            dislikes.add("Eggs")
        if (dealBreakers?.pets == 1)
            dislikes.add("Pets")
        if (dealBreakers?.workout == 1)
            dislikes.add("Workout")
        return dislikes
    }
}