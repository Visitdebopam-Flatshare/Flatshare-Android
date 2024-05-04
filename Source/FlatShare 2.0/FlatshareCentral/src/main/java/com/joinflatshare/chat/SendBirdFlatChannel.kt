package com.joinflatshare.chat

import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.chat.chatInterfaces.OnChannelCreatedListener
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener
import com.joinflatshare.chat.metadata.FlatMetadata
import com.joinflatshare.chat.pojo.channel_detail.ChannelDetailResponse
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnFlatFetched
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.params.GroupChannelCreateParams

class SendBirdFlatChannel(private val activity: BaseActivity) :
    SendBirdChannel(activity) {

    private fun createFlatChannel(
        flatResponse: FlatResponse?,
        type: String,
        callback: OnChannelCreatedListener?
    ) {
        val groupChannelUrl = getFlatChannelName(flatResponse?.data?.mongoId!!)

        // User IDs
        val groupUserIds = ArrayList<String>()
        if (!flatResponse?.flatMates.isNullOrEmpty()) {
            for (user in flatResponse!!.flatMates)
                groupUserIds.add(user.id)
        } else {
            groupUserIds.add(
                FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID)!!
            )
        }

        // MetaData
        val metadata = FlatMetadata()
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
            name = flatResponse?.data?.name!!
        }

        GroupChannel.createChannel(params) { groupChannel: GroupChannel?, e: SendbirdException? ->
            if (e != null) {
                CommonMethod.makeToast("Failed to create a channel")
                callback?.onCreated(null)
            }
            callback?.onCreated(groupChannel)
        }
    }

    fun getFlatChannelName(flat_id: String): String {
        return "FLAT_$flat_id"
    }

    fun addFlatMember(flatId: String, userId: String, type: String, callback: OnStringFetched?) {
        searchChannels(flatId, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (!allChannels.isNullOrEmpty()) {
                    val channel = allChannels[0]
                    joinChannel(
                        channel.channelUrl, userId
                    ) {
                        CommonMethod.makeLog("Channel Url", channel.channelUrl)
                        callback?.onFetched(channel.channelUrl)
                    }
                } else {
                    // create channel
                    activity.baseApiController.getFlat(false, flatId,
                        object : OnFlatFetched {
                            override fun flatFetched(resp: FlatResponse?) {
                                createFlatChannel(resp, type, null)
                            }
                        })
                }
            }
        })
    }

    fun removeFlatMember(userId: String, flatId: String) {
        searchChannels(flatId, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                allChannels?.let {
                    val users = ArrayList<String>()
                    users.add(userId)
                    val apiManager = SendBirdApiManager()
                    for (channel in allChannels) {
                        // check if this is is the last member leaving. if yes, delete the channel
                        if (channel.memberCount == 1)
                            deleteChannel(channel.channelUrl)
                        else
                            leaveChannel(channel.channelUrl, users) { }
                    }
                }
            }
        })
    }

    fun updateFlatName(mongoId: String, newName: String) {
        searchChannels(mongoId, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (!allChannels.isNullOrEmpty()) {
                    val params = java.util.HashMap<String, String>()
                    params["name"] = newName
                    sendBirdApiManager.updateChannelDetail(
                        allChannels[0].channelUrl, params
                    ) { response1: ChannelDetailResponse? ->
                    }
                }

            }

        })
    }

    private fun searchChannels(flatId: String, callback: OnChannelsFetchedListener) {
        val channelUrl = getFlatChannelName(flatId)
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