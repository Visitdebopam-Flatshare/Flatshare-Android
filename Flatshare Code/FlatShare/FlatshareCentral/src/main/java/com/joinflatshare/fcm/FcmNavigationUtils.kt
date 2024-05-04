package com.joinflatshare.fcm

import android.content.Intent
import com.joinflatshare.constants.NotificationConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.ui.flat.edit.FlatEditActivity
import com.joinflatshare.ui.notifications.NotificationActivity
import com.joinflatshare.ui.notifications.request_chat.ChatRequestActivity
import com.joinflatshare.ui.notifications.request_friend.FriendRequestActivity
import com.joinflatshare.ui.notifications.request_invitation.InvitationRequestActivity
import com.joinflatshare.ui.profile.edit.ProfileEditActivity
import com.joinflatshare.ui.profile.myprofile.ProfileActivity
import com.joinflatshare.utils.helper.CommonMethod

class FcmNavigationUtils(activity: BaseActivity, intent: Intent) {
    init {
        var intnt: Intent? = null
        if (!intent.hasExtra("type")) {
            intnt = Intent(activity, NotificationActivity::class.java)
        } else {
            when (intent.getStringExtra("type")) {
                NotificationConstants.NOTIFICATION_TYPE_INVITE_FLAT_MEMBER -> {
                    intnt = Intent(activity, InvitationRequestActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_INVITE_FRIEND -> {
                    intnt = Intent(activity, FriendRequestActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_NO_NAVIGATION,
                NotificationConstants.NOTIFICATION_TYPE_GENERAL_USER,
                NotificationConstants.NOTIFICATION_TYPE_GENERAL_FLAT -> {
                    intnt = Intent(activity, NotificationActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_CONNECTION_F2U,
                NotificationConstants.NOTIFICATION_TYPE_CONNECTION_U2F,
                NotificationConstants.NOTIFICATION_TYPE_CHAT_SFS_F2U,
                NotificationConstants.NOTIFICATION_TYPE_CHAT_FMS_U2F,
                NotificationConstants.NOTIFICATION_TYPE_CONNECTION_FHT,
                NotificationConstants.NOTIFICATION_TYPE_CHAT_FHT_U2U,
                NotificationConstants.NOTIFICAITON_TYPE_DATING_CASUAL,
                NotificationConstants.NOTIFICATION_TYPE_DATING_CSU_U2U,
                NotificationConstants.NOTIFICAITON_TYPE_DATING_LONG_TERM,
                NotificationConstants.NOTIFICATION_TYPE_DATING_LTR_U2U,
                NotificationConstants.NOTIFICAITON_TYPE_DATING_ACTIVITY_PARTNERS,
                NotificationConstants.NOTIFICATION_TYPE_DATING_ACT_U2U,
                -> {
                    intnt = Intent(activity, ChatRequestActivity::class.java)
                    intnt.putExtra("type", intent.getStringExtra("type"))
                }
                /*NotificationConstants.NOTIFICATION_TYPE_CONVERSATION -> {
                    val channel = SendBirdFlatChannel(activity)
                    intnt = Intent(activity, ChatDetailsActivity::class.java)
                    intnt.putExtra(
                        "channel",
                        channel.getFlatChannelName(intent.getStringExtra("flatId")!!)
                    )
                }*/
                NotificationConstants.NOTIFICATION_TYPE_CHAT -> {
                    activity.baseViewBinder.ll_menu[1].performClick()
                }

                NotificationConstants.NOTIFICATION_TYPE_SENDBIRD -> {
                    intnt = Intent(activity, ChatListActivity::class.java)
                    intnt.putExtra("channel", intent.getStringExtra("channel"))
                }

                NotificationConstants.NOTIFICATION_TYPE_GENERAL_USER_EDIT -> {
                    intnt = Intent(activity, ProfileEditActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_GENERAL_FLAT_EDIT -> {
                    intnt = Intent(activity, FlatEditActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_GET_VERIFIED_500,
                NotificationConstants.NOTIFICATION_TYPE_GET_VERIFIED_500_PAST_12 -> {
                    intnt = Intent(activity, ProfileActivity::class.java)
                    intnt.putExtra("verify", true)
                }
            }
        }
        if (intnt != null) {
            CommonMethod.switchActivity(activity, intnt, false)
        }
    }
}