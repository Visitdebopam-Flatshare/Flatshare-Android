package com.joinflatshare.ui.admin.features

import androidx.activity.ComponentActivity
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.chat.pojo.user_list.ModelUserListResponse
import com.joinflatshare.chat.pojo.user_list.UsersItem
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by debopam on 22/08/24
 */
class SendbirdUserNameFix {
    private val TAG = SendbirdUserNameFix::class.java.simpleName
    private val sendBirdApiManager = SendBirdApiManager()
    private lateinit var activity: BaseActivity
    private var next = ""
    val users = ArrayList<UsersItem>()
    val allUsers = ArrayList<UsersItem>()
    val unsafeUsers = ArrayList<String>()

    fun fix(activity: BaseActivity) {
        this.activity = activity
        DialogCustomProgress.showProgress(activity)
        unsafeUsers.add("8368058647")
        unsafeUsers.add("9940939133")
        unsafeUsers.add("9508798308")
        unsafeUsers.add("7897894575")
        unsafeUsers.add("9981680290")
        unsafeUsers.add("9740684000")
        val executor: Executor = Executors.newSingleThreadExecutor()
        executor.execute {
            getAllUsers()
        }
    }

    private fun getAllUsers() {
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
                        allUsers.add(it)
                        if (it.nickname.isNullOrEmpty() && unsafeUsers.contains(it.userId).not()) {
                            users.add(it)
                        }
                    }
                }
                if (next.isNotEmpty() && users.size < 20)
                    getAllUsers()
                else {
                    CommonMethod.makeLog("Total User Count", "" + allUsers.size)
                    CommonMethod.makeLog("No Name User Count", "" + users.size)
                    DialogCustomProgress.hideProgress(activity!!)
                    workOnEachUser()
                }
            }
        })
    }

    private fun workOnEachUser() {
        if (users.isNotEmpty()) {
            CommonMethod.makeToast("Users Left " + users.size)
            CommonMethod.makeLog("Users Left", "" + users.size)
            addUser(users[0].userId!!)
        } else {
            if (next.isNotEmpty()) {
                allUsers.clear()
                DialogCustomProgress.showProgress(activity)
                getAllUsers()
            }
        }
    }

    private fun addUser(userToAdd: String) {
        activity?.baseApiController?.getUser(false, userToAdd, object : OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                val name = resp?.data?.name
                if (!name?.firstName.isNullOrEmpty()
                    && AppConstants.isSendbirdLive
                ) {
                    if (name?.lastName.isNullOrEmpty())
                        name?.lastName = "Mehta"
                    val sendBirdUser = SendBirdUser(activity)
                    val params =
                        HashMap<String, String>()
                    params["nickname"] =
                        resp?.data?.name?.firstName + " " + resp?.data?.name?.lastName
                    params["profile_url"] =
                        if (resp?.data?.dp.isNullOrEmpty()) "" else resp?.data?.dp!!
                    sendBirdUser.updateUser(
                        userToAdd, params
                    ) {
                        users.removeAt(0)
                        Thread.sleep(3000)
                        workOnEachUser()
                    }
                }
            }
        })
    }
}