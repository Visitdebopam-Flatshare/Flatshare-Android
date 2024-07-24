package com.joinflatshare.ui.chat.details

import android.view.View
import android.widget.TextView
import com.joinflatshare.FlatshareCentral.databinding.ActivityChatDetailsBinding
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper

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
        } else viewBind.includeChatTopbar.imgTopbarChatBack.visibility = View.GONE
        if (showUserImage) {
            viewBind.includeChatTopbar.frameTopbarPhoto.visibility = View.VISIBLE
            val id = activity.sendBirdChannel.getChannelDisplayUserId(activity.groupChannel)
            activity.baseApiController.getUser(false, id, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    ImageHelper.loadProfileImage(
                        activity,
                        viewBind.includeChatTopbar.imgProfile,
                        viewBind.includeChatTopbar.txtPhoto,
                        resp?.data
                    )
                }
            })
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