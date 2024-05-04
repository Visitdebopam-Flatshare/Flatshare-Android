package com.joinflatshare.ui.dialogs

import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.DialogMatchBinding
import com.joinflatshare.chat.SendBirdFlatmateChannel
import com.joinflatshare.chat.SendBirdUserChannel
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.chat.list.ChatListActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.ImageHelper

class DialogConnection(
    private val activity: BaseActivity,
    private val user: User?,
    private val flatId: String?,
    private val fhtUser: User?,
    private val connectionType: String
) {
    var channelUrl = ""

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = DialogMatchBinding.inflate(activity.layoutInflater)
        builder.setView(holder.root)
        builder.setCancelable(false)
        val dialog: AlertDialog = builder.create()

        // Set button texts
        val button = holder.llMatchContinue.getChildAt(1) as TextView
        button.text = "See Other Requests"
        // Set icon
        val img = holder.llMatchContinue.getChildAt(0) as ImageView
        img.setImageResource(R.drawable.ic_wave_blue)

        // content
        holder.viewBg.setOnClickListener {}

        // Image
        ImageHelper.loadImage(
            activity, R.drawable.ic_user, holder.imgProfile1,
            ImageHelper.getProfileImageWithAws(user)
        )
        when (connectionType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F -> {
                ImageHelper.loadImage(
                    activity, R.drawable.ic_flat_bg, holder.imgProfile2,
                    ImageHelper.getFlatDpWithAws(flatId)
                )
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM,
            "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                ImageHelper.loadImage(
                    activity, R.drawable.ic_user, holder.imgProfile2,
                    ImageHelper.getProfileImageWithAws(fhtUser)
                )
            }
        }

        holder.txtMatchEmoji.text = activity.getEmojiByUnicode(0x1F64C)
        holder.txtMatchConnection.text =
            "You've made a new connection " + activity.getEmojiByUnicode(0x1F91D)

        holder.llMatchContinue.setOnClickListener {
            CommonMethods.dismissDialog(activity, dialog)
        }

        holder.llMatchTextnow.setOnClickListener {
            CommonMethods.dismissDialog(activity, dialog)
            val intent = Intent(activity, ChatListActivity::class.java)
            CommonMethod.switchActivity(activity, intent, false)
            /*createChannel()
            if (channelUrl.isBlank())
                CommonMethod.makeToast("Could not create channel")
            else {
                val intent = Intent(activity, ChatListActivity::class.java)
//                intent.putExtra("channel", channelUrl)
                CommonMethods.switchActivity(activity, intent, false)
            }*/
        }
        showLottie(holder)
        CommonMethods.showDialog(activity, dialog)
    }

    private fun showLottie(holder: DialogMatchBinding) {
        DialogLottieViewer.loadAnimation(
            holder.lottieMatch,
            R.raw.lottie_match, null
        )
    }

    private fun createChannel() {
        when (connectionType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U -> {
                val channel = SendBirdFlatmateChannel(activity)
                channel.addFlatMate(
                    flatId!!, user?.id!!,
                ) { text -> channelUrl = text!! }
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT -> {
                val channel = SendBirdUserChannel(activity)
                channel.joinUserChannel(
                    fhtUser?.id!!,
                    SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
                ) { text -> channelUrl = text!! }
            }
        }
    }
}