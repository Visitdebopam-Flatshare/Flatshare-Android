package com.joinflatshare.utils.mixpanel

import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.MixPanelConstants
import com.joinflatshare.pojo.requests.Requester
import com.joinflatshare.pojo.user.User
import com.joinflatshare.utils.helper.CommonMethod
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

/**
 * Created by debopam on 06/02/23
 */
object MixpanelUtils {

    fun setupToken() {
        if (AppConstants.isAppLive)
            MixPanelConstants.initialiseMixPanelToken {

            }
    }

    private fun getInstance(): MixpanelAPI? {
        return if (MixPanelConstants.MIXPANEL_TOKEN.isBlank()) {
            setupToken()
            null
        } else
            MixpanelAPI.getInstance(
                FlatShareApplication.instance,
                MixPanelConstants.MIXPANEL_TOKEN,
                true
            )
    }

    fun identity(user: User?) {
        getInstance()?.identify(user?.id)
        val props = JSONObject()
        props.put("userId", user?.id)
        props.put(
            "User Verified", user?.verification?.isVerified == true
        )
        props.put("Name", user?.name?.firstName + " " + user?.name?.lastName)
        getInstance()?.people?.set(props)
        register(props)
    }

    private fun register(props: JSONObject) {
        getInstance()?.registerSuperProperties(props)
    }

    fun logout() {
        getInstance()?.reset()
    }


    private fun sendToMixPanel(eventName: String, props: JSONObject) {
        val user = AppConstants.loggedInUser
        if (user != null && user.id.isNotBlank()) {
            props.put("userId", user.id)
        }
        if (AppConstants.isAppLive) {
            CommonMethod.makeLog("MIXPANEL", props.toString())
            getInstance()?.track(eventName, props)
        }
    }

    fun logError(reason: String, type: String) {
        val props = JSONObject()
        props.put("reason", reason)
        props.put("type", type)
        sendToMixPanel("LOGGER", props)
    }

    fun sendToMixPanel(eventName: String) {
        val props = JSONObject()
        sendToMixPanel(eventName, props)
    }

    fun numberEntered(number: String) {
        val props = JSONObject()
        props.put("mobile", number)
        sendToMixPanel("Get Started Clicked", props)
    }

    fun otpEntered(number: String, otp: String) {
        val props = JSONObject()
        props.put("mobile", number)
        props.put("otp", otp)
        sendToMixPanel("OTP Verified", props)
    }

    fun locationChecked(
        lat: Double,
        lng: Double,
        city: String,
        isAllowed: Boolean
    ) {
        val props = JSONObject()
        props.put("latitude", lat)
        props.put("longitude", lng)
        props.put("city", city)
        props.put("allowedInApp", isAllowed)
        sendToMixPanel("Location Retrieved", props)
        sendToMixPanel(if (isAllowed) "Location Available" else "Location Not Available", props)
    }

    fun locationFailed(reason: String) {
        val props = JSONObject()
        props.put("reason", reason)
        sendToMixPanel("Location Retrieve Failed", props)
    }

    fun onScreenOpened(name: String) {
        val props = JSONObject()
        sendToMixPanel("Screen $name", props)
    }

    fun onButtonClicked(name: String) {
        sendToMixPanel("$name Clicked")
    }

    fun onFriendRequested(user: User) {
        val props = JSONObject()
        props.put("Friend ID", user.id)
        if (!user.name?.firstName.isNullOrBlank())
            props.put("Friend Name", "${user.name?.firstName} ${user.name?.lastName}")
        sendToMixPanel("Friend Added", props)
    }

    fun onUserInvited(user: User) {
        val props = JSONObject()
        props.put("User ID", user.id)
        if (!user.name?.firstName.isNullOrBlank())
            props.put("User Name", "${user.name?.firstName} ${user.name?.lastName}")
        sendToMixPanel("Friend Invited", props)
    }

    fun onFlatmateInvited(user: User) {
        val props = JSONObject()
        props.put("Flatmate ID", user.id)
        if (!user.name?.firstName.isNullOrBlank())
            props.put("Flatmate Name", "${user.name?.firstName} ${user.name?.lastName}")
        sendToMixPanel("Flatmate Invited", props)
    }

    fun onCheck(to: String, searchType: String) {
        val props = JSONObject()
        props.put("Checked By", AppConstants.loggedInUser?.id)
        props.put("Checked To", to)
        props.put("Search Type", searchType)
        sendToMixPanel("Checked Clicked", props)
    }

    fun onMatched(userId: String, searchType: String) {
        val props = JSONObject()
        props.put("User 1", AppConstants.loggedInUser?.id)
        props.put("User 2", userId)
        props.put("Search Type", searchType)
        sendToMixPanel("New Match", props)
    }

    fun onChatRequested(to: String, searchType: String) {
        val props = JSONObject()
        props.put("Requested By", AppConstants.loggedInUser?.id)
        props.put("Requested To", to)
        props.put("Search Type", searchType)
        sendToMixPanel("Super Check Request Clicked", props)
    }

    fun onChatRequestAccepted(to: String) {
        val props = JSONObject()
        props.put("Accepted By", AppConstants.loggedInUser?.id)
        props.put("Requested By", to)
        props.put("User 1", AppConstants.loggedInUser?.id)
        props.put("User 2", to)
        sendToMixPanel("Super Check Accept", props)
    }

    fun onChatRequestRejected(to: String) {
        val props = JSONObject()
        props.put("Rejected By", AppConstants.loggedInUser?.id)
        props.put("Requested By", to)
        props.put("User 1", AppConstants.loggedInUser?.id)
        props.put("User 2", to)
        sendToMixPanel("Super Check Reject", props)
    }

    fun onPaymentSuccess(paymentDetails: String, paymentType: String) {
        val props = JSONObject()
        props.put("Payment Type", paymentType)
        props.put("Payment Details", paymentDetails)
        sendToMixPanel("New Payment", props)
    }

    fun onPurchaseFailure(
        paymentDetails: String,
        code: Int,
        exception: String,
        apiMessage: String
    ) {
        val props = JSONObject()
        props.put("Api Status Code", code)
        props.put("Api Exception", exception)
        props.put("Api Message", apiMessage)
        props.put("Payment Details", paymentDetails)
        sendToMixPanel("Purchase Failure", props)
    }

    fun onPurchaseSuccess(paymentDetails: String, apiRequest: String) {
        val props = JSONObject()
        props.put("Api Request", apiRequest)
        props.put("Payment Details", paymentDetails)
        sendToMixPanel("Purchase Success", props)
    }

}