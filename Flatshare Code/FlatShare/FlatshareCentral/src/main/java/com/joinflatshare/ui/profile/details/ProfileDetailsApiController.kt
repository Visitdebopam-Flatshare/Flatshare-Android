package com.joinflatshare.ui.profile.details

import android.app.Activity
import android.content.Intent
import android.view.View
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.inapp_review.InAppReview
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.invite.InvitedResponse
import com.joinflatshare.pojo.likes.LikeRequest
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 04/01/23
 */
class ProfileDetailsApiController(
    private val activity: ProfileDetailsActivity,
    private val viewBinding: ActivityProfileDetailsBinding
) {
    var callbackIntent: Intent? = null

    fun getProfile() {
        if (activity.userId == FlatShareApplication.getDbInstance().userDao()
                .get(UserDao.USER_CONSTANT_USERID)
        ) {
            activity.initUserData(FlatShareApplication.getDbInstance().userDao().getUserResponse())
        } else {
            activity.baseApiController.getUser(true, activity.userId, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    activity.userResponse = resp
                    activity.user = activity.userResponse.data
                    activity.profileBottomViewHandler.handleBottomView()
                }
            })
        }
    }

    fun likeUser(
        searchType: String,
        mixpanelSearchType: String
    ) {
        val likeUrl = WebserviceCustomRequestHandler.getLikeRequestUrl(
            searchType, activity.chatConnectionForDialogMatch,
            activity.userId
        )
        WebserviceManager().addLike(activity,
            likeUrl,
            LikeRequest(AppConstants.loggedInUser!!.location.loc.coordinates),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {

                override fun onCallBackPayment(count: Int) {
                    // Checking payment gateway
                    PaymentHandler.showPaymentForChecks(activity, null)
                }

                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, BaseResponse::class.java)
                    DialogLottieViewer(activity, R.raw.lottie_like, null)
                    if (resp!!.status == 200) {
                        activity.userResponse.isLiked = true
                        viewBinding.cardLike.visibility = View.GONE
                        viewBinding.cardNotInterested.visibility = View.GONE
                        MixpanelUtils.onLiked(activity.userId, mixpanelSearchType)
                        if (resp.matched) {
                            MixpanelUtils.onMatched(activity.userId, mixpanelSearchType)
                            showConnectionMatch()
                            if (resp.shouldShowReview())
                                InAppReview.show(activity)
                        }
                        if (callbackIntent == null)
                            callbackIntent = Intent()
                        callbackIntent?.putExtra("like", true)
                    }
                }
            })
    }

    fun sendChatRequest(
        searchType: String,
        mixpanelSearchType: String
    ) {

        activity.apiManager.sendConnectionRequest(
            activity.chatConnectionForDialogMatch, activity.user.id
        ) { response: Any? ->
            if (isRestricted(response)) {
                // Checking payment gateway
                PaymentHandler.showPaymentForChats(activity, null)
            } else {
                DialogLottieViewer(activity, R.raw.lottie_chat_request, null)
                val resp = response as com.joinflatshare.pojo.BaseResponse?
                if (resp?.status == 200) {
                    if (searchType.equals(BaseActivity.TYPE_USER)) {
                        val myFlat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                        MixpanelUtils.onChatRequested(
                            myFlat!!.mongoId, activity.userId, mixpanelSearchType
                        )
                    } else {
                        MixpanelUtils.onChatRequested(
                            AppConstants.loggedInUser!!.id, activity.userId, mixpanelSearchType
                        )
                    }
                    if (resp.message.equals("Accepted.")) {
                        showConnectionMatch()
                        viewBinding.rlHolderBottom.visibility = View.GONE
                    } else {
                        viewBinding.txtChatRequest.text = "Chat Request Sent"
                        viewBinding.txtChatRequest.alpha = 0.7f
                    }
                    if (callbackIntent == null) callbackIntent = Intent()
                    callbackIntent?.putExtra("chat", true)
                }
            }

        }
    }

    fun getConnectionData(callback: (InvitedResponse?) -> Unit) {
        val request = InvitedRequest()
        request.ids.add(activity.userId)
        request.type = activity.chatConnectionType
        activity.apiManager.getInvitedStatus(
            InviteActivity.INVITE_TYPE_CONNECTION, request
        ) { response: Any? ->
            val resp = response as InvitedResponse
            callback(resp)
        }
    }

    fun reportNotInterested() {
        DialogLottieViewer(activity, R.raw.lottie_not_interested, null)
        activity.apiManager.report(
            false, BaseActivity.TYPE_USER, 0, activity.userId
        ) { response ->
            val resp = response as com.joinflatshare.pojo.BaseResponse
            if (resp.status == 200) {
                viewBinding.cardLike.visibility = View.GONE
                viewBinding.cardNotInterested.visibility = View.GONE
                if (callbackIntent == null)
                    callbackIntent = Intent()
                callbackIntent?.putExtra("report", true)
                activity.onBackPressed()
            }
        }
    }

    fun handleRequest(id: String, isAccept: Boolean, callback: (Boolean) -> Unit) {
        when (activity.intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)) {
            RouteConstants.ROUTE_CONSTANT_FRIEND_REQUEST -> {
                if (isAccept) {
                    activity.apiManager.acceptFriendRequest(id) { clearRequestNotification(callback) }
                } else {
                    activity.apiManager.rejectFriendRequest(id) { clearRequestNotification(callback) }
                }
            }

            RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST -> {
                if (isAccept) {
                    activity.apiManager.acceptConnection(id, activity.chatConnectionType) {
                        showConnectionMatch()
                        clearRequestNotification(callback)
                    }
                } else {
                    activity.apiManager.rejectConnection(
                        id,
                        activity.chatConnectionType
                    ) { clearRequestNotification(callback) }
                }
            }
        }
    }

    private fun clearRequestNotification(callback: (Boolean) -> Unit) {
        val notificationId = activity.intent.getStringExtra("notificationId")
        if (!notificationId.isNullOrEmpty()) {
            activity.intent.removeExtra("notificationId")
            FlatShareApplication.getDbInstance().requestDao()
                .deleteRequest(notificationId)
        }
        activity.setResult(Activity.RESULT_OK)
        callback(true)
    }


    private fun showConnectionMatch(
    ) {
        when (activity.chatConnectionForDialogMatch) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                DialogConnection(
                    activity,
                    AppConstants.loggedInUser, null, activity.user,
                    activity.chatConnectionForDialogMatch
                )
            }

            else -> {
                val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                if (flat != null)
                    DialogConnection(
                        activity,
                        activity.user,
                        flat.mongoId, null,
                        activity.chatConnectionForDialogMatch
                    )
            }
        }
    }

    private fun isRestricted(response: Any?): Boolean {
        return (response is Int)
    }
}