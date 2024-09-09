package com.joinflatshare.ui.admin.features

import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.db.tables.TableSendbirdUser
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by debopam on 22/08/24
 */
class SendbirdImagePathFix {
    private lateinit var activity: BaseActivity
    private var next = ""
    val users = ArrayList<TableSendbirdUser>()

    fun fix(activity: BaseActivity) {
        this.activity = activity
        getAllUsers()
    }

    private fun getAllUsers() {
        users.addAll(
            FlatShareApplication.getDbInstance().sendBirdUserDao()
                .getSendbirdUsersWithWrongImagePath()
        )
        CommonMethod.makeLog("Wrong Image User Count", "" + users.size)
        workOnEachUser()
    }

    private fun workOnEachUser() {
        if (users.isNotEmpty()) {
            CommonMethod.makeToast("Users Left " + users.size)
            CommonMethod.makeLog("Users Left", "" + users.size)
            addUser(users[0])
        } else {
            CommonMethod.makeToast("Clear")
        }
    }

    private fun addUser(userToAdd: TableSendbirdUser) {
        val dbUser =
            FlatShareApplication.getDbInstance().flatshareUserDao().getFlatshareUser(userToAdd.id)
        if (dbUser == null) {
            checkApi(userToAdd)
        } else {
            if (dbUser.dp.isNullOrEmpty()) {
                users.removeAt(0)
                workOnEachUser()
            } else {
                val sendBirdUser = SendBirdUser(activity)
                val params = HashMap<String, String>()
                params["profile_url"] = dbUser.dp!!
                sendBirdUser.updateUser(
                    userToAdd.id, params
                ) {
                    userToAdd.dp = params["profile_url"]
                    FlatShareApplication.getDbInstance().sendBirdUserDao().updateUser(userToAdd)
                    users.removeAt(0)
                    Thread.sleep(1000)
                    workOnEachUser()
                }
            }

        }
    }

    private fun checkApi(userToAdd: TableSendbirdUser) {
        activity.baseApiController.getUser(false, userToAdd.id, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                if (resp?.data?.dp.isNullOrEmpty()) {
                    users.removeAt(0)
                    workOnEachUser()
                } else {
                    val dp: String = if (resp?.data?.dp.isNullOrEmpty()) "" else resp?.data?.dp!!
                    val sendBirdUser = SendBirdUser(activity)
                    val params = HashMap<String, String>()
                    params["profile_url"] = dp
                    sendBirdUser.updateUser(
                        userToAdd.id, params
                    ) {
                        userToAdd.dp = params["profile_url"]
                        FlatShareApplication.getDbInstance().sendBirdUserDao().updateUser(userToAdd)
                        users.removeAt(0)
                        Thread.sleep(1000)
                        workOnEachUser()
                    }
                }
            }
        })
    }
}