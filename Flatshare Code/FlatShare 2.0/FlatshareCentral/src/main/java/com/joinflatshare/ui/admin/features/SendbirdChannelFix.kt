package com.joinflatshare.ui.admin.features

import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.chat.pojo.channel_list.ChannelListResponse
import com.joinflatshare.chat.pojo.channel_list.ChannelsItem
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by debopam on 22/08/24
 */
class SendbirdChannelFix {
    private val TAG = SendbirdChannelFix::class.java.simpleName
    private val sendBirdApiManager = SendBirdApiManager()
    private lateinit var activity: BaseActivity
    private var next = ""
    val channelsWithOneMember = ArrayList<ChannelsItem>()

    fun fix(activity: BaseActivity) {
        this.activity = activity
        DialogCustomProgress.showProgress(activity)
        val executor: Executor = Executors.newSingleThreadExecutor()
        executor.execute {
            getAllChannels()
        }
    }

    private fun getAllChannels() {
        val params = HashMap<String, String>()
        params["limit"] = "60"
        params["public_mode"] = "public"
        params["show_empty"] = "true"
        params["show_member"] = "true"
        params["custom_types"] = SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
        params["order"] = "chronological"
        if (next.isNotEmpty()) params["token"] = next
        sendBirdApiManager.searchChannel(params, object : OnResponseCallback<ChannelListResponse> {
            override fun oncallBack(response: ChannelListResponse?) {
                next =
                    if (response?.next.isNullOrEmpty()) "" else if (response?.next == next) "" else response?.next!!
                deleteChannels(response)
                getActualChannels(response)
            }
        })
    }

    private fun deleteChannels(response: ChannelListResponse?) {
        response?.channels?.forEach { channel ->
            run {
                if (channel.members.isNullOrEmpty()) {
                    deleteChannel(channel)
                }
            }
        }
    }

    private fun getActualChannels(response: ChannelListResponse?) {
        response?.channels?.forEach { channel ->
            run {
                var url = channel.channelUrl
                url = url?.replace("USER_FHT_", "")
                val numbers = url?.split("_")
                if (numbers != null && numbers.size == 2) {
                    if (numbers[0].length == 10 && numbers[1].length == 10 && channel.members?.size == 1) {
                        channelsWithOneMember.add(channel)
                    }
                }
            }
        }
        if (next.isNotEmpty() && channelsWithOneMember.size < 20) getAllChannels()
        else {
            DialogCustomProgress.hideProgress(activity)
            processChannels()
        }
    }

    private fun processChannels() {
        CommonMethod.makeLog(TAG, "Total count  " + channelsWithOneMember.size)
        if (channelsWithOneMember.size > 0) {
            checkForValidUser(channelsWithOneMember[0])
        } else {
            if (next.isNotEmpty()) {
                DialogCustomProgress.showProgress(activity)
                getAllChannels()
            }
        }
    }

    private fun checkForValidUser(channel: ChannelsItem) {
        var url = channel.channelUrl
        url = url?.replace("USER_FHT_", "")
        val numbers = url?.split("_")
        val members = channel.members
        if (!members.isNullOrEmpty()) {
            members.forEach { member ->
                run {
                    val userToAdd =
                        if (member?.userId.equals(numbers!![0])) numbers!![1] else numbers!![0]
                    getUserFromFlatShare(userToAdd, channel)
                }
            }
        }
    }

    private fun getUserFromFlatShare(userToAdd: String, channel: ChannelsItem) {
        activity?.baseApiController?.getUser(false, userToAdd, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                val name = resp?.data?.name
                if (!name?.firstName.isNullOrEmpty() && AppConstants.isSendbirdLive) {
                    if (name?.lastName.isNullOrEmpty()) name?.lastName = "Mehta"
                    val sendBirdUser = SendBirdUser(activity)
                    val params = HashMap<String, String>()
                    params["user_id"] = userToAdd
                    params["nickname"] =
                        resp?.data?.name?.firstName + " " + resp?.data?.name?.lastName
                    params["profile_url"] =
                        if (resp?.data?.dp.isNullOrEmpty()) "" else resp?.data?.dp!!
                    CommonMethod.makeLog(TAG, "Calling sendbird create user")
                    sendBirdUser.createUser(
                        params
                    ) {
                        joinChannel(userToAdd, channel)
                    }
                }
            }

        })
    }

    private fun joinChannel(userToAdd: String, channel: ChannelsItem) {
        // This member does not exist in the channel. Add him
        sendBirdApiManager.joinChannel(
            channel.channelUrl, userToAdd
        ) {
            channelsWithOneMember.removeAt(0)
            Thread.sleep(3000)
            processChannels()
        }
    }

    private fun deleteChannel(channel: ChannelsItem) {
        CommonMethod.makeLog(TAG, "Deleting channel ${channel.channelUrl}")
        sendBirdApiManager.deleteChannel(
            channel.channelUrl
        ) { }
    }
}