package com.joinflatshare.ui.profile.details

import android.view.View
import com.debopam.flatshareprogress.DialogCustomProgress
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 13/03/23
 */
class ProfileBottomViewHandler(
    private val activity: ProfileDetailsActivity,
    private val viewBinding: ActivityProfileDetailsBinding
) {

    fun handleBottomView() {
        viewBinding.rlHolderBottom.visibility = View.GONE

        val fromIntent = activity.intent.getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)

        if (!fromIntent.isNullOrEmpty()) {
            when (fromIntent) {
                RouteConstants.ROUTE_CONSTANT_FRIEND_REQUEST -> {
                    viewBinding.rlProfileBottomDefault.visibility = View.GONE
                    viewBinding.rlProfileBottomRequest.visibility = View.VISIBLE
                    showBottomView()
                }

                RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST -> {
                    if (activity.intent.hasExtra("connectionType")) {
                        val connectionType = activity.intent.getStringExtra("connectionType")
                        activity.isFHTSearch =
                            connectionType.equals(ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT)

                        activity.chatConnectionType = connectionType
                        // Since it is coming from incoming chat request,
                        // so the connection type will be reversed for DialogConnection
                        if (connectionType == ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F)
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        else if (connectionType == ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U)
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                        else activity.chatConnectionForDialogMatch = connectionType
                    }
                    viewBinding.rlProfileBottomDefault.visibility = View.GONE
                    viewBinding.rlProfileBottomRequest.visibility = View.VISIBLE
                    showBottomView()
                }

                RouteConstants.ROUTE_CONSTANT_DEEPLINK -> {
                    if (activity.intent.hasExtra("searchType")) {
                        val searchType = activity.intent.getStringExtra("searchType")
                        if (searchType.equals(BaseActivity.TYPE_FHT)) {
                            activity.isFHTSearch = true
                            activity.chatConnectionType =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        } else {
                            activity.isFHTSearch = false
                            activity.chatConnectionType =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        }
                    }
                    showDefaultView()
                }

                RouteConstants.ROUTE_CONSTANT_FEED -> {
                    if (activity.intent.hasExtra("searchType")) {
                        val searchType = activity.intent.getStringExtra("searchType")
                        if (searchType.equals(BaseActivity.TYPE_FHT)) {
                            activity.isFHTSearch = true
                            activity.chatConnectionType =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                            activity.chatConnectionForDialogMatch =
                                ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        } else {
                            activity.isFHTSearch = false
                            when (searchType) {
                                BaseActivity.TYPE_USER -> {
                                    activity.chatConnectionType =
                                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                                    activity.chatConnectionForDialogMatch =
                                        ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                                }

                                BaseActivity.TYPE_DATE -> {
                                    activity.chatConnectionType =
                                        "" + AppConstants.loggedInUser?.dateProperties?.dateType
                                    activity.chatConnectionForDialogMatch =
                                        "" + AppConstants.loggedInUser?.dateProperties?.dateType
                                }
                            }
                        }
                    }
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
            viewBinding.cardNotInterested.visibility = View.VISIBLE
        } else {
            if (activity.userResponse != null) {
                viewBinding.cardLike.visibility =
                    if (activity.userResponse.isLiked) View.GONE else View.VISIBLE
                viewBinding.cardNotInterested.visibility =
                    if (activity.userResponse.isLiked) View.GONE else View.VISIBLE
            }
        }

    }

    private fun isUserMyFriend(): Boolean {
        if (!activity.userResponse.friends.isNullOrEmpty()) {
            for (temp in activity.userResponse.friends) {
                if (temp.id == AppConstants.loggedInUser?.id) {
                    return true
                }
            }
        }
        return false
    }

    private fun showDefaultView() {
        if (isUserMyFriend()) {
            viewBinding.rlProfileBottomDefault.visibility = View.GONE
            viewBinding.btnProfileMessage.visibility = View.VISIBLE
            showBottomView()
        } else {
            checkLiked()
            activity.apiController.getConnectionData {
                val data = it?.data
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
                showBottomView()
            }
        }
    }

    private fun showBottomView() {
        viewBinding.rlHolderBottom.visibility = View.VISIBLE
        initUserData()
    }

    private fun initUserData() {
        DialogCustomProgress.hideProgress(activity);
        activity.initUserData(activity.userResponse)
    }
}