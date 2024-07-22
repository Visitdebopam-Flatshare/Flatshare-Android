package com.joinflatshare.ui.dialogs.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogShareBinding
import com.joinflatshare.FlatshareCentral.databinding.IncludePopupHeaderWhiteBinding
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.base.BaseActivity.TYPE_FLAT
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.deeplink.UserShareMessageGenerator
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.DateUtils


class DialogShare(
    private val activity: BaseActivity,
    private val header: String,
    private val shareType: String,
    private val flat: MyFlatData?,
    private val user: User?
) {

    var viewBind: DialogShareBinding
    var flatShareText = ""
    var userShareText = ""

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = IncludePopupHeaderWhiteBinding.inflate(activity.layoutInflater)
        viewBind = DialogShareBinding.inflate(activity.layoutInflater)
        holder.llPopupHolder.addView(viewBind.root)
        builder.setView(holder.root)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()

        if (prepareShareMessage()) {
            // Header
            holder.txtDialogHeader.text = header
            holder.imgHeader.visibility = View.GONE
            holder.imgCross.setOnClickListener {
                CommonMethods.dismissDialog(activity, dialog)
            }

            // content
            holder.viewBg.setOnClickListener {}
            viewBind.llShareWithin.visibility = View.GONE
            viewBind.llShareWithin.setOnClickListener {
                DialogShareChatlist(activity, header)
                CommonMethods.dismissDialog(activity, dialog)
            }
            viewBind.llShareVia.setOnClickListener {
                var text = ""
                if (shareType.equals(TYPE_FLAT)) {
                    text = flatShareText
                } else {
                    text = userShareText
                }
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    text
                )
                sendIntent.type = "text/plain"
                CommonMethod.switchActivity(activity, sendIntent, false)
                CommonMethods.dismissDialog(activity, dialog)
            }
            viewBind.llShareCopyLink.setOnClickListener {
                if (shareType.equals(TYPE_FLAT))
                    copyText(flatShareText)
                else
                    copyText(userShareText)
                CommonMethods.dismissDialog(activity, dialog)
            }
            CommonMethods.showDialog(activity, dialog)
        } else {
            val message =
                if (shareType == TYPE_FLAT) "The flat profile is incomplete." else "The user profile is incomplete."

            com.joinflatshare.customviews.alert.AlertDialog.showAlert(activity, message)
        }
    }

    private fun copyText(text: String) {
        val clipboard: ClipboardManager =
            activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        CommonMethod.makeToast("Link Copied")
    }


    private fun prepareShareMessage(): Boolean {
        if (shareType.equals(TYPE_FLAT)) {
            if (isAllFlatDataAvailableToShare(flat)) {
                val currency = activity.resources.getString(R.string.currency)
                flatShareText =
                    "Checkout this ${flat?.flatProperties?.roomType} available for rent at $currency" +
                            "${flat?.flatProperties?.rentperPerson} per person in a ${flat?.flatProperties?.flatsize} flat in ${flat?.flatProperties?.location?.name}.\n\n"
                flatShareText += "${flat?.flatProperties?.furnishing!![0]} flat."
                if (!flat.flatProperties.amenities.isNullOrEmpty()) {
                    flatShareText += " Amenities includes ${
                        TextUtils.join(
                            ", ",
                            flat.flatProperties.amenities
                        )
                    }."
                }
                val gender =
                    if (flat.flatProperties.gender.equals("Both")) "Gender-Neutral" else flat.flatProperties.gender + " only"
                flatShareText += "\n\n$gender flat."
                flatShareText += " Available from ${
                    DateUtils.convertToShortMonthFormat(
                        flat.flatProperties.moveinDate
                    )
                }.\n\n"
                if (!flat.flatProperties.interests.isNullOrEmpty()) {
                    var interests = TextUtils.join(
                        ", ",
                        flat.flatProperties.interests!!
                    )
                    var lastIndex = interests.lastIndexOf(",")
                    interests = interests.substring(
                        0,
                        lastIndex
                    ) + " and" + interests.substring(lastIndex + 1)
                    flatShareText += "Flatmates are interested in $interests."

                    if (!flat.flatProperties.languages.isNullOrEmpty()) {
                        interests = TextUtils.join(
                            ", ",
                            flat.flatProperties.languages!!
                        )
                        lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                        flatShareText += "\nThey speak $interests."
                    } else flatShareText += ". "
                } else {
                    if (!flat.flatProperties.languages.isNullOrEmpty()) {
                        var interests = TextUtils.join(
                            ", ",
                            flat.flatProperties.languages!!
                        )
                        val lastIndex = interests.lastIndexOf(",")
                        interests =
                            interests.substring(0, lastIndex) + " and" + interests.substring(
                                lastIndex + 1
                            )
                        flatShareText += "Flatmates speak $interests."
                    }
                }
                val likes = ArrayList<String>()
                val dislikes = ArrayList<String>()
                if (flat.flatProperties.dealBreakers?.smoking == 1)
                    dislikes.add("Smoking")
                else if (flat.flatProperties.dealBreakers?.smoking == 3)
                    likes.add("Smoking")
                if (flat.flatProperties.dealBreakers?.nonveg == 1)
                    dislikes.add("Non-Veg")
                else if (flat.flatProperties.dealBreakers?.nonveg == 3)
                    likes.add("Non-Veg")
                if (flat.flatProperties.dealBreakers?.flatparty == 1)
                    dislikes.add("Drinking Alcohol")
                else if (flat.flatProperties.dealBreakers?.flatparty == 3)
                    likes.add("Drinking Alcohol")
                if (flat.flatProperties.dealBreakers?.eggs == 1)
                    dislikes.add("Eggs")
                else if (flat.flatProperties.dealBreakers?.eggs == 3)
                    likes.add("Eggs")
                if (flat.flatProperties.dealBreakers?.pets == 1)
                    dislikes.add("Pets")
                else if (flat.flatProperties.dealBreakers?.pets == 3)
                    likes.add("Pets")
                if (flat.flatProperties.dealBreakers?.workout == 1)
                    dislikes.add("Workout")
                else if (flat.flatProperties.dealBreakers?.workout == 3)
                    likes.add("Workout")

                if (likes.size > 0) {
                    if (flatShareText.contains("Flatmates"))
                        flatShareText += "\nThey"
                    else flatShareText += "Flatmates"
                    flatShareText += " also like ${TextUtils.join(", ", likes)}"
                    if (dislikes.size > 0) {
                        flatShareText += " and dislike ${TextUtils.join(", ", dislikes)}."
                    } else flatShareText += "."
                } else if (dislikes.size > 0) {
                    if (flatShareText.contains("Flatmates"))
                        flatShareText += "\nThey"
                    else flatShareText += "Flatmates"
                    flatShareText += " dislike ${TextUtils.join(", ", dislikes)}."
                }
                flatShareText += "\n\n" + DeepLinkHandler.createFlatDeepLink(flat.mongoId)
                CommonMethod.makeLog("Share", flatShareText)
                return true
            }
        } else if (shareType.equals(BaseActivity.TYPE_FHT)) {
            if (UserShareMessageGenerator.isUserDataAvailableToShare(user)) {
                val name = user?.name?.firstName
                val pronoun = if (TextUtils.equals(
                        user?.gender,
                        "Male"
                    )
                ) "He" else if (TextUtils.equals(user?.gender, "Female")) "She" else name
                val roomType = user?.flatProperties?.roomType
                val loc = user?.flatProperties?.preferredLocation!![0].name
                val moveInDate = DateUtils.convertToAppFormat(user.flatProperties.moveinDate)

                userShareText = "$name is looking for $roomType Room in $loc by $moveInDate\n"

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
                    userShareText += "${user.name?.firstName} is into $interests."

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
                        userShareText += "\n$pronoun speaks $interests."
                    } else userShareText += ". "
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
                        userShareText += "${user.name?.firstName} speaks $interests."
                    }
                }

                val likes = ArrayList<String>()
                val dislikes = ArrayList<String>()
                if (user.flatProperties.dealBreakers?.smoking == 2)
                    dislikes.add("Smoking")
                else if (user.flatProperties.dealBreakers?.smoking == 1)
                    likes.add("Smoking")
                if (user.flatProperties.dealBreakers?.nonveg == 2)
                    dislikes.add("Non-Veg")
                else if (user.flatProperties.dealBreakers?.nonveg == 1)
                    likes.add("Non-Veg")
                if (user.flatProperties.dealBreakers?.flatparty == 2)
                    dislikes.add("Drinking Alcohol")
                else if (user.flatProperties.dealBreakers?.flatparty == 1)
                    likes.add("Drinking Alcohol")
                if (user.flatProperties.dealBreakers?.eggs == 2)
                    dislikes.add("Eggs")
                else if (user.flatProperties.dealBreakers?.eggs == 1)
                    likes.add("Eggs")
                if (user.flatProperties.dealBreakers?.pets == 2)
                    dislikes.add("Pets")
                else if (user.flatProperties.dealBreakers?.pets == 1)
                    likes.add("Pets")
                if (user.flatProperties.dealBreakers?.workout == 2)
                    dislikes.add("Workout")
                else if (user.flatProperties.dealBreakers?.workout == 1)
                    likes.add("Workout")

                if (likes.size > 0) {
                    userShareText += "\n$pronoun"
                    userShareText += " also enjoys ${TextUtils.join(", ", likes)}"
                    if (dislikes.size > 0) {
                        userShareText += " & would prefer no ${
                            TextUtils.join(
                                ", ",
                                dislikes
                            )
                        } in the flat please!"
                    }
                } else if (dislikes.size > 0) {
                    userShareText += "\n$pronoun"
                    userShareText += " would prefer no ${
                        TextUtils.join(
                            ", ",
                            dislikes
                        )
                    }in the flat please!"
                }
                userShareText += "\n\n" + DeepLinkHandler.createUserDeepLink(user.id)
                CommonMethod.makeLog("Share", userShareText)
                return true
            }
        }
        return false
    }

    companion object {
        fun isAllFlatDataAvailableToShare(flat: MyFlatData?): Boolean {
            if (flat?.images == null || flat.images.size < 2)
                return false
            if (flat.flatProperties == null)
                return false
            val properties = flat.flatProperties
            if (properties.roomType.isNullOrEmpty() ||
                properties.gender.isNullOrEmpty() ||
                properties.furnishing.isNullOrEmpty() ||
                properties.moveinDate.isNullOrEmpty() ||
                properties.rentperPerson == 0 ||
                properties.flatsize.isNullOrEmpty() ||
                properties.location == null ||
                properties.location?.loc == null ||
                properties.location?.loc?.coordinates == null ||
                properties.location?.loc?.coordinates?.size == 0
            )
                return false
            return true
        }
    }


}