package com.joinflatshare.utils.deeplink

import android.text.TextUtils
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.user.User
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils

/**
 * Created by debopam on 25/03/23
 */
object UserShareMessageGenerator {

    fun generateUserMessage(user: User?): String {
        var userShareText = ""
        if (isUserDataAvailableToShare(user)) {
            val currency = FlatShareApplication.instance.resources.getString(R.string.currency)
            userShareText += "I am looking for a "
            // Room Type
            userShareText += if (user?.flatProperties?.roomType.isNullOrEmpty()) {
                "Room"
            } else {
                when (user?.flatProperties?.roomType!!) {
                    "Both" -> {
                        "Private/Shared room"
                    }

                    else -> {
                        user.flatProperties.roomType
                    }
                }
            }
            userShareText += " in an already rented shared flat in an around "
            val loc = user?.flatProperties?.preferredLocation!![0]
            userShareText += "${loc.name} by ${DateUtils.convertToAppFormat(user.flatProperties.moveinDate)}."
            // Budget
            if (user.flatProperties.rentRange != null
                && user.flatProperties.rentRange!!.startRange > 0
                && user.flatProperties.rentRange!!.endRange > 0
            ) {
                userShareText += " My Budget is " +
                        "$currency${user.flatProperties.rentRange?.startRange} - " +
                        "$currency${user.flatProperties.rentRange?.endRange}."
            }
            userShareText += "\n\n"

            if (!user.flatProperties.interests.isNullOrEmpty()) {
                var interests = TextUtils.join(
                    ", ",
                    user.flatProperties.interests
                )
                val lastIndex = interests.lastIndexOf(",")
                interests = interests.substring(
                    0,
                    lastIndex
                ) + " and" + interests.substring(lastIndex + 1)
                userShareText += "I'm into $interests"

                if (!user.flatProperties.languages.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        user.flatProperties.languages
                    )
                    val lastIndex = interests.lastIndexOf(",")
                    interests =
                        interests.substring(0, lastIndex) + " and" + interests.substring(
                            lastIndex + 1
                        )
                    userShareText += ", speak $interests.\n"
                } else userShareText += ".\n"
            } else {
                if (!user.flatProperties.languages.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        user.flatProperties.languages
                    )
                    val lastIndex = interests.lastIndexOf(",")
                    interests =
                        interests.substring(0, lastIndex) + " and" + interests.substring(
                            lastIndex + 1
                        )
                    userShareText += "I speak $interests.\n"
                }
            }

            val likes = getLikes(user)
            val dislikes = getDisLikes(user)
            if (likes.size > 0) {
                userShareText += "I enjoy ${TextUtils.join(", ", likes)}"
                if (dislikes.size > 0) {
                    userShareText += ", & would prefer no ${
                        TextUtils.join(
                            ", ",
                            dislikes
                        )
                    } in the flat please!"
                }
            } else if (dislikes.size > 0) {
                userShareText += "I would prefer no ${
                    TextUtils.join(
                        ", ",
                        dislikes
                    )
                } in the flat please!"
            }
            CommonMethod.makeLog("Share", userShareText)
        }
        return userShareText
    }

    fun generateFHTMessage(user: User?): String {
        var userShareText = ""
        if (isUserDataAvailableToShare(user)) {
            userShareText += "I am looking for flatmates to Flathunt Together in and around "
            val loc = user?.flatProperties?.preferredLocation!![0]
            userShareText += "${loc.name}."
            userShareText += "\n\n"

            if (!user.flatProperties.interests.isNullOrEmpty()) {
                var interests = TextUtils.join(
                    ", ",
                    user.flatProperties.interests
                )
                if (interests.contains(",")) {
                    val lastIndex = interests.lastIndexOf(",")
                    interests = interests.substring(
                        0,
                        lastIndex
                    ) + " and" + interests.substring(lastIndex + 1)
                }
                userShareText += "I'm into $interests"

                if (!user.flatProperties.languages.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        user.flatProperties.languages
                    )
                    if (interests.contains(",")) {
                        val lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                    }
                    userShareText += ", speak $interests.\n"
                } else userShareText += ".\n"
            } else {
                if (!user.flatProperties.languages.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        user.flatProperties.languages
                    )
                    if (interests.contains(",")) {
                        val lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                    }
                    userShareText += "I speak $interests.\n"
                }
            }

            val likes = getLikes(user)
            val dislikes = getDisLikes(user)
            if (likes.size > 0) {
                userShareText += "I enjoy ${TextUtils.join(", ", likes)}"
                if (dislikes.size > 0) {
                    userShareText += ", & would prefer no ${
                        TextUtils.join(
                            ", ",
                            dislikes
                        )
                    } in the flat please!"
                }
            } else if (dislikes.size > 0) {
                userShareText += "I would prefer no ${
                    TextUtils.join(
                        ", ",
                        dislikes
                    )
                } in the flat please!"
            }
            CommonMethod.makeLog("Share", userShareText)
        }
        return userShareText
    }

    fun isUserDataAvailableToShare(user: User?): Boolean {
        val properties = user?.flatProperties
        if (properties?.roomType.isNullOrEmpty() || CommonMethod.isLocationEmpty(properties?.preferredLocation) ||
            properties?.moveinDate.isNullOrEmpty()
        )
            return false
        return true
    }

    private fun isAllUserDataAvailableToShare(user: User?): Boolean {
        if (user?.name == null || user.name?.firstName.isNullOrBlank() || user.name?.lastName.isNullOrBlank())
            return false
        if (user.flatProperties == null)
            return false
        val properties = user.flatProperties
        if (properties.roomType.isNullOrEmpty() ||
            properties.gender.isNullOrEmpty() ||
            properties.preferredLocation.isNullOrEmpty() ||
            properties.preferredLocation[0] == null ||
            properties.preferredLocation[0].name.isNullOrEmpty()
        )
            return false
        return true
    }

    private fun getLikes(user: User): ArrayList<String> {
        val likes = ArrayList<String>()
        val dealBreakers = user.flatProperties.dealBreakers
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

    private fun getDisLikes(user: User): ArrayList<String> {
        val dislikes = ArrayList<String>()
        val dealBreakers = user.flatProperties.dealBreakers
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