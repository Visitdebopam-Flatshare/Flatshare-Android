package com.joinflatshare.ui.chat.details

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatDetailsBinding
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse
import com.joinflatshare.constants.SendBirdConstants.USER_STATUS_REFRESH_DELAY
import com.joinflatshare.ui.chat.details.mediaholder.ChatDetailsMediaBottomSheet
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.sendbird.android.user.User
import com.sendbird.android.user.User.ConnectionStatus
import java.util.Timer
import java.util.TimerTask

class ChatDetailsViewBind(
    private val activity: ChatDetailsActivity,
    private val viewBind: ActivityChatDetailsBinding
) {
    var mediaBottomSheet: ChatDetailsMediaBottomSheet

    @kotlin.jvm.JvmField
    var replyViewBind: ChatDetailsReplyViewBind
    var sendListener: ChatDetailsSendListener
    lateinit var adapter: ChatDetailsAdapter
    var timer: Timer? = null

    init {
        bind()
        mediaBottomSheet = ChatDetailsMediaBottomSheet(activity)
        replyViewBind = ChatDetailsReplyViewBind(activity)
        sendListener = ChatDetailsSendListener(activity, this)
    }

    fun bind() {
        viewBind.rvMessageList.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            true
        )
        adapter = ChatDetailsAdapter(activity, activity.messageList)
        viewBind.rvMessageList.adapter = adapter
    }

    fun alterChatView() {
        if (activity.messageList.size == 0) {
            viewBind.rvMessageList.visibility = View.GONE
            viewBind.llNochat.visibility = View.VISIBLE
        } else {
            viewBind.rvMessageList.visibility = View.VISIBLE
            viewBind.llNochat.visibility = View.GONE
        }
    }

    fun setData() {
        if (activity.groupChannel.url.startsWith("FLAT")) {
            // It is a flat group channel
            viewBind.includeChatTopbar.txtChatTopbarUser.text = activity.groupChannel.name
            viewBind.includeChatTopbar.txtChatTopbarStatus.visibility = View.GONE
        } else if (activity.groupChannel.url.startsWith("USER_")) {
            // It is a one to one channel
            val channelDisplayUserId =
                activity.sendBirdChannel.getChannelDisplayUserId(activity.groupChannel)
            activity.sendBirdUser.getUserDetails(
                channelDisplayUserId
            ) { user: ModelChatUserResponse? ->
                if (user != null) {
                    activity.chatUser = user
                    // Check blocked status
                    val userIDs =
                        ArrayList<String>()
                    userIDs.add(activity.sendBirdChannel.getChannelDisplayUserId(activity.groupChannel))
                    activity.sendBirdUser.getBlockedUserList(
                        userIDs
                    ) { users: List<User?> ->
                        if (users.isEmpty()) {
                            viewBind.includeChatTopbar.txtChatTopbarUser.text = user.nickname
                            viewBind.rlChatBottom.visibility = View.VISIBLE
                            setRefreshTimer()
                        } else {
                            CommonMethods.makeToast(
                                "You have blocked " + user.nickname
                            )
                            viewBind.includeChatTopbar.txtChatTopbarStatus.text = "Blocked"
                            viewBind.includeChatTopbar.txtChatTopbarUser.text = user.nickname
                            viewBind.rlChatBottom.setVisibility(View.INVISIBLE)
                        }
                    }
                } else viewBind.includeChatTopbar.txtChatTopbarStatus.text = "Offline"
            }
            if (!channelDisplayUserId.isEmpty()) for (member in activity.groupChannel.members) {
                if (member.userId == channelDisplayUserId) {
                    if (member.connectionStatus == ConnectionStatus.ONLINE) viewBind.includeChatTopbar.txtChatTopbarStatus.text =
                        "Online" else viewBind.includeChatTopbar.txtChatTopbarStatus.text =
                        "Offline"
                    break
                }
            }
        }
    }

    fun setRefreshTimer() {
        if (timer != null) timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    activity.sendBirdChannel.getOnlineStatus(
                        activity.groupChannel
                    ) { text: String? ->
                        CommonMethod.makeLog("Sendbird Online Status", text)
                        viewBind.includeChatTopbar.txtChatTopbarStatus.text = text
                        setRefreshTimer()
                    }
                }
            }
        }, USER_STATUS_REFRESH_DELAY)
    }
}