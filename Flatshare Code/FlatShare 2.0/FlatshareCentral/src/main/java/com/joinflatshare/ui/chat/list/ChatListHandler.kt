package com.joinflatshare.ui.chat.list

import com.sendbird.android.SendbirdChat.addChannelHandler
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.ChannelType
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.handler.GroupChannelHandler
import com.sendbird.android.message.BaseMessage

class ChatListHandler(private val activity: ChatListActivity) {
    init {
        setHandler()
    }

    private fun setHandler() {
        addChannelHandler(activity.CHANNEL_HANDLER_ID, object : GroupChannelHandler() {
            override fun onMessageReceived(channel: BaseChannel, message: BaseMessage) {
                notifyAdapter(channel)
            }

            override fun onMessageDeleted(channel: BaseChannel, msgId: Long) {
                super.onMessageDeleted(channel, msgId)
                notifyAdapter(channel)
            }

            override fun onMessageUpdated(channel: BaseChannel, message: BaseMessage) {
                super.onMessageUpdated(channel, message)
                notifyAdapter(channel)
            }

            override fun onReadStatusUpdated(channel: GroupChannel) {
                super.onReadStatusUpdated(channel)
                notifyAdapter(channel)
            }

            override fun onTypingStatusUpdated(channel: GroupChannel) {
                super.onTypingStatusUpdated(channel)
                notifyAdapter(channel)
            }

            override fun onDeliveryStatusUpdated(channel: GroupChannel) {
                super.onDeliveryStatusUpdated(channel)
                notifyAdapter(channel)
            }

            override fun onChannelDeleted(
                channelUrl: String,
                channelType: ChannelType
            ) {
                super.onChannelDeleted(channelUrl, channelType)
                if (activity.adapter != null) {
                    for (position in activity.allGroupChannelList.indices) {
                        if (activity.allGroupChannelList[position].url == channelUrl) {
                            activity.adapter!!.notifyItemRemoved(position)
                            break
                        }
                    }
                }
            }
        }
        )
    }

    private fun notifyAdapter(channel: BaseChannel) {
        if (activity.adapter != null) {
            for (position in activity.allGroupChannelList.indices) {
                if (activity.allGroupChannelList[position].url == channel.url) {
                    activity.adapter!!.notifyItemChanged(position)
                    break
                }
            }
        }
    }
}