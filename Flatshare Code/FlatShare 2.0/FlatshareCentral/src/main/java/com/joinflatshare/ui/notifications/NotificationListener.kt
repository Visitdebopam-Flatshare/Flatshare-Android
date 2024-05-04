package com.joinflatshare.ui.notifications

import android.app.Activity
import android.content.Intent
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityNotificationsBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.ui.notifications.request_chat.ChatRequestActivity
import com.joinflatshare.ui.notifications.request_friend.FriendRequestActivity
import com.joinflatshare.ui.notifications.request_invitation.InvitationRequestActivity
import com.joinflatshare.utils.helper.CommonMethod

class NotificationListener(
    private val activity: NotificationActivity,
    private val viewBind: ActivityNotificationsBinding
) : View.OnClickListener {
    init {
        viewBind.rlFriendRequests.setOnClickListener(this)
        viewBind.rlFlatRequests.setOnClickListener(this)
        viewBind.rlChatRequests.setOnClickListener(this)
        viewBind.btnInvite.setOnClickListener(this)
        viewBind.pullToRefresh.setOnRefreshListener {
            RequestHandler.getFriendRequest(activity, true)
            activity.fetchData()
            viewBind.pullToRefresh.isRefreshing = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.rlFriendRequests.id -> {
                val friendRequestCount = FlatShareApplication.getDbInstance().requestDao()
                    .getCount(ChatRequestConstants.FRIEND_REQUEST_CONSTANT)
                if (friendRequestCount > 0) {
                    val intent = Intent(activity, FriendRequestActivity::class.java)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK)
                            activity.dataBinder.bindFriendRequests()
                    }
                }
            }

            viewBind.rlFlatRequests.id -> {
                val flatRequestCount = FlatShareApplication.getDbInstance().requestDao()
                    .getCount(ChatRequestConstants.FLAT_REQUEST_CONSTANT)
                if (flatRequestCount > 0) {
                    val intent = Intent(activity, InvitationRequestActivity::class.java)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK)
                            activity.dataBinder.bindFriendRequests()
                    }
                }
            }

            viewBind.rlChatRequests.id -> {
                val dao = FlatShareApplication.getDbInstance().requestDao()
                val totalCount = dao.getTotalChatCount()
                if (totalCount > 0) {
                    val intent = Intent(activity, ChatRequestActivity::class.java)
                    CommonMethod.switchActivity(
                        activity,
                        intent
                    ) { result ->
                        if (result?.resultCode == Activity.RESULT_OK)
                            activity.dataBinder.bindFriendRequests()
                    }
                }
            }

            viewBind.btnInvite.id -> {
                val intent = Intent(activity, InviteActivity::class.java)
                intent.putExtra(
                    RouteConstants.ROUTE_CONSTANT_FROM,
                    InviteActivity.INVITE_TYPE_FRIEND
                )
                CommonMethod.switchActivity(activity, intent, false)
            }
        }
    }
}