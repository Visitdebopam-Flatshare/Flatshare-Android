package com.joinflatshare.ui.chat.details

import android.view.View
import android.widget.TextView
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatDetailsBinding
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper
import com.stfalcon.imageviewer.loader.ImageLoader

class ChatDetailsTopbarHandler(
    private val activity: ChatDetailsActivity,
    private val viewBind: ActivityChatDetailsBinding
) {

    fun showChatTopbar(
        showBack: Boolean,
        showUserImage: Boolean
    ) {
        if (showBack) {
            viewBind.includeChatTopbar.imgTopbarChatBack.visibility = View.VISIBLE
            viewBind.includeChatTopbar.imgTopbarChatBack.setOnClickListener {
                CommonMethod.finishActivity(activity)
            }
        } else viewBind.includeChatTopbar.imgTopbarChatBack.visibility = View.GONE;
        if (showUserImage) {
            viewBind.includeChatTopbar.frameTopbarPhoto.visibility = View.VISIBLE;
            val link = activity.sendBirdChannel.getChannelDisplayImage(activity.groupChannel);
            val txt2 =
                viewBind.includeChatTopbar.frameTopbarPhoto.getChildAt(1) as TextView

            viewBind.includeChatTopbar.imgProfile.visibility = View.VISIBLE
            viewBind.includeChatTopbar.txtPhoto.visibility = View.GONE
            ImageHelper.loadImageWithException(
                activity,
                0,
                viewBind.includeChatTopbar.imgProfile,
                link
            ) {
                viewBind.includeChatTopbar.imgProfile.visibility = View.GONE
                viewBind.includeChatTopbar.txtPhoto.visibility = View.VISIBLE
                var name = viewBind.includeChatTopbar.txtChatTopbarUser.text.toString()
                var initial = "" + name[0]
                if (name.contains(" ")) {
                    name = name.substring(name.lastIndexOf(" ") + 1)
                    initial += name[0]
                }
                viewBind.includeChatTopbar.txtPhoto.text = initial
            }
            txt2.text = activity.getEmojiByUnicode(0x1F603);
        } else viewBind.includeChatTopbar.frameTopbarPhoto.visibility = View.GONE;
    }

    fun showTopBarIconOne(icon: Int) {
        if (icon == 0)
            viewBind.includeChatTopbar.llChatTopbarIconone.visibility = View.GONE;
        else {
            viewBind.includeChatTopbar.llChatTopbarIconone.visibility = View.VISIBLE;
            viewBind.includeChatTopbar.btnChatTopbarIconone.setImageResource(icon);
        }
    }

    fun showTopBarIconTwo(icon: Int) {
        if (icon == 0)
            viewBind.includeChatTopbar.llChatTopbarIcontwo.visibility = View.GONE;
        else {
            viewBind.includeChatTopbar.llChatTopbarIcontwo.visibility = View.VISIBLE;
            viewBind.includeChatTopbar.btnChatTopbarIcontwo.setImageResource(icon);
        }
    }

    fun showTopBarIconThree(icon: Int) {
        if (icon == 0)
            viewBind.includeChatTopbar.llChatTopbarIconthree.visibility = View.GONE;
        else {
            viewBind.includeChatTopbar.llChatTopbarIconthree.visibility = View.VISIBLE;
            viewBind.includeChatTopbar.btnChatTopbarIconthree.setImageResource(icon);
        }
    }
}