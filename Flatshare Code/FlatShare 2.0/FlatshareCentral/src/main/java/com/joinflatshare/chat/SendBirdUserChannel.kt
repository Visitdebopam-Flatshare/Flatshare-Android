package com.joinflatshare.chat

import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.chatInterfaces.OnChannelCreatedListener
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener
import com.joinflatshare.chat.metadata.UserChannelMetadata
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.params.GroupChannelCreateParams

class SendBirdUserChannel(
    private val activity: BaseActivity,
) : SendBirdChannel(activity) {
    val loggedinUser = FlatShareApplication.getDbInstance().userDao().getUser()
    private fun createUserChannel(
        chatUser: User,
        type: String,
        callback: OnChannelCreatedListener
    ) {
        val groupChannelUrl = getChannelName(loggedinUser?.id!!, chatUser.id)

        // User IDs
        val groupUserIds = ArrayList<String>()
        groupUserIds.add(loggedinUser.id)
        groupUserIds.add(chatUser.id)

        // MetaData
        val metadata = UserChannelMetadata()
        metadata.userMap[loggedinUser.id] = loggedinUser
        metadata.userMap[chatUser.id] = chatUser
        val time = System.currentTimeMillis()
        metadata.createdTime = time
        metadata.updatedTime = time

        val params = GroupChannelCreateParams().apply {
            isPublic = true
            isEphemeral = false
            isDistinct = false
            customType = type
            isSuper = false
            channelUrl = groupChannelUrl
            data = Gson().toJson(metadata)
            userIds = groupUserIds
            name = groupChannelUrl
        }

        GroupChannel.createChannel(params) { groupChannel: GroupChannel?, e: SendbirdException? ->
            if (e != null) {
                CommonMethod.makeToast("Failed to create a channel")
                callback.onCreated(null)
            }
            callback.onCreated(groupChannel)
        }
    }

    private fun getChannelName(num1: String, num2: String): String {
        var channelName = ""
        val number1 = num1.toLong()
        val number2 = num2.toLong()
        channelName = "USER_" +
                if (number1 < number2) "" + number1 + "_" + number2 else "" + number2 + "_" + number1
        return channelName
    }

    private fun getFHTChannelName(num1: String, num2: String): String {
        var channelName = ""
        val number1 = num1.toLong()
        val number2 = num2.toLong()
        channelName = "USER_FHT_" +
                if (number1 < number2) "" + number1 + "_" + number2 else "" + number2 + "_" + number1
        return channelName
    }

    fun joinUserChannel(userId: String, type: String, callback: OnStringFetched?) {
        val channelUrl: String
        if (type == SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
            || type == SendBirdConstants.CHANNEL_TYPE_MATCH_FHT
        )
            channelUrl = getFHTChannelName(userId, loggedinUser?.id!!)
        else
            channelUrl = getChannelName(userId, loggedinUser?.id!!)
        CommonMethod.makeLog("Channel url", channelUrl)
        callback?.onFetched(channelUrl)
        /*searchChannels(channelUrl, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (allChannels == null || allChannels.size == 0) {
                    // channel does not exist. create one
                    createUserChannel(
                        user, type
                    ) { channel ->
                        if (channel == null)
                            callback?.onFetched("0")
                        else callback?.onFetched(channel.url)
                    }
                } else {
                    val channel = allChannels[0]
                    if (!channel.customType.equals(type) && !type.equals(SendBirdConstants.CHANNEL_TYPE_FLAT)) {
                        val apiManager = SendBirdApiManager()
                        val map = HashMap<String, String>()
                        map.put("custom_type", type)
                        apiManager.updateChannelDetail(
                            channel.channelUrl,
                            map
                        ) { joinChannelrespond(channel, user, callback) }
                    } else
                        joinChannelrespond(channel, user, callback)
                }
            }

        })*/
    }

    private fun joinChannelrespond(channel: ChannelsItem, user: User, callback: OnStringFetched?) {
        var found = false
        if (!channel.members.isNullOrEmpty()) {
            for (member in channel.members) {
                if (member?.userId.equals(user.id)) {
                    found = true
                    break
                }
            }
        }
        if (found) callback?.onFetched(channel.channelUrl)
        else {
            joinChannel(
                channel.channelUrl, user.id
            ) { callback?.onFetched(channel.channelUrl) }
        }
    }

    fun reportUser(userId: String) {
        SendbirdChat.blockUser(userId) { user, e -> }
        val channelName = getChannelName(loggedinUser?.id!!, userId)
        searchChannels(channelName, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (allChannels != null) {
                    for (channel in allChannels) {
                        deleteChannel(channel.channelUrl)
                    }
                }
            }

        })
    }

    private fun searchChannels(channelUrl: String, callback: OnChannelsFetchedListener) {
        val params = HashMap<String, String>()
        params["limit"] = "100"
        params["public_mode"] = "public"
        params["show_empty"] = "true"
        params["show_member"] = "true"
        params["channel_urls"] = channelUrl
        sendBirdApiManager.searchChannel(
            params
        ) { response ->
            callback.onFetched(response?.channels)
        }
    }
}