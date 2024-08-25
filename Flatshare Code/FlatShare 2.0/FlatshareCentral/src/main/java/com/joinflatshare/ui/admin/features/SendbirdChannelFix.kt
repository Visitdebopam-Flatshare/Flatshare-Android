package com.joinflatshare.ui.admin.features

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

/**
 * Created by debopam on 22/08/24
 */
class SendbirdChannelFix {
    private val TAG = SendbirdChannelFix::class.java.simpleName
    private val sendBirdApiManager = SendBirdApiManager()
    private var activity: BaseActivity? = null
    private var next = ""
    val genuineNumbers = ArrayList<ChannelsItem>()

    fun fix(activity: BaseActivity) {
        this.activity = activity
        getAllChannels()
    }

    private fun getAllChannels() {
        val params = HashMap<String, String>()
        params["limit"] = "60"
        params["public_mode"] = "public"
        params["show_empty"] = "true"
        params["show_member"] = "true"
        params["custom_types"] = SendBirdConstants.CHANNEL_TYPE_CONNECTION_FHT
        params["order"] = "chronological"
        if (next.isNotEmpty())
            params["token"] = next
        sendBirdApiManager.searchChannel(params, object : OnResponseCallback<ChannelListResponse> {
            override fun oncallBack(response: ChannelListResponse?) {
                next =
                    if (response?.next.isNullOrEmpty()) "" else if (response?.next == next) "" else response?.next!!
                getActualChannels(response)
            }
        })
    }

    private fun getActualChannels(response: ChannelListResponse?) {
        response?.channels?.forEach { channel ->
            run {
                if (channel.members.isNullOrEmpty()) {
                    deleteChannel(channel)
                } else {
                    var url = channel.channelUrl
                    url = url?.replace("USER_FHT_", "")
                    val numbers = url?.split("_")
                    if (numbers != null && numbers.size == 2) {
                        if (isValidNumber(numbers[0]) && isValidNumber(numbers[1])) {
                            if (channel.members.size == 1)
                                genuineNumbers.add(channel)
                        }
                    }
                }
            }
        }
        if (genuineNumbers.isEmpty()) {
            if (next.isNotEmpty())
                getAllChannels()
        } else
            printLog(genuineNumbers)
    }

    private fun isValidNumber(number: String): Boolean {
        return (/*number.startsWith("6")
                || number.startsWith("7")
                || number.startsWith("8")
                || number.startsWith("9")
                && */number.length == 10)
    }

    private fun printLog(genuineNumbers: ArrayList<ChannelsItem>) {
        CommonMethod.makeLog(TAG, "Total count  " + genuineNumbers.size)
        genuineNumbers.forEach { channel ->
            checkForValidUser(channel)
            Thread.sleep(5000)
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
                    addUser(userToAdd, channel)
//                    joinChannel(userToAdd, channel, true)

                }
            }
        }
    }

    private fun joinChannel(userToAdd: String, channel: ChannelsItem, shouldRetry: Boolean) {
        // This member does not exist in the channel. Add him
        CommonMethod.makeLog(TAG, "Calling join channel")
        sendBirdApiManager.joinChannel(
            channel.channelUrl, userToAdd
        ) {
            CommonMethod.makeLog(TAG, "Got callback from join channel")
            /*if (!it.error)
                CommonMethod.makeLog(
                    TAG,
                    "Member $userToAdd added in channel ${channel.channelUrl}"
                )
            else {
                CommonMethod.makeLog(
                    TAG,
                    "Member $userToAdd does not exist"
                )
                addUser(userToAdd, channel)
            }*/
        }
    }

    private fun addUser(userToAdd: String, channel: ChannelsItem) {
        CommonMethod.makeLog(TAG, "Calling Get user api")
        activity?.baseApiController?.getUser(false, userToAdd, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                CommonMethod.makeLog(TAG, "Got callback from get user api")
                val name = resp?.data?.name
                if (!name?.firstName.isNullOrEmpty()
                    && !name?.lastName.isNullOrEmpty()
                    && AppConstants.isSendbirdLive
                ) {
                    val sendBirdUser = SendBirdUser(activity)
                    val params =
                        HashMap<String, String>()
                    params["user_id"] = userToAdd
                    params["nickname"] =
                        resp?.data?.name?.firstName + " " + resp?.data?.name?.lastName
                    params["profile_url"] =
                        if (resp?.data?.dp.isNullOrEmpty()) "" else resp?.data?.dp!!
                    CommonMethod.makeLog(TAG, "Calling sendbird create user")
                    sendBirdUser.createUser(
                        params
                    ) {
                        CommonMethod.makeLog(TAG, "Got callback from create sendbird user api")
                        joinChannel(userToAdd, channel, false)
                    }
                }
            }

        })
    }

    private fun deleteChannel(channel: ChannelsItem) {
        CommonMethod.makeLog(TAG, "Deleting channel ${channel.channelUrl}")
        sendBirdApiManager.deleteChannel(
            channel.channelUrl
        ) { }
    }
}