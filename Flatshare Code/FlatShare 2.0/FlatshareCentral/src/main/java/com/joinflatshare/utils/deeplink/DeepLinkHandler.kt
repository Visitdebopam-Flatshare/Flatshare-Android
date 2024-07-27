package com.joinflatshare.utils.deeplink

import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.ui.settings.SettingsActivity
import com.joinflatshare.utils.helper.CommonMethod
import java.nio.charset.StandardCharsets


object DeepLinkHandler {
    const val TAG = "DeepLinkHandler"

    fun handle(activity: BaseActivity, intent: Intent) {
        val param = intent.getStringExtra("param1")
        if (param != null) {
            if (param.startsWith("/s/")) {
                val action = param.substring(param.lastIndexOf("/") + 1)
                when (action) {
                    "deleteAccount" -> {
                        intent.setClass(activity, SettingsActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }

                    else -> {
                        val number = convert64ToString(action)
                        if (number.length == 10) {
                            intent.setClass(activity, ProfileDetailsActivity::class.java)
                            intent.putExtra("phone", number)
                            CommonMethod.switchActivity(activity, intent, false)
                        }
                    }
                }
            }

        }
    }

    private fun checkFHTOn(activity: BaseActivity, userId: String) {
        activity.baseApiController.getUser(false, userId, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                if (resp?.data?.isFHTSearch?.value == true) {
                    val intent = Intent(activity, ProfileDetailsActivity::class.java)
                    intent.putExtra("searchType", BaseActivity.TYPE_FHT)
                    intent.putExtra("phone", userId)
                    intent.putExtra(
                        RouteConstants.ROUTE_CONSTANT_FROM,
                        RouteConstants.ROUTE_CONSTANT_DEEPLINK
                    )
                    CommonMethod.switchActivity(activity, intent, false)
                } else {
                    AlertDialog.showAlert(
                        activity,
                        "Looks like they closed the search for now. Good luck with your search! "
                                + activity.getEmojiByUnicode(0x1F50D)
                    )
                }
            }

        })
    }

    /*-----------------------------DEPRECATED-----------------------------*/
    fun createFlatDeepLink(mongoId: String): String {
        return UrlConstants.DEEPLINK_BASE_URL + convertTo64(mongoId)
    }

    fun createUserDeepLink(id: String): String {
        return UrlConstants.DEEPLINK_BASE_URL + "s/" + convertTo64(id)
    }

    private fun convertTo64(text: String): String {
        val data: ByteArray = text.toByteArray(StandardCharsets.UTF_8)
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    private fun convert64ToString(encoded: String): String {
        val data = Base64.decode(encoded, Base64.DEFAULT)
        return String(data, StandardCharsets.UTF_8)
    }
    /*-----------------------------DEPRECATED ENDS-----------------------------*/

    fun createFlatDynamicLink(flat: MyFlatData?, callback: OnStringFetched) {
        if (flat?.deepLink.isNullOrEmpty()) {
            createDynamicLink("fms", flat?.mongoId!!, callback)
        } else callback.onFetched(flat?.deepLink)
    }

    private fun createDynamicLink(base: String, url: String, callback: OnStringFetched) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://joinfs.app/$base/$url")
            domainUriPrefix = "https://joinfs.app/$base"
            buildShortDynamicLink()
        }.addOnSuccessListener { (shortLink, _) ->
            CommonMethod.makeLog("Dynamic Link", shortLink?.toString())
            callback.onFetched(shortLink?.toString())
        }.addOnFailureListener {
            callback.onFetched("0")
            CommonMethod.makeToast("Failed to create link")
        }
    }
}