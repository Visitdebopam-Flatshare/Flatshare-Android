package com.joinflatshare.ui.chat

import android.content.Intent
import android.os.Bundle
import com.joinflatshare.chat.ApplicationChatHandler
import com.joinflatshare.chat.SendBirdChannel
import com.joinflatshare.chat.SendBirdMessage
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.sendbird.android.channel.GroupChannel

open class ChatBaseActivity : BaseActivity() {
    lateinit var sendBirdChannel: SendBirdChannel
    lateinit var sendBirdMessage: SendBirdMessage
    lateinit var groupChannel: GroupChannel
    lateinit var sendBirdUser: SendBirdUser
    lateinit var sendBirdApiManager: SendBirdApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendBirdChannel = SendBirdChannel(this);
        sendBirdMessage = SendBirdMessage(this);
        sendBirdUser = SendBirdUser(this);
        sendBirdApiManager = SendBirdApiManager();
    }

    fun initialiseChat(callback: OnStringFetched) {
        ApplicationChatHandler().connectChat {
            if (it.equals("1"))
                callback.onFetched(it)
            else {
                AlertDialog.showAlert(
                    this, "Failed to initialise chat", "OK"
                ) { intent: Intent?, requestCode: Int ->  CommonMethod.finishActivity(this) }
            }
        }
    }
}