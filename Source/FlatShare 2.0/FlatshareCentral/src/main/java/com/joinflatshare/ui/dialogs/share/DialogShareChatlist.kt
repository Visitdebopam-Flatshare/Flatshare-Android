package com.joinflatshare.ui.dialogs.share

import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogShareChatlistBinding
import com.joinflatshare.FlatshareCentral.databinding.IncludePopupHeaderWhiteBinding
import com.joinflatshare.chat.SendBirdChannel
import com.joinflatshare.chat.SendBirdConnectionManager
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.sendbird.android.channel.GroupChannel

class DialogShareChatlist(
    private val activity: BaseActivity,
    header: String
) {
    var viewBind: DialogShareChatlistBinding

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = IncludePopupHeaderWhiteBinding.inflate(activity.layoutInflater)
        viewBind = DialogShareChatlistBinding.inflate(activity.layoutInflater)
        holder.llPopupHolder.addView(viewBind.root)
        builder.setView(holder.root)
        builder.setCancelable(true)
        val dialog: AlertDialog = builder.create()

        // Header
        holder.txtDialogHeader.text = header
        holder.imgCross.setOnClickListener {
            CommonMethods.dismissDialog(activity, dialog)
        }

        // content
        holder.viewBg.setOnClickListener {}
        getChatlist(dialog)
    }

    private fun getChatlist(dialog: AlertDialog) {
        if (SendBirdConnectionManager.isSendBirdConnected) {
            val sendBirdChannel = SendBirdChannel(activity)
            sendBirdChannel.getChannelList(object : OnChannelsFetchedListener {
                override fun onFetched(allChannels: MutableList<GroupChannel>?) {
                    if (allChannels.isNullOrEmpty()) {
                        CommonMethod.makeToast(
                            "You have not initiated chat with any of your friends"
                        )
                    } else {
                        CommonMethods.showDialog(activity, dialog)
                    }
                }

                override fun onFetched(allChannels: ArrayList<ChannelsItem>?) {

                }

            })
        } else CommonMethod.makeToast( "Chat not connected")
    }
}