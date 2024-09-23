package com.joinflatshare.ui.chat.list

import android.content.Intent
import android.text.TextUtils
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
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.AdminMessage
import com.sendbird.android.message.BaseMessage
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
            var name = ""
            if (channel.url.startsWith("FLAT")) {
                name = channel.name
            } else if (channel.url.startsWith("USER")) {
                val displayUserId = activity.sendBirdChannel.getChannelDisplayUserId(channel)
                if (TextUtils.isEmpty(displayUserId))
                    name = "No Name"
                else
                    for (member in channel.members) {
                        if (member.userId == displayUserId) {
                            if (TextUtils.isEmpty(member.nickname))
                                name = "No Name"
                            else
                                name = member.nickname
                            break
                        }
                    }
            }
            holder.txtChatName.text = name

            val link = activity.sendBirdChannel.getChannelDisplayImage(channel)
            ImageHelper.loadProfileImage(
                activity,
                holder.imgChat,
                holder.txtPhoto,
                link, name
            )

            // Typing indicator
            if (channel.isTyping) {
                holder.txtChatTyping.visibility = View.VISIBLE
                holder.txtChatLastMessage.visibility = View.GONE
            } else {
                holder.txtChatTyping.visibility = View.GONE
                holder.txtChatLastMessage.visibility = View.VISIBLE
            }

            // Last Message
            val lastMessage = channel.lastMessage
            if (lastMessage == null) {
                holder.txtChatLastMessage.visibility = View.GONE
                holder.txtChatTyping.visibility = View.GONE
                holder.txtChatLastMessageTime.visibility = View.GONE
            } else {
                // Display information about the most recently sent message in the channel.
                val messageTime = DateUtils.formatDateTime(lastMessage.createdAt).toString()
                holder.txtChatLastMessageTime.visibility = View.VISIBLE
                holder.txtChatLastMessageTime.text = messageTime


                if (lastMessage.sender?.userId == SendbirdChat.currentUser?.userId) {
                    // Last message is mine
                    holder.txtChatLastMessage.text = getLastMessage(lastMessage)
                    holder.txtChatLastMessage.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.grey4
                        )
                    )
                } else {
                    if (channel.unreadMessageCount > 0) {
                        if (channel.unreadMessageCount == 1) {
                            holder.txtChatLastMessage.text = getLastMessage(lastMessage)
                            holder.txtChatLastMessage.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.blue_dark
                                )
                            )
                        } else {
                            val count = if (channel.unreadMessageCount <= 4)
                                "${channel.unreadMessageCount} messages" else "4+ New messages"
                            holder.txtChatLastMessage.text = count
                            holder.txtChatLastMessage.setTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.grey4
                                )
                            )
                        }
                    } else {
                        holder.txtChatLastMessage.text = getLastMessage(lastMessage)
                        holder.txtChatLastMessage.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.grey4
                            )
                        )
                    }
                }
            }

            holder.llChatlist.setOnClickListener {
                // Move to chat details screen
                val intent =
                    Intent(activity, ChatDetailsActivity::class.java)
                intent.putExtra("channel", channel.url)
                CommonMethod.switchActivity(activity, intent, false)
            }
        }

        private fun getLastMessage(lastMessage: BaseMessage): String {
            var htmlMessage = ""
            val metaData: MessageMetaData? = Gson().fromJson(
                lastMessage.data,
                MessageMetaData::class.java
            )
            if (metaData?.messageType != null)
                when (metaData.messageType) {
                    SendBirdConstants.MESSAGE_TYPE_TEXT -> if (lastMessage is UserMessage || lastMessage is AdminMessage) {
                        htmlMessage = lastMessage.message
                    }

                    SendBirdConstants.MESSAGE_TYPE_AUDIO -> htmlMessage += "Sent an audio"
                    SendBirdConstants.MESSAGE_TYPE_CONTACT -> htmlMessage += "Sent a contact"
                    SendBirdConstants.MESSAGE_TYPE_DOCUMENT -> htmlMessage += "Sent a document"
                    SendBirdConstants.MESSAGE_TYPE_IMAGE -> htmlMessage += "Sent an image"
                    SendBirdConstants.MESSAGE_TYPE_LOCATION -> htmlMessage += "Sent location"
                    SendBirdConstants.MESSAGE_TYPE_VIDEO -> htmlMessage += "Sent a video"
                }
            return htmlMessage
        }
    }

}