package com.joinflatshare.chat

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.firestore.DbSendbirdRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.helper.CommonMethod
import com.sendbird.android.LogLevel
import com.sendbird.android.SendbirdChat
import com.sendbird.android.SendbirdChat.getTotalUnreadChannelCount
import com.sendbird.android.SendbirdChat.init
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.ChannelType
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.CountHandler
import com.sendbird.android.handler.GroupChannelHandler
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.params.GroupChannelTotalUnreadChannelCountParams
import com.sendbird.android.params.InitParams

class ApplicationChatHandler {
    private var CONNECTION_RETRY = 3
    private val APPLICATION_CONNECTION_HANDLER_ID = "APPLICATION_CHANNEL_HANDLER_ID"
    private val APPLICATION_CHANNEL_HANDLER_ID = "APPLICATION_CHANNEL_HANDLER_ID"
    private val TAG = ApplicationChatHandler::class.java.simpleName

    fun initialise(callback: OnStringFetched?) {
        if (AppConstants.isSendbirdLive) {
            if (SendBirdConstants.SENDBIRD_APPID.isEmpty()) {
                getChatKeys(callback)
            } else initialiseSendbird(callback)
        } else onConnectionFailure(callback)
    }


    private fun getChatKeys(callback: OnStringFetched?) {
        CommonMethod.makeLog(TAG, "Retrieving chat keys")
        DbSendbirdRetriever().getSendbirdKeys { text: String ->
            if (text == "1") {
                initialiseSendbird(callback)
            } else onConnectionFailure(callback)
        }
    }

    private fun initialiseSendbird(callback: OnStringFetched?) {
        // Initialize a SendbirdChat instance to use APIs in your app.
        CommonMethod.makeLog(TAG, "Initializing Sendbird")
        val params = InitParams(
            SendBirdConstants.SENDBIRD_APPID,
            FlatShareApplication.instance,
            true
        )
        params.logLevel = LogLevel.ERROR
        init(params, object : InitResultHandler {
            override fun onMigrationStarted() {}
            override fun onInitFailed(e: SendbirdException) {
                CommonMethod.makeLog(TAG, "Sendbird init failed")
                onConnectionFailure(callback)
            }

            override fun onInitSucceed() {
                CommonMethod.makeLog(TAG, "Sendbird init success")
                connectChat(callback)
            }
        })
    }

    fun connectChat(callback: OnStringFetched?) {
        if (SendBirdConnectionManager.isSendBirdConnected) {
            onConnectionSuccess(callback)
        } else {
            if (CONNECTION_RETRY > 0) {
                SendBirdConnectionManager.connect { text ->
                    if (text.equals("0"))
                        onConnectionFailure(callback)
                    else {
                        SendbirdChat.removeConnectionHandler(APPLICATION_CONNECTION_HANDLER_ID)
                        SendBirdConnectionManager.addConnectionManagementHandler(
                            APPLICATION_CONNECTION_HANDLER_ID
                        ) {
                            if (SendBirdConnectionManager.isSendBirdConnected) {
                                onConnectionSuccess(callback)
                            } else {
                                CONNECTION_RETRY--
                                connectChat(callback)
                            }
                        }
                    }
                }
            } else {
                onConnectionFailure(callback)
            }
        }
    }

    private fun setHandler() {
        SendbirdChat.addChannelHandler(
            APPLICATION_CHANNEL_HANDLER_ID,
            object : GroupChannelHandler() {
                override fun onMessageReceived(channel: BaseChannel, message: BaseMessage) {
                    getUnreadChannelCount()
                }

                override fun onMessageDeleted(channel: BaseChannel, msgId: Long) {
                    super.onMessageDeleted(channel, msgId)
                    getUnreadChannelCount()
                }

                override fun onChannelDeleted(
                    channelUrl: String,
                    channelType: ChannelType
                ) {
                    super.onChannelDeleted(channelUrl, channelType)
                    getUnreadChannelCount()
                }
            }
        )
    }

    companion object {
        fun getUnreadChannelCount() {
            val params = GroupChannelTotalUnreadChannelCountParams()
            getTotalUnreadChannelCount(params,
                CountHandler { unreadCount, e ->
                    val old = SendBirdConstants.unreadChannelCount
                    SendBirdConstants.unreadChannelCount = unreadCount
                    if (old != unreadCount) {
                        LocalBroadcastManager.getInstance(FlatShareApplication.instance)
                            .sendBroadcast(Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_CHAT_COUNT))
                    }
                })
        }
    }

    private fun onConnectionSuccess(callback: OnStringFetched?) {
        if (!SendBirdConnectionManager.isSendBirdConnected) {
            CommonMethod.makeLog(TAG, "Sendbird Connection Success")
            SendBirdConnectionManager.isSendBirdConnected = true
//        getUnreadChannelCount()
            setHandler()
        }
        callback?.onFetched("1")
    }

    private fun onConnectionFailure(callback: OnStringFetched?) {
        CommonMethod.makeLog(TAG, "Sendbird Connection Failure")
        AppConstants.isSendbirdLive = false
        if (SendBirdConnectionManager.isSendBirdConnected) {
            SendbirdChat.removeAllConnectionHandlers()
            SendbirdChat.removeAllChannelHandlers()
            SendbirdChat.disconnect(null)
        }
        SendBirdConnectionManager.isSendBirdConnected = false
        callback?.onFetched("0")
    }
}