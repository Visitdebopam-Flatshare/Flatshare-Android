package com.joinflatshare.ui.flat.details

import android.app.Activity
import android.content.Intent
import android.view.View
import com.debopam.flatshareprogress.DialogCustomProgress
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication.Companion.getDbInstance
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatDetailsBinding
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.api.retrofit.WebserviceCustomRequestHandler.getLikeRequestUrl
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.customviews.inapp_review.InAppReview.show
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.payment.PaymentHandler.showPaymentForChats
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.likes.LikeRequest
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.DialogConnection
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils.onChatRequested
import com.joinflatshare.utils.mixpanel.MixpanelUtils.onMatched
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 01/02/24
 */
class FlatDetailsApiController(
    private val activity: FlatDetailsActivity,
    private val viewBind: ActivityFlatDetailsBinding
) {
    var callbackIntent: Intent? = null
    fun setCallBackIntent(intent: Intent) {
        callbackIntent = intent
    }

    fun getFlat() {
        activity.baseApiController.getFlat(
            false,
            activity.phone
        ) { resp: FlatResponse? ->
            activity.flatResponse = resp
            if (activity.flatResponse != null) {
                val flatData =
                    getDbInstance().userDao().getFlatData()
                if (flatData != null && activity.flatResponse.data!!.mongoId == flatData.mongoId
                ) {
                    activity.dataBinder.setData()
                    activity.listener = FlatDetailsListener(activity, viewBind)
                    DialogCustomProgress.hideProgress(activity);
                } else {
                    activity.flatBottomViewHandler.handleBottomView()
                }
            }
        }
    }

    fun likeFlat() {
        val likeUrl = getLikeRequestUrl(
            BaseActivity.TYPE_FLAT,
            activity.chatConnectionForDialogMatch,
            activity.flatResponse.data!!.mongoId
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
                    DialogLottieViewer(activity, R.raw.lottie_like, null);
                    val resp =
                        Gson().fromJson(response, BaseResponse::class.java)
                    if (resp.status == 200) {
                        activity.flatResponse.isLiked = true
                        viewBind.cardLike.visibility = View.GONE
                        viewBind.cardNotInterested.visibility = View.GONE
                        if (resp.matched) {
                            onMatched(activity.flatResponse.data!!.mongoId, "Shared Flat Search")
                            showConnectionMatch()
                            if (resp.shouldShowReview()) show(activity)
                        }
                        if (callbackIntent == null) callbackIntent = Intent()
                        callbackIntent!!.putExtra("like", true)
                    }
                }
            })

    }

    fun getConnectionData(callback: OnResponseCallback<Any>) {
        val request = InvitedRequest()
        if (activity.flatResponse.flatMates.size > 0)
            for (temp in activity.flatResponse.flatMates)
                request.ids.add(temp.id)
        else request.ids.add(
            activity.flatResponse.data!!.id
        )
        request.type = activity.chatConnectionType
        activity.apiManager.getInvitedStatus(
            InviteActivity.INVITE_TYPE_CONNECTION, request
        ) { response: Any? ->
            callback.oncallBack(
                response
            )
        }
    }

    fun sendConnectionRequest() {
        activity.apiManager.sendConnectionRequest(true,
            activity.chatConnectionForDialogMatch, activity.flatResponse.data!!.id
        ) { response: Any? ->
            if (isRestricted(response)) {
                // Checking payment gateway
                showPaymentForChats(activity, null)
            } else {
                DialogLottieViewer(activity, R.raw.lottie_chat_request, null)
                val resp = response as BaseResponse?
                if (resp!!.status == 200) {
                    onChatRequested(
                        AppConstants.loggedInUser!!.id,
                        activity.flatResponse.data!!.mongoId,
                        "Shared Flat Search"
                    )
                    if (resp.message == "Accepted.") {
                        showConnectionMatch()
                        viewBind.rlHolderBottom.visibility = View.GONE
                    } else {
                        viewBind.txtChatRequest.text = "Chat Request Sent"
                        viewBind.txtChatRequest.alpha = 0.7f
                    }
                    if (callbackIntent == null)
                        callbackIntent = Intent()
                    callbackIntent!!.putExtra("chat", true)
                }
            }
        }
    }

    fun reportNotInterested() {
        DialogLottieViewer(activity, R.raw.lottie_not_interested, null)
        activity.apiManager.report(
            false,
            BaseActivity.TYPE_FLAT,
            0,
            activity.flatResponse.data!!.mongoId
        ) { response: Any? ->
            val resp = response as BaseResponse?
            if (resp!!.status == 200) {
                if (callbackIntent == null)
                    callbackIntent = Intent()
                callbackIntent!!.putExtra("report", true)
                activity.onBackPressed()
            }
        }
    }


    fun handleRequest(id: String?, isAccept: Boolean, callback: OnStringFetched?) {
        when (activity.intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)) {
            RouteConstants.ROUTE_CONSTANT_FLAT_REQUEST -> {
                if (isAccept) {
                    activity.apiManager.acceptFlatInvitation(
                        id!!
                    ) { response: Any? ->
                        clearRequestNotification(
                            callback
                        )
                    }
                } else {
                    activity.apiManager.rejectFlatInvitation(
                        id!!
                    ) { response: Any? ->
                        clearRequestNotification(
                            callback
                        )
                    }
                }
            }

            RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST -> {
                if (isAccept) {
                    activity.apiManager.acceptConnection(
                        id!!, activity.chatConnectionType
                    ) { response: Any? ->
                        clearRequestNotification(
                            callback
                        )
                    }
                } else {
                    activity.apiManager.rejectConnection(
                        id!!, activity.chatConnectionType
                    ) { response: Any? ->
                        clearRequestNotification(
                            callback
                        )
                    }
                }
            }
        }
    }

    private fun clearRequestNotification(callback: OnStringFetched?) {
        activity.intent.removeExtra(RouteConstants.ROUTE_CONSTANT_FROM)
        val notificationId = activity.intent.getStringExtra("notificationId")
        if (!notificationId.isNullOrEmpty()) {
            activity.intent.removeExtra("notificationId")
            getDbInstance().requestDao()
                .deleteRequest(notificationId)
        }
        activity.setResult(Activity.RESULT_OK)
        callback?.onFetched("1")
    }

    private fun showConnectionMatch() {
        DialogConnection(
            activity,
            AppConstants.loggedInUser,
            activity.flatResponse.data!!.mongoId, null,
            activity.chatConnectionForDialogMatch
        )
    }

    private fun isRestricted(response: Any?): Boolean {
        return response is Int
    }
}