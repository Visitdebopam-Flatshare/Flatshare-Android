package com.joinflatshare.chat

import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.chatInterfaces.OnChannelCreatedListener
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener
import com.joinflatshare.chat.metadata.FlatMetadata
import com.joinflatshare.chat.pojo.channel_detail.ChannelDetailResponse
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.interfaces.OnFlatFetched
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.params.GroupChannelListQueryParams

class SendBirdFlatmateChannel(private val activity: BaseActivity) :
    SendBirdChannel(activity) {
    private fun createFlatMateChannel(
        flatId: String,
        userId: String,
        type: String,
        callback: OnChannelCreatedListener?
    ) {
        val channelName = getFlatmateChannelUrl(flatId, userId)

        // User IDs
        val userIds = ArrayList<String>()
        // check if flat id is my own flat
        if (flatId.equals(FlatShareApplication.getDbInstance().userDao().getFlatData()?.mongoId)) {
            val flatResponse = FlatShareApplication.getDbInstance().userDao().getFlatResponse()
            if (!flatResponse?.flatMates.isNullOrEmpty()) {
                for (user in flatResponse?.flatMates!!)
                    userIds.add(user.id)
            }

            activity.baseApiController.getUser(false, userId, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    userIds.add(userId)
                    createFlatMateChannel(
                        channelName,
                        type,
                        flatResponse,
                        resp?.data?.name?.firstName + " " + resp?.data?.name?.lastName,
                        userIds,
                        callback
                    )
                }
            })
        } else {
            // not my flat. call api
            activity.baseApiController.getFlat(false, flatId, object : OnFlatFetched {
                override fun flatFetched(resp: FlatResponse?) {
                    if (resp?.flatMates?.isNotEmpty() == true) {
                        for (user in resp.flatMates)
                            userIds.add(user.id)
                    }
                    val user = FlatShareApplication.getDbInstance().userDao().getUser()
                    userIds.add(user?.id!!)
                    createFlatMateChannel(
                        channelName,
                        type,
                        resp,
                        user.name?.firstName + " " + user.name?.lastName,
                        userIds,
                        callback
                    )
                }
            })
        }
    }

    private fun createFlatMateChannel(
        groupChannelUrl: String, type: String, flatData: FlatResponse?, userName: String,
        groupUserIds: ArrayList<String>, callback: OnChannelCreatedListener?
    ) {

        // MetaData
        val metadata = FlatMetadata()
        metadata.flatResponse = flatData
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
            name = getFlatmateChannelName(flatData?.data?.name!!, userName)
        }

        GroupChannel.createChannel(params) { groupChannel: GroupChannel?, e: SendbirdException? ->
            if (e != null) {
                CommonMethod.makeToast("Failed to create a channel")
                callback?.onCreated(null)
            }
            callback?.onCreated(groupChannel)
        }
    }

    private fun getFlatmateChannelUrl(flat_id: String, userId: String): String {
        return "FLATMATE_$flat_id" + "_" + userId
    }

    private fun getFlatmateChannelName(flatName: String, userName: String): String {
        return "$flatName x $userName"
    }

    fun addFlatMate(flatId: String, userId: String, callback: OnStringFetched?) {
        val channelUrl = getFlatmateChannelUrl(flatId, userId)
        CommonMethod.makeLog("Channel url", channelUrl)
        callback?.onFetched(channelUrl)
        /*searchChannels(channelUrl, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (allChannels.isNullOrEmpty()) {
                    // channel does not exist. create one
                    createFlatMateChannel(
                        flatId, userId, type
                    ) { channel ->
                        if (channel == null)
                            callback?.onFetched("")
                        else callback?.onFetched(channel.url)
                    }
                } else {
                    val channel = allChannels[0]
                    if (!channel.customType.equals(type)) {
                        val apiManager = SendBirdApiManager(activity)
                        val map = HashMap<String, String>()
                        map.put("custom_type", type)
                        apiManager.updateChannelDetail(
                            channel.channelUrl,
                            map
                        ) { joinChannelrespond(channel, userId, callback) }
                    } else
                        joinChannelrespond(channel, userId, callback)
                }
            }

        })*/
    }

    private fun joinChannelrespond(
        channel: ChannelsItem,
        userId: String,
        callback: OnStringFetched?
    ) {
        var found = false
        if (!channel.members.isNullOrEmpty()) {
            for (member in channel.members) {
                if (member?.userId.equals(userId)) {
                    found = true
                    break
                }
            }
        }
        if (found) callback?.onFetched(channel.channelUrl)
        else {
            joinChannel(
                channel.channelUrl, userId
            ) { callback?.onFetched(channel.channelUrl) }
        }
    }

    fun removeFlatMember(flatId: String, userId: String) {
        /*val channelUrl = getFlatmateChannelUrl(flatId, userId)
        val query = GroupChannel.createMyGroupChannelListQuery().apply {
            isIncludeEmpty = true
            channelUrlsFilter = listOf(channelUrl)
            limit = 100
        }
        query.next { queryResult, e ->
            if (!queryResult.isNullOrEmpty()) {
                val users = ArrayList<String>()
                users.add(userId)
                for (channel in queryResult) {
                    sendBirdApiManager.leaveChannel(
                        channel.url, users
                    ) { }
                }
            }
        }*/
    }

    fun unmatchFlat(flatId: String, userId: String) {
        val channelUrl = getFlatmateChannelUrl(flatId, userId)
        val params = GroupChannelListQueryParams().apply {
            includeEmpty = true
            channelUrlsFilter = listOf(channelUrl)
            limit = 100
        }
        val query = GroupChannel.createMyGroupChannelListQuery(params)
        query.next { queryResult, e ->
            if (!queryResult.isNullOrEmpty()) {
                for (channel in queryResult) {
                    deleteChannel(channel.url)
                }
            }
        }
    }

    fun removeUserFromAllFlatmates(flatId: String, userId: String) {
        /*val channelUrl = "FLATMATE_$flatId" + "_"
        searchChannels(channelUrl, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (!allChannels.isNullOrEmpty()) {
                    val users = ArrayList<String>()
                    users.add(userId)
                    for (channel in allChannels) {
                        if (!channel.members.isNullOrEmpty()) {
                            for (member in channel.members) {
                                if (member?.userId.equals(userId)) {
                                    leaveChannel(
                                        channel.channelUrl, users
                                    ) { }
                                    break
                                }
                            }
                        }
                    }
                }
            }
        })*/
    }

    fun updateFlatName(oldName: String, newName: String) {
        val channelName = "$oldName x "
        val params = GroupChannelListQueryParams().apply {
            includeEmpty = true
            channelNameContainsFilter = channelName
            limit = 100
        }
        val query = GroupChannel.createMyGroupChannelListQuery(params)
        query.next { queryResult, e ->
            if (!queryResult.isNullOrEmpty()) {
                for (channel in queryResult) {
                    deleteChannel(channel.url)
                }
            }
        }
        query.next { queryResult, e ->
            if (!queryResult.isNullOrEmpty()) {
                val params = java.util.HashMap<String, String>()
                for (item in queryResult) {
                    val name = newName + item.name.substring(item.name.indexOf(" x "))
                    params["name"] = name
                    sendBirdApiManager.updateChannelDetail(
                        item.url, params
                    ) { response1: ChannelDetailResponse? ->
                    }
                }
            }
        }


        /*searchChannels(channelUrl, object : OnChannelsFetchedListener {
            override fun onFetched(allChannels: MutableList<GroupChannel>?) {

            }

            override fun onFetched(allChannels: java.util.ArrayList<ChannelsItem>?) {
                if (!allChannels.isNullOrEmpty()) {
                    val params = java.util.HashMap<String, String>()
                    for (item in allChannels) {
                        val name = flatName + item.name?.substring(item.name.indexOf(" "))
                        params["name"] = name
                        sendBirdApiManager.updateChannelDetail(
                            item.channelUrl, params
                        ) { response1: ChannelDetailResponse? ->
                        }
                    }
                }
            }
        })*/
    }

    private fun searchChannels(channelUrl: String, callback: OnChannelsFetchedListener) {
        val params = HashMap<String, String>()
        params["limit"] = "100"
        params["public_mode"] = "public"
        params["show_empty"] = "true"
        params["show_member"] = "true"
        if (channelUrl.isNotEmpty())
            params["channel_urls"] = channelUrl
        sendBirdApiManager.searchChannel(
            params
        ) { response ->
            callback.onFetched(response?.channels)
        }
    }
}