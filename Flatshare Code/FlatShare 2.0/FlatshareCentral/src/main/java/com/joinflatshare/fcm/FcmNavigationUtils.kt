package com.joinflatshare.fcm

import android.content.Intent
import com.joinflatshare.constants.NotificationConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.checks.request_invitation.InvitationRequestActivity
import com.joinflatshare.ui.flat.edit.FlatEditActivity
import com.joinflatshare.ui.profile.edit.ProfileEditActivity
import com.joinflatshare.ui.profile.myprofile.ProfileActivity
import com.joinflatshare.utils.helper.CommonMethod

class FcmNavigationUtils(activity: BaseActivity, intent: Intent) {
    init {
        var intnt: Intent? = null
        if (!intent.hasExtra("type")) {
            intnt = Intent(activity, ChecksActivity::class.java)
        } else {
            when (intent.getStringExtra("type")) {
                NotificationConstants.NOTIFICATION_TYPE_INVITE_FLAT_MEMBER -> {
                    intnt = Intent(activity, InvitationRequestActivity::class.java)
                }

                NotificationConstants.NOTIFICATION_TYPE_NO_NAVIGATION,
                NotificationConstants.NOTIFICATION_TYPE_GENERAL_USER,
                NotificationConstants.NOTIFICATION_TYPE_GENERAL_FLAT -> {
                    intnt = Intent(activity, ChecksActivity::class.java)
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