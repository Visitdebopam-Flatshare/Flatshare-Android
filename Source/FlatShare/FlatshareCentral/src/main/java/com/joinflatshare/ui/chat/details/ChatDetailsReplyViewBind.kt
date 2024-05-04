package com.joinflatshare.ui.chat.details

import android.view.View
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.ImageHelper
import com.sendbird.android.SendbirdChat.currentUser
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.FileMessage

class ChatDetailsReplyViewBind(
    private val activity: ChatDetailsActivity
) {
    init {
        activity.viewBind.btnChatReplyCross.setOnClickListener { hide() }
    }

    fun hide() {
        activity.viewBind.txtChatReply.text = ""
        activity.viewBind.rlChatReply.visibility = View.GONE
    }

    fun show(message: BaseMessage) {
        // Find sender name
        if (message.sender!!.userId == currentUser!!.userId)
            activity.viewBind.txtChatReplyName.setText("You") else activity.viewBind.txtChatReplyName.setText(
            message.sender!!.nickname
        )
        val metaData = Gson().fromJson(
            message.data,
            MessageMetaData::class.java
        )
        if (metaData != null) {
            if (metaData.messageType == SendBirdConstants.MESSAGE_TYPE_TEXT) {
                activity.viewBind.txtChatReply.visibility = View.VISIBLE
                activity.viewBind.imgChatReply.visibility = View.GONE
                var text = message.message
                if (text.length > 230) text = text.substring(0, 227) + "..."
                activity.viewBind.txtChatReply.setText(text)
            } else if (metaData.messageType == SendBirdConstants.MESSAGE_TYPE_LOCATION) {
                activity.viewBind.txtChatReply.visibility = View.GONE
                activity.viewBind.imgChatReply.visibility = View.VISIBLE
                activity.viewBind.imgChatReply.setImageResource(R.drawable.ic_location)
            } else if (metaData.messageType == SendBirdConstants.MESSAGE_TYPE_IMAGE) {
                val fm = message as FileMessage
                activity.viewBind.txtChatReply.visibility = View.GONE
                activity.viewBind.imgChatReply.visibility = View.VISIBLE
                ImageHelper.loadImage(activity, 0, activity.viewBind.imgChatReply, fm.url)
            } else if (metaData.messageType == SendBirdConstants.MESSAGE_TYPE_CONTACT) {
                activity.viewBind.txtChatReply.visibility = View.GONE
                activity.viewBind.imgChatReply.visibility = View.VISIBLE
                activity.viewBind.imgChatReply.setImageResource(R.drawable.ic_user_grey)
            }
        }
        activity.viewBind.rlChatReply.visibility = View.VISIBLE
        activity.viewBind.edtChatMessage.requestFocusFromTouch()
        activity.viewBind.edtChatMessage.requestFocus()
        CommonMethods.showSoftKeyboard(activity, activity.viewBind.edtChatMessage)
    }

    fun isVisible(): Boolean {
        return activity.viewBind.rlChatReply.visibility == View.VISIBLE
    }

}