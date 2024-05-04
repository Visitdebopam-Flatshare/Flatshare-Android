package com.joinflatshare.ui.chat.details

import android.os.Handler
import android.os.Looper
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData
import com.joinflatshare.constants.SendBirdConstants
import com.sendbird.android.message.UserMessage
import com.sendbird.android.params.UserMessageCreateParams

class ChatDetailsSendListener(
    private val activity: ChatDetailsActivity,
    private val dataBind: ChatDetailsViewBind
) {
    fun sendMessage() {
        // Send message
        if (activity.viewBind.edtChatMessage.text?.isEmpty() == false) {
            // Text message
            sendTextMessage()
        }
        if (activity.groupChannel.isTyping) activity.groupChannel.endTyping()
    }

    private fun sendTextMessage() {
        val message: String = activity.viewBind.edtChatMessage.getText().toString().trim()
        if (message.isNotEmpty()) {
            // Sending a text message
            val metaData = MessageMetaData()
            metaData.messageType = SendBirdConstants.MESSAGE_TYPE_TEXT
            val params = UserMessageCreateParams()
            params.message = message
            if (dataBind.adapter.selectedMessage != null) {
                // Reply message
                params.parentMessageId = dataBind.adapter.selectedMessage.messageId
                metaData.previousMessageMetaData =
                    dataBind.mediaBottomSheet.handleReplyParentMessage()
            }
            params.data = metaData.getJson(metaData)
            activity.sendBirdMessage.sendTextMessage(
                activity.groupChannel, params
            ) { userMessage: UserMessage? ->
                activity.viewBind.edtChatMessage.setText("")
                dataBind.replyViewBind.hide()
                if (dataBind.adapter.selectedMessage != null) {
                    dataBind.adapter.selectedMessage = null
                    activity.topbarHandler.showTopBarIconTwo(0)
                    activity.topbarHandler.showTopBarIconThree(0)
                    activity.topbarHandler.showTopBarIconOne(R.drawable.ic_threedots)
                }
                activity.messageList.add(0, userMessage!!)
                dataBind.adapter.notifyDataSetChanged()
                dataBind.alterChatView()
                Handler(Looper.getMainLooper())
                    .postDelayed({
                        activity.viewBind.rvMessageList.smoothScrollToPosition(
                            0
                        )
                    }, 500)
            }
        }
    }
}