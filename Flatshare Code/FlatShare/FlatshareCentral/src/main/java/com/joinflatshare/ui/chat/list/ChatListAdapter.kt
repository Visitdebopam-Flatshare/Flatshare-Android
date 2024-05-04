package com.joinflatshare.ui.chat.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemChatListBinding
import com.joinflatshare.chat.metadata.MessageMetaData
import com.joinflatshare.chat.utils.DateUtils
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.sendbird.android.SendbirdChat.currentUser
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.AdminMessage
import com.sendbird.android.message.SendingStatus
import com.sendbird.android.message.UserMessage

class ChatListAdapter(
    private val activity: ChatListActivity,
    private val items: ArrayList<GroupChannel>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val activity: ChatListActivity,
        private val holder: ItemChatListBinding
    ) :
        RecyclerView.ViewHolder(holder.root) {
        fun bind(
            position: Int,
            adapter: ChatListAdapter
        ) {

            val channel: GroupChannel = adapter.items[position]
            if (channel.url.startsWith("FLAT")) {
                holder.txtChatName.text = channel.name
            } else if (channel.url.startsWith("USER")) {
                val displayUserId = activity.sendBirdChannel.getChannelDisplayUserId(channel)
                for (member in channel.members) {
                    if (member.userId == displayUserId) {
                        holder.txtChatName.text = member.nickname
                        break
                    }
                }
            }

            // Typing indicator
            if (channel.isTyping) {
                holder.txtChatTyping.visibility = View.VISIBLE
                holder.llChatMessage.visibility = View.GONE
            } else {
                holder.txtChatTyping.visibility = View.GONE
                holder.llChatMessage.visibility = View.VISIBLE
            }

            // Unread Count
            if (channel.unreadMessageCount > 0) {
                holder.frameCountChat.visibility = View.VISIBLE
                holder.txtUnreadCount.text = "" + channel.unreadMessageCount
            } else holder.frameCountChat.visibility = View.INVISIBLE

            // Image
            val link = activity.sendBirdChannel.getChannelDisplayImage(channel)
            ImageHelper.loadImage(
                activity,
                if (channel.url.startsWith("USER")) R.drawable.ic_user else if (channel.url.startsWith(
                        "FLATMATE_"
                    )
                ) R.drawable.ic_flat_chat else R.drawable.ic_flat_bg,
                holder.imgChat,
                link
            )
            // Image Border
            when (channel.customType) {
                SendBirdConstants.CHANNEL_TYPE_CONNECTION_CASUAL,
                SendBirdConstants.CHANNEL_TYPE_MATCH_CASUAL -> {
                    holder.imgChat.borderWidth = 8f
                    holder.imgChat.borderColor =
                        ContextCompat.getColor(activity, R.color.color_date_casual)
                }

                SendBirdConstants.CHANNEL_TYPE_CONNECTION_LONG_TERM,
                SendBirdConstants.CHANNEL_TYPE_MATCH_LONG_TERM -> {
                    holder.imgChat.borderWidth = 8f
                    holder.imgChat.borderColor =
                        ContextCompat.getColor(activity, R.color.color_date_long_term)
                }

                SendBirdConstants.CHANNEL_TYPE_CONNECTION_ACTIVITY_PARTNERS,
                SendBirdConstants.CHANNEL_TYPE_MATCH_ACTIVITY_PARTNERS -> {
                    holder.imgChat.borderWidth = 8f
                    holder.imgChat.borderColor =
                        ContextCompat.getColor(activity, R.color.color_date_activity_partners)
                }

                else -> {
                    holder.imgChat.borderWidth = 0f
                }
            }

            // Last Message
            val lastMessage = channel.lastMessage
            if (lastMessage != null) {
                holder.llChatMessage.visibility = View.VISIBLE

                // Last message sent status
                if (lastMessage.sender?.userId == currentUser?.userId) {
                    // I have sent the last message
                    holder.imgDelivered.visibility = View.VISIBLE
                    if (lastMessage.sendingStatus.value == SendingStatus.SUCCEEDED.value) {
                        // Message sent. Check for delivery report
                        holder.imgDelivered.setImageResource(R.drawable.ic_chat_delivered_black)
                    } else holder.imgDelivered.setImageResource(R.drawable.ic_chat_sent)
                } else {
                    // Some one has sent me the last message
                    holder.imgDelivered.visibility = View.GONE
                }

                // Display information about the most recently sent message in the channel.
                val messageTime = DateUtils.formatDateTime(lastMessage.createdAt).toString()
                holder.txtChatLastMessageTime.setText(messageTime)

                // Bind last message text according to the type of message. Specifically, if
                // the last message is a File Message, there must be special formatting.
                var htmlMessage = ""
                val metaData = Gson().fromJson(
                    lastMessage.data,
                    MessageMetaData::class.java
                )
                if (metaData.messageType != null)
                    when (metaData.messageType) {
                        SendBirdConstants.MESSAGE_TYPE_TEXT -> if (lastMessage is UserMessage || lastMessage is AdminMessage) {
                            htmlMessage = lastMessage.message
                            if (htmlMessage.length > 21) htmlMessage =
                                htmlMessage.substring(0, 18) + "..."
                        }

                        SendBirdConstants.MESSAGE_TYPE_AUDIO -> htmlMessage += "Sent an audio"
                        SendBirdConstants.MESSAGE_TYPE_CONTACT -> htmlMessage += "Sent a contact"
                        SendBirdConstants.MESSAGE_TYPE_DOCUMENT -> htmlMessage += "Sent a document"
                        SendBirdConstants.MESSAGE_TYPE_IMAGE -> htmlMessage += "Sent an image"
                        SendBirdConstants.MESSAGE_TYPE_LOCATION -> htmlMessage += "Sent location"
                        SendBirdConstants.MESSAGE_TYPE_VIDEO -> htmlMessage += "Sent a video"
                    }
                if (htmlMessage.length > 25) htmlMessage = htmlMessage.substring(0, 22) + "..."
                holder.txtChatLastMessage.setText(htmlMessage)
            } else {
                holder.llChatMessage.visibility = View.GONE
            }

            holder.llChatlist.setOnClickListener {
                // Move to chat details screen
                val intent =
                    Intent(activity, ChatDetailsActivity::class.java)
                intent.putExtra("channel", channel.url)
                CommonMethod.switchActivity(activity, intent, false)
            }
        }
    }

}