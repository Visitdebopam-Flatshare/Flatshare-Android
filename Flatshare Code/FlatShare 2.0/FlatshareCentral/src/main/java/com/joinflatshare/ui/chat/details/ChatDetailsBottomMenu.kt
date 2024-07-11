package com.joinflatshare.ui.chat.details

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.interfaces.OnFlatFetched
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.interfaces.OnitemClick
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.invite.InvitedResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.report.DialogReport
import com.joinflatshare.ui.dialogs.share.DialogShare
import com.joinflatshare.ui.flat.details.FlatDetailsActivity
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.permission.PermissionUtil
import com.joinflatshare.utils.system.Prefs
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.user.User

class ChatDetailsBottomMenu(
    private val activity: ChatDetailsActivity
) {
    fun click(groupChannel: GroupChannel) {
        if (groupChannel.url.startsWith("USER_")) {
            val userIDs = java.util.ArrayList<String>()
            userIDs.add(activity.sendBirdChannel.getChannelDisplayUserId(groupChannel))
            activity.sendBirdUser.getBlockedUserList(
                userIDs
            ) { users: List<User?> ->
                showUserMenu(groupChannel, users.isNotEmpty())
            }
        } else if (groupChannel.url.startsWith("FLAT_")) {
            // Channel created when a flat is created and all flat members are inside it
            val flatId = activity.sendBirdChannel.getFlatId(groupChannel.url)
            activity.baseApiController.getFlat(true, flatId, object : OnFlatFetched {
                override fun flatFetched(resp: FlatResponse?) {
                    showFlatMenu(groupChannel, resp)
                }
            })
        } else if (groupChannel.url.startsWith("FLATMATE_")) {
            // Channel is created when there is a connection (U2F or F2U) or when there is a match (U2F or F2U)
            val flatId = activity.sendBirdChannel.getFlatId(groupChannel.url)
            activity.baseApiController.getFlat(true, flatId, object : OnFlatFetched {
                override fun flatFetched(resp: FlatResponse?) {
                    showFlatmateMenu(groupChannel, resp)
                }
            })
        }
    }

    private fun showUserMenu(groupChannel: GroupChannel, isBlocked: Boolean) {
        val items = ArrayList<ModelBottomSheet>()
        items.add(
            ModelBottomSheet(
                R.drawable.ic_user,
                activity.sendBirdChannel.getChannelDisplayName(groupChannel) + "_" + activity.sendBirdChannel.getChannelDisplayImage(
                    groupChannel
                ),
                1
            )
        )
        items.add(ModelBottomSheet(R.drawable.ic_chat_clear, "Clear Chat", 3))
        // Report user
        items.add(
            ModelBottomSheet(
                R.drawable.ic_report_red,
                "Report " + activity.sendBirdChannel.getChannelDisplayName(
                    groupChannel
                ),
                3
            )
        )
        // Check blocked status
        val blocked = if (isBlocked) "Unblock " else "Block "
        items.add(
            ModelBottomSheet(
                R.drawable.ic_chat_block, blocked + activity.sendBirdChannel.getChannelDisplayName(
                    groupChannel
                ), 3
            )
        )
        if (groupChannel.customType.equals(SendBirdConstants.CHANNEL_TYPE_FRIEND)) items.add(
            ModelBottomSheet(R.drawable.ic_chat_remove, "Remove from Friends", 3)
        )
        else if (groupChannel.customType.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_U2F)
            || groupChannel.customType.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_F2U)
            || groupChannel.customType.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_FHT)
            || groupChannel.customType.equals(SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT)
        )
            items.add(
                ModelBottomSheet(R.drawable.ic_chat_remove, "Unmatch", 3)
            )
        else items.add(ModelBottomSheet(R.drawable.ic_chat_remove, "Leave Chat", 3))
        BottomSheetView(activity,items) { _, position ->
            when (position) {
                0 -> {
                    // View Profile
                    val intent = Intent(activity, ProfileDetailsActivity::class.java)
                    intent.putExtra(
                        "phone", activity.sendBirdChannel.getChannelDisplayUserId(groupChannel)
                    )
                    CommonMethod.switchActivity(activity, intent, false)
                }

                1 -> {
                    // Clear Chat
                    AlertDialog.showAlert(
                        activity,
                        "",
                        "Are you sure you want to\nclear this chat?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            activity.sendBirdChannel.removeChatHistory(
                                groupChannel
                            ) {
                                activity.messageList.clear()
                                activity.dataBind.adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                2 -> {
                    // Report user
                    DialogReport(
                        activity,
                        BaseActivity.TYPE_USER,
                        activity.sendBirdChannel.getChannelDisplayUserId(groupChannel), null,
                    ) {
                        CommonMethod.finishActivity(activity)
                    }
                }

                3 -> {
                    // Block user
                    val title = if (isBlocked) "Unblock" else "Block"
                    AlertDialog.showAlert(
                        activity,
                        "",
                        "Are you sure you want to\n$title ${
                            activity.sendBirdChannel.getChannelDisplayName(
                                groupChannel
                            )
                        }?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            if (isBlocked) {
                                activity.sendBirdUser.unblockUser(
                                    activity.sendBirdChannel.getChannelDisplayUserId(
                                        groupChannel
                                    )
                                ) { _, _ ->
                                    CommonMethod.makeToast("Unblocked")
                                    CommonMethod.finishActivity(activity)
                                }
                            } else {
                                activity.sendBirdUser.blockUser(
                                    activity.sendBirdChannel.getChannelDisplayUserId(
                                        groupChannel
                                    )
                                ) { _, _ ->
                                    CommonMethod.makeToast(
                                        if (isBlocked) "Unblocked" else "Blocked"
                                    )
                                    CommonMethod.finishActivity(activity)
                                }
                            }

                        }
                    }
                }

                4 -> {
                    val message =
                        if (items[position].name.equals("Remove from Friends"))
                            "All your chats will be deleted.\nAre you sure you want to unfriend?"
                        else "All your chats will be deleted.\nAre you sure you want to unmatch?"
                    AlertDialog.showAlert(
                        activity, items[position].name,
                        message,
                        "Yes", "No",
                    ) { _, requestCode ->
                        if (requestCode == 1) {
                            when (items[position].name) {
                                "Remove from Friends" -> {
                                    activity.apiManager.removeFriend(
                                        activity.sendBirdChannel.getChannelDisplayUserId(
                                            activity.groupChannel
                                        )
                                    ) { CommonMethod.finishActivity(activity) }
                                }

                                else -> {
                                    var connectionType = ""
                                    if (groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_CONNECTION_U2F) == true
                                        || groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_U2F) == true
                                    )
                                        connectionType =
                                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                                    else if (groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_CONNECTION_F2U
                                        ) == true
                                        || groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_MATCH_F2U
                                        ) == true
                                    )
                                        connectionType =
                                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                                    else if (groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
                                        ) == true
                                        || groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_MATCH_FHT
                                        ) == true
                                    )
                                        connectionType =
                                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                                    else if (groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_CONNECTION_CASUAL
                                        ) == true
                                        || groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_MATCH_CASUAL
                                        ) == true
                                    )
                                        connectionType =
                                            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                                    else if (groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_CONNECTION_LONG_TERM
                                        ) == true
                                        || groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_MATCH_LONG_TERM
                                        ) == true
                                    )
                                        connectionType =
                                            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                                    else if (groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_CONNECTION_ACTIVITY_PARTNERS
                                        ) == true
                                        || groupChannel.customType?.equals(
                                            SendBirdConstants.CHANNEL_TYPE_MATCH_ACTIVITY_PARTNERS
                                        ) == true
                                    )
                                        connectionType =
                                            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                                    activity.apiManager.removeConnection(
                                        activity.sendBirdChannel.getChannelDisplayUserId(
                                            groupChannel
                                        ), connectionType
                                    ) { CommonMethod.finishActivity(activity) }
                                }
                            }
                            // TODO removing the below line to test backend sendbird
                            //                            activity.sendBirdChannel.deleteChannel(groupChannel.url)

                        }
                    }
                }
            }
        }
    }

    private fun showFlatMenu(groupChannel: GroupChannel, flatResponse: FlatResponse?) {
        val items = ArrayList<ModelBottomSheet>()
        items.add(
            ModelBottomSheet(
                if (groupChannel.url.startsWith("FLATMATE_")) R.drawable.ic_flat_chat else R.drawable.ic_flat_bg,
                flatResponse?.data?.name + "_" + activity.sendBirdChannel.getChannelDisplayImage(
                    groupChannel
                ),
                1
            )
        )
        items.add(ModelBottomSheet(R.drawable.ic_camera, "Change Flat Photo"))
        items.add(ModelBottomSheet(R.drawable.ic_chat_plus, "Add New Member"))
        items.add(
            ModelBottomSheet(
                R.drawable.ic_report_red, "Report " + flatResponse?.data?.name, 3
            )
        )
        items.add(ModelBottomSheet(R.drawable.ic_chat_clear, "Clear Chat", 3))
        items.add(ModelBottomSheet(R.drawable.ic_leave_flat_red, "Leave Flat", 3))

        BottomSheetView(activity,items) { _, position ->
            when (position) {
                0 -> {
                    val intent = Intent(activity, FlatDetailsActivity::class.java)
                    intent.putExtra("phone", flatResponse?.data?.id)
                    CommonMethod.switchActivity(activity, intent, false)
                }

                1 -> {
                    PermissionUtil.validatePermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        object : OnPermissionCallback {
                            override fun onCallback(granted: Boolean) {
                                if (granted) activity.pickImage()
                            }
                        })
                }

                2 -> {
                    val intent = Intent(activity, InviteActivity::class.java)
                    intent.putExtra(
                        RouteConstants.ROUTE_CONSTANT_FROM,
                        InviteActivity.INVITE_TYPE_FLAT
                    )
                    CommonMethod.switchActivity(activity, intent, false)
                }

                3 -> {
                    // Report flat
                    DialogReport(
                        activity, BaseActivity.TYPE_FLAT, "", flatResponse?.data
                    ) {
                        activity.apiManager.showProgress()
                        val list = ArrayList<String>()
                        list.add(AppConstants.loggedInUser?.id!!)
                        activity.sendBirdChannel.leaveChannel(
                            groupChannel.url, list
                        ) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                DialogCustomProgress.hideProgress(activity);
                                CommonMethod.finishActivity(activity)
                            }, 500)
                        }
                    }
                }

                4 -> {
                    // Clear Chat
                    AlertDialog.showAlert(
                        activity,
                        "",
                        "Are you sure you want to\nclear this chat?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            activity.sendBirdChannel.removeChatHistory(
                                groupChannel
                            ) {
                                activity.messageList.clear()
                                activity.dataBind.adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                5 -> {
                    AlertDialog.showAlert(
                        activity, "", "Are you sure you want to\nleave this shared flat?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            val phone = AppConstants.loggedInUser?.id!!
                            activity.apiManager.showProgress()
                            activity.apiManager.removeFlatmate(
                                phone
                            ) {
                                AppConstants.isFeedReloadRequired = true
                                CommonMethod.clearFlat()
                                activity.prefs.setString(
                                    Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "1"
                                )
                                CommonMethod.finishActivity(activity)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun showFlatmateMenu(groupChannel: GroupChannel, flatResponse: FlatResponse?) {
        val items = ArrayList<ModelBottomSheet>()
        // Invite name to flatname
        var memberName = ""
        var memberId = ""
        if (!flatResponse?.data?.mongoId.isNullOrBlank()) items.add(
            ModelBottomSheet(
                R.drawable.ic_view_card_tilted,
                "View Flat Profile"
            )
        )
        activity.apiManager.showProgress()
        getInvitedUserForMatch(
            groupChannel, flatResponse
        ) { intent, requestCode ->
            if (requestCode == 1) {
                if (intent.hasExtra("userprofile"))
                    items.add(
                        ModelBottomSheet(
                            R.drawable.ic_user,
                            intent.getStringExtra("userprofile"),
                            1
                        )
                    )
                if (intent.hasExtra("invite")) items.add(
                    ModelBottomSheet(R.drawable.ic_user, intent.getStringExtra("invite"), 1)
                )
                memberId = intent.getStringExtra("memberId")!!
                memberName = intent.getStringExtra("memberName")!!
            }
            /*if (CommonMethods.isFlatComplete(flatResponse.data)) items.add(
                ModelBottomSheet(
                    R.drawable.ic_share1,
                    "Share Flat Profile"
                )
            )*/
            DialogCustomProgress.hideProgress(activity);
            items.add(ModelBottomSheet(R.drawable.ic_chat_clear, "Clear Chat", 3))
            items.add(ModelBottomSheet(R.drawable.ic_report_red, "Report and Unmatch", 3))
            items.add(ModelBottomSheet(R.drawable.ic_cross_red, "Unmatch", 3))
            BottomSheetView(activity, items) { _, position ->
                var name = items[position].name
                if (name.contains("_")) name = name.substring(0, name.indexOf("_"))
                if (name.equals("View Flat Profile")) {
                    val intnt = Intent(activity, FlatDetailsActivity::class.java)
                    intnt.putExtra("phone", flatResponse?.data?.mongoId)
                    CommonMethod.switchActivity(activity, intnt, false)
                } else if (name.equals(memberName)) {
                    val intnt = Intent(activity, ProfileDetailsActivity::class.java)
                    intnt.putExtra("phone", memberId)
                    CommonMethod.switchActivity(activity, intnt, false)
                } else if (name.startsWith("Invite $memberName")) {
                    activity.apiManager.addFlatmate(memberId) {
                        CommonMethod.makeToast("Invite sent to $memberName")
                    }
                } else if (name.equals("Share Flat Profile")) {
                    DialogShare(
                        activity,
                        "Share Flat Profile",
                        BaseActivity.TYPE_FLAT,
                        flatResponse?.data,
                        null
                    )
                } else if (name.equals("Clear Chat")) {
                    // Clear Chat
                    AlertDialog.showAlert(
                        activity,
                        "",
                        "Are you sure you want to\nclear this chat?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            activity.sendBirdChannel.removeChatHistory(
                                groupChannel
                            ) {
                                activity.messageList.clear()
                                activity.dataBind.adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } else if (name.equals("Report and Unmatch")) {
                    DialogReport(
                        activity, BaseActivity.TYPE_FLAT, "", flatResponse?.data
                    ) {
                        var connectionType = ""
                        if (groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_CONNECTION_U2F) == true
                            || groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_U2F) == true
                        )
                            connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                        else if (groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_CONNECTION_F2U
                            ) == true
                            || groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_MATCH_F2U
                            ) == true
                        )
                            connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        else if (groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
                            ) == true
                            || groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_MATCH_FHT
                            ) == true
                        )
                            connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        else if (groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_CONNECTION_CASUAL
                            ) == true
                            || groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_MATCH_CASUAL
                            ) == true
                        )
                            connectionType =
                                "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                        else if (groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_CONNECTION_LONG_TERM
                            ) == true
                            || groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_MATCH_LONG_TERM
                            ) == true
                        )
                            connectionType =
                                "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                        else if (groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_CONNECTION_ACTIVITY_PARTNERS
                            ) == true
                            || groupChannel.customType?.equals(
                                SendBirdConstants.CHANNEL_TYPE_MATCH_ACTIVITY_PARTNERS
                            ) == true
                        )
                            connectionType =
                                "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS

                        activity.apiManager.removeConnection(
                            memberId, connectionType
                        ) { CommonMethod.finishActivity(activity) }
                    }
                } else if (name.equals("Unmatch")) {
                    AlertDialog.showAlert(
                        activity, "", "Are you sure you want to unmatch?",
                        "Yes", "No",
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            unMatchConnection(groupChannel)
                        }

                    }
                }
            }
        }
    }

    private fun unMatchConnection(groupChannel: GroupChannel) {
        var connectionType = ""
        if (groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_CONNECTION_U2F) == true
            || groupChannel.customType?.equals(SendBirdConstants.CHANNEL_TYPE_MATCH_U2F) == true
        )
            connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
        else if (groupChannel.customType?.equals(
                SendBirdConstants.CHANNEL_TYPE_CONNECTION_F2U
            ) == true
            || groupChannel.customType?.equals(
                SendBirdConstants.CHANNEL_TYPE_MATCH_F2U
            ) == true
        )
            connectionType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U

        val sendbirdUrlUserId = groupChannel.url.substring(groupChannel.url.lastIndexOf("_") + 1)
        if (sendbirdUrlUserId == AppConstants.loggedInUser?.id) {
            // find the id of the flat owner as a unmatch id
            val flatId = groupChannel.url.substring(
                groupChannel.url.indexOf("_") + 1,
                groupChannel.url.lastIndexOf("_")
            )
            activity.baseApiController.getFlat(true, flatId, object : OnFlatFetched {
                override fun flatFetched(resp: FlatResponse?) {
                    val flat = resp?.data
                    if (flat != null)
                        activity.apiManager.removeConnection(
                            flat.id, connectionType
                        ) { CommonMethod.finishActivity(activity) }
                }

            })
        } else {
            // I am a flat member. Unmatch with the sendbird url user id
            activity.apiManager.removeConnection(
                sendbirdUrlUserId, connectionType
            ) { CommonMethod.finishActivity(activity) }
        }
    }

    private fun getInvitedUserForMatch(
        groupChannel: GroupChannel, flatResponse: FlatResponse?, callback: OnUiEventClick
    ) {

        var memberName = ""
        var memberImageUrl = ""
        val intent = Intent()
        val userId = groupChannel.url.substring(groupChannel.url.lastIndexOf("_") + 1)

        if (userId.isNotEmpty()) {
            for (member in groupChannel.members) {
                if (member.userId == userId) {
                    memberName = member.nickname
                    memberImageUrl = member.profileUrl
                    break
                }
            }
        }

        val flatId = groupChannel.url.substring(
            groupChannel.url.indexOf("_") + 1,
            groupChannel.url.lastIndexOf("_")
        )
        intent.putExtra("flatId", flatId)
        intent.putExtra("memberId", userId)
        intent.putExtra("memberName", memberName)
        if (!AppConstants.loggedInUser?.id.equals(userId)) {
            intent.putExtra(
                "userprofile",
                memberName + "_" + ImageHelper.getProfileImageWithAwsFromPath(memberImageUrl)
            )
        }
        if (!flatResponse?.data?.mongoId.isNullOrBlank()) {
            getInvitedStatus(
                userId, memberName, flatResponse
            ) { text ->
                if (text.equals("1")) {
                    intent.putExtra(
                        "invite",
                        "Invite $memberName to ${flatResponse?.data?.name}_" + ImageHelper.getProfileImageWithAwsFromPath(
                            memberImageUrl
                        )
                    )
                }
                callback.onClick(intent, 1)
            }
        } else callback.onClick(intent, 1)
    }

    private fun getInvitedStatus(
        nonFlatMemberId: String,
        memberName: String,
        flatResponse: FlatResponse?,
        callback: OnStringFetched
    ) {
        val request = InvitedRequest()
        request.ids.add(nonFlatMemberId)
        activity.apiManager.getInvitedStatus(
            InviteActivity.INVITE_TYPE_FLAT, request
        ) { response: Any? ->
            val resp = response as InvitedResponse?
            if (resp!!.data.isNotEmpty()) {
                val item = resp.data[0]
                if (!item.isFlatmate && !item.isRequested) {
                    callback.onFetched("1")
                } else callback.onFetched("0")
            }
        }
    }
}