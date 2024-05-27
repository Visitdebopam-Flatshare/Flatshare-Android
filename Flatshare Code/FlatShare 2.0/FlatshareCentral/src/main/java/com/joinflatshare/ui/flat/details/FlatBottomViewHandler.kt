package com.joinflatshare.ui.flat.details

import android.view.View
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatDetailsBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.pojo.invite.InvitedResponse

/**
 * Created by debopam on 14/03/23
 */
class FlatBottomViewHandler(
    private val activity: FlatDetailsActivity,
    private val viewBinding: ActivityFlatDetailsBinding
) {
    fun handleBottomView() {
        viewBinding.rlHolderBottom.visibility = View.GONE

        val fromIntent = activity.intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)

        if (!fromIntent.isNullOrEmpty()) {
            when (fromIntent) {
                RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST -> {
                    if (activity.intent.hasExtra("connectionType")) {
                        val connectionType = activity.intent.getStringExtra("connectionType")
                        activity.chatConnectionType = connectionType
                        // Since it is coming from incoming chat request, so the connection type will be reversed for DialogConnection
                        if (connectionType == ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F)
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        else activity.chatConnectionForDialogMatch =
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                    }
                    viewBinding.rlProfileBottomDefault.visibility = View.GONE
                    viewBinding.rlProfileBottomRequest.visibility = View.VISIBLE
                    showBottomView()
                }

                RouteConstants.ROUTE_CONSTANT_DEEPLINK -> {
                    activity.chatConnectionType =
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                    activity.chatConnectionForDialogMatch =
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                    showDefaultView()
                }

                RouteConstants.ROUTE_CONSTANT_FLAT_REQUEST -> {
                    viewBinding.rlProfileBottomDefault.visibility = View.GONE
                    viewBinding.rlProfileBottomRequest.visibility = View.VISIBLE
                    showBottomView()
                }

                RouteConstants.ROUTE_CONSTANT_FEED -> {
                    activity.chatConnectionType =
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                    activity.chatConnectionForDialogMatch =
                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                    showDefaultView()
                }
            }
        } else initUserData()
    }

    private fun checkLiked() {
        // Check if intent is from deeplink, then do not show like
        val fromIntent = activity.intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)
        if (!fromIntent.isNullOrEmpty() && fromIntent == RouteConstants.ROUTE_CONSTANT_DEEPLINK) {
            viewBinding.cardLike.visibility = View.GONE
            viewBinding.cardNotInterested.visibility=View.VISIBLE
        } else {
            if (activity.flatResponse != null) {
                viewBinding.cardLike.visibility =
                    if (activity.flatResponse.isLiked) View.GONE else View.VISIBLE
                viewBinding.cardNotInterested.visibility =
                    if (activity.flatResponse.isLiked) View.GONE else View.VISIBLE
            }
        }
    }

    private fun showDefaultView() {
        checkLiked()
        activity.apiController.getConnectionData {
            val resp = it as InvitedResponse?
            val data = resp?.data
            if (!data.isNullOrEmpty()) {
                if (data[0].isConnected) {
                    viewBinding.txtChatRequest.text = "Connected"
                    viewBinding.txtChatRequest.alpha = 0.7f
                } else if (data[0].hasSentConnection) {
                    viewBinding.txtChatRequest.text = "Chat Request Sent"
                    viewBinding.txtChatRequest.alpha = 0.7f
                } else if (data[0].hasReceivedConnection) {
                    viewBinding.rlProfileBottomDefault.visibility = View.GONE
                    viewBinding.rlProfileBottomRequest.visibility = View.VISIBLE
                } else {
                    viewBinding.txtChatRequest.text = "Chat Request"
                    viewBinding.txtChatRequest.alpha = 1f
                }
            }
        }
        showBottomView()
    }

    private fun showBottomView() {
        viewBinding.rlHolderBottom.visibility = View.VISIBLE
        initUserData()
    }

    private fun initUserData() {
        DialogCustomProgress.hideProgress(activity);
        activity.dataBinder.setData()
        activity.listener = FlatDetailsListener(activity, viewBinding)
    }
}