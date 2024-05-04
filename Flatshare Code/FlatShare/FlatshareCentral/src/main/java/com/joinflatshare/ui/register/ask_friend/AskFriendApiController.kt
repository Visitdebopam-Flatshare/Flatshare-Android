package com.joinflatshare.ui.register.ask_friend

import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.constants.ContactConstants
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.invite.InvitedResponse
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.invite.InviteActivity.Companion.INVITE_TYPE_APP
import com.joinflatshare.utils.helper.CommonMethod

class AskFriendApiController(private val activity: AskFriendActivity) {
    var count = 0
    private val contactSubList = ArrayList<String>()
    private val apiManager = ApiManager(activity)

    init {
        for (item in ContactConstants.allContacts)
            contactSubList.add(item.id)
        activity.allAppUsers.clear()
    }

    fun checkStatusFromContacts() {
        if (contactSubList.size > 0) {
            if (contactSubList.size > 1000) {
                val temp = ArrayList<String>()
                for (i in 0 until 100) {
                    temp.add(contactSubList[i])
                }
                callStatusApi(temp)
            } else callStatusApi(contactSubList)
        } else {
            activity.showFriendList()
        }

    }

    private fun callStatusApi(contactSubLists: ArrayList<String>) {
        if (activity.phone == null) {
            AlertImageDialog.somethingWentWrong(activity)
            CommonMethod.finishActivity(activity)
            return
        }
        val request = InvitedRequest()
        request.ids.addAll(contactSubLists)
        request.type = "0"
        request.requester = activity.phone!!
        apiManager.getInvitedStatus(
            INVITE_TYPE_APP, request
        ) { response ->
            val resp = response as InvitedResponse
            for (item in resp.data) {
                if (item.isRegistered) {
                    val user = User()
                    user.id = item.id
                    user.name = activity.contactMap[item.id]!!
                    if (!activity.allAppUsersId.contains(item.id)) {
                        activity.allAppUsers.add(user)
                        activity.allAppUsersId.add(item.id)
                    }
                }
                contactSubList.remove(item.id)
            }
            checkStatusFromContacts()
        }
    }

    fun requestInvite(from: String) {
        apiManager.requestInvite(
            from, activity.phone!!
        ) {
            activity.allAppUsersId.remove(from)
            for (i in 0 until activity.allAppUsers.size)
                if (activity.allAppUsers[i].id.equals(from)) {
                    activity.allAppUsers.removeAt(i)
                    break
                }
            activity.adapter.notifyDataSetChanged()
        }
    }
}