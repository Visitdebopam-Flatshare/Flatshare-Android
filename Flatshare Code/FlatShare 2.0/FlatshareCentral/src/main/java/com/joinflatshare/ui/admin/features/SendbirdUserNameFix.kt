package com.joinflatshare.ui.admin.features

import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.db.tables.TableSendbirdUser
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 22/08/24
 */
class SendbirdUserNameFix {
    private lateinit var activity: BaseActivity
    private var next = ""
    val users = ArrayList<TableSendbirdUser>()
    private var isMehta: Boolean = false
    private val sendBirdApiManager = SendBirdApiManager()

    fun fix(activity: BaseActivity) {
        this.activity = activity
        isMehta = false
        getAllUsers()
    }

    fun fixNullMehtaNames(activity: BaseActivity) {
        this.activity = activity
        isMehta = true
        getAllUsers()
    }

    private fun getAllUsers() {
        if (isMehta) {
            users.addAll(
                FlatShareApplication.getDbInstance().sendBirdUserDao()
                    .getSendbirdUsersWithNullName()
            )
        } else {
            users.addAll(
                FlatShareApplication.getDbInstance().sendBirdUserDao().getSendbirdUsersWithoutName()
            )
        }
        CommonMethod.makeLog("User Count", "" + users.size)
        workOnEachUser()
    }

    private fun workOnEachUser() {
        if (users.isNotEmpty()) {
            CommonMethod.makeToast("Users Left " + users.size)
            CommonMethod.makeLog("Users Left", "" + users.size)
            if (isMehta)
                checkApi(users[0])
            else {
                addUser(users[0])
            }
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
            val sendBirdUser = SendBirdUser(activity)
            val params = HashMap<String, String>()
            params["nickname"] = dbUser.name!!
            params["profile_url"] = dbUser.dp!!
            sendBirdUser.updateUser(
                userToAdd.id, params
            ) {
                userToAdd.name = params["nickname"]
                userToAdd.dp = params["profile_url"]
                FlatShareApplication.getDbInstance().sendBirdUserDao().updateUser(userToAdd)
                users.removeAt(0)
                Thread.sleep(1000)
                workOnEachUser()
            }
        }
    }

    private fun checkApi(userToAdd: TableSendbirdUser) {
        activity.baseApiController.getUser(false, userToAdd.id, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                if (resp?.message?.contains("does not exists.") == true) {
                    sendBirdApiManager.deleteUser(
                        userToAdd.id
                    ) {
                        CommonMethod.makeLog("Deleted User ", userToAdd.id)
                        users.removeAt(0)
                        Thread.sleep(1000)
                        workOnEachUser()
                    }
                } else {
                    val sendBirdUser = SendBirdUser(activity)
                    val params = HashMap<String, String>()
                    val fname = resp?.data?.name?.firstName
                    var lname = resp?.data?.name?.lastName
                    if (lname.isNullOrEmpty())
                        lname = ""
                    val dp: String = if (resp?.data?.dp.isNullOrEmpty()) "" else resp?.data?.dp!!
                    params["nickname"] = fname + " " + lname
                    params["profile_url"] = dp
                    sendBirdUser.updateUser(
                        userToAdd.id, params
                    ) {
                        userToAdd.name = params["nickname"]
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