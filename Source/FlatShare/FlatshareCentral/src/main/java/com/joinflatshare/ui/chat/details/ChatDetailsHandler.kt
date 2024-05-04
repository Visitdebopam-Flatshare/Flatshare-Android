package com.joinflatshare.ui.chat.details

import android.os.Handler
import android.os.Looper
import com.joinflatshare.utils.helper.CommonMethod
import com.sendbird.android.SendbirdChat.addChannelHandler
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.ChannelType
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.handler.GroupChannelHandler
import com.sendbird.android.message.BaseMessage

class ChatDetailsHandler(private val activity: ChatDetailsActivity) {
    init {
        setHandler()
    }

    private fun setHandler() {
        addChannelHandler(activity.CHANNEL_HANDLER_ID, object : GroupChannelHandler() {
            override fun onMessageDeleted(channel: BaseChannel, msgId: Long) {
                super.onMessageDeleted(channel, msgId)
                if (channel.url == activity.groupChannel.url) {
                    for (i in activity.messageList.indices) {
                        val msg = activity.messageList[i]
                        if (msg.messageId == msgId) {
                            activity.messageList.remove(msg)
                            activity.dataBind.adapter.notifyItemRemoved(i)
                            break
                        }
                    }
                }
            }

            override fun onMessageUpdated(channel: BaseChannel, message: BaseMessage) {
                super.onMessageUpdated(channel, message)
                if (channel.url == activity.groupChannel.url) {
                    var baseMessage: BaseMessage
                    for (index in activity.messageList.indices.reversed()) {
                        baseMessage = activity.messageList[index]
                        if (message.messageId == baseMessage.messageId) {
                            activity.messageList.removeAt(index)
                            activity.messageList.add(index, message)
                            activity.dataBind.adapter.notifyItemChanged(index)
                            break
                        }
                    }
                }
            }

            override fun onDeliveryStatusUpdated(channel: GroupChannel) {
                super.onDeliveryStatusUpdated(channel)
                if (channel.url == activity.groupChannel.url) {
                    activity.dataBind.adapter.notifyDataSetChanged()
                }
            }

            override fun onReadStatusUpdated(channel: GroupChannel) {
                super.onReadStatusUpdated(channel)
                if (channel.url == activity.groupChannel.url) {
                    activity.dataBind.adapter.notifyDataSetChanged()
                }
            }

            override fun onTypingStatusUpdated(channel: GroupChannel) {
                super.onTypingStatusUpdated(channel)
                if (channel.url == activity.groupChannel.url) {
                    val typing = activity.sendBirdChannel.getTypingUser(channel)
                    if (typing.isEmpty()) {
                        // Show status
                        activity.sendBirdChannel.getOnlineStatus(
                            channel
                        ) { text: String? ->
                            activity.viewBind.includeChatTopbar.txtChatTopbarStatus.text = text
                        }
                    } else {
                        activity.viewBind.includeChatTopbar.txtChatTopbarStatus.text = typing
                    }
                }
            }

            override fun onMessageReceived(baseChannel: BaseChannel, baseMessage: BaseMessage) {
                if (baseChannel.url == activity.groupChannel.url) {
                    activity.messageList.add(0, baseMessage)
                    activity.groupChannel.markAsRead(null)
                    activity.dataBind.alterChatView()
                    activity.dataBind.adapter.notifyItemInserted(0)
                    Handler(Looper.getMainLooper()).postDelayed({
                        activity.viewBind.rvMessageList.smoothScrollToPosition(
                            0
                        )
                    }, 500)
                }
            }

            override fun onChannelDeleted(
                channelUrl: String,
                channelType: ChannelType
            ) {
                super.onChannelDeleted(channelUrl, channelType)
                CommonMethod.finishActivity(activity)
            }
        })
    }
}