package com.joinflatshare.ui.admin.features

import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.chat.pojo.user_list.ModelUserListResponse
import com.joinflatshare.db.FlatshareDbManager
import com.joinflatshare.db.tables.TableSendbirdUser
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 06/09/24
 */
class SendBirdUsersTableReload {
    private var next = ""
    private val sendBirdApiManager = SendBirdApiManager()
    private val userLists = ArrayList<TableSendbirdUser>()

    fun reload(activity: BaseActivity) {
        getAllSendbirdUsers(activity)
    }

    private fun getAllSendbirdUsers(activity: BaseActivity) {
        val params = HashMap<String, String>()
        params["limit"] = "100"
        params["show_bot"] = "false"
        if (next.isNotEmpty())
            params["token"] = next
        sendBirdApiManager.getAllUsers(params, object : OnResponseCallback<ModelUserListResponse> {
            override fun oncallBack(response: ModelUserListResponse?) {
                next =
                    if (response?.next.isNullOrEmpty()) "" else if (response?.next == next) "" else response?.next!!
                if (!response?.users.isNullOrEmpty()) {
                    response?.users?.forEach {
                        val tb = TableSendbirdUser(it.userId!!, it.nickname, it.profileUrl)
                        userLists.add(tb)
                    }
                }
                if (next.isNotEmpty())
                    getAllSendbirdUsers(activity)
                else {
                    CommonMethod.makeLog("All Sendbird Users Count", "" + userLists.size)
                    if (userLists.isNotEmpty()) {
                        FlatshareDbManager.getInstance(activity).sendBirdUserDao()
                            .deleteAllSendbirdUsers()
                        FlatshareDbManager.getInstance(activity).sendBirdUserDao()
                            .insertUser(userLists)
                    }
                }
            }
        })
    }
}