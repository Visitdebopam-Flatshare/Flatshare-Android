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
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onMessageDeleted(channel: BaseChannel, msgId: Long) {
                super.onMessageDeleted(channel, msgId)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onMessageUpdated(channel: BaseChannel, message: BaseMessage) {
                super.onMessageUpdated(channel, message)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onReadStatusUpdated(channel: GroupChannel) {
                super.onReadStatusUpdated(channel)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onTypingStatusUpdated(channel: GroupChannel) {
                super.onTypingStatusUpdated(channel)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onDeliveryStatusUpdated(channel: GroupChannel) {
                super.onDeliveryStatusUpdated(channel)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }

            override fun onChannelDeleted(
                channelUrl: String,
                channelType: ChannelType
            ) {
                super.onChannelDeleted(channelUrl, channelType)
                if (activity.adapter != null) activity.adapter!!.notifyDataSetChanged()
            }
        }
        )
    }
}