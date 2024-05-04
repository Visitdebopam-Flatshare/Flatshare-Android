package com.joinflatshare.ui.base

import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.notifications.NotificationActivity
import com.joinflatshare.ui.notifications.RequestHandler
import com.joinflatshare.ui.profile.myprofile.ProfileActivity

class NotificationIconHandler {
    companion object {
        fun evaluateIcon(activity: BaseActivity) {
            if (activity is ExploreActivity
                || activity is ChatListActivity
                || activity is NotificationActivity
                || activity is ProfileActivity
            ) {
                RequestHandler.calculateTotalRequestCount(activity)
            }
        }
    }
}