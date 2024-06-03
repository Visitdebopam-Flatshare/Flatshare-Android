package com.joinflatshare.ui.base

import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.checks.RequestHandler
import com.joinflatshare.ui.profile.myprofile.ProfileActivity

class NotificationIconHandler {
    companion object {
        fun evaluateIcon(activity: BaseActivity) {
            if (activity is ExploreActivity
                || activity is ChatListActivity
                || activity is ChecksActivity
                || activity is ProfileActivity
            ) {
                RequestHandler.calculateTotalRequestCount(activity)
            }
        }
    }
}