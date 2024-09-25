package com.joinflatshare.ui.admin.features

import com.debopam.progressdialog.DialogCustomProgress
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.admin.features.model.SendbirdUserResponseItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import org.json.JSONArray

/**
 * Created by debopam on 06/09/24
 */
class DeleteUnregisteredUsersFromSendbird {

    private val flatshareUserMap = HashMap<String, SendbirdUserResponseItem>()
    private lateinit var activity: BaseActivity
    private var next = ""
    val allSendBirdUsers = ArrayList<String>()
    val sendBirdDemoUsers = ArrayList<String>()
    private val sendBirdApiManager = SendBirdApiManager()
    private var shouldCheckJson = true


    fun deleteUnregisteredUsers(activity: BaseActivity, shouldCheckJson: Boolean) {
        this.activity = activity
        this.shouldCheckJson = shouldCheckJson
        if (shouldCheckJson) {
            DialogCustomProgress.showProgress(activity)
            openFile()
        } else getAllSendbirdUsers()
    }

    private fun openFile() {
        val str = StringBuilder()
        activity.assets.open("all-users.json").bufferedReader().use {
            str.append(it.readText())
        }
        val jsonArray = JSONArray(str.toString())
        iterateJson(jsonArray)
    }

    private fun iterateJson(jsonArray: JSONArray) {
        val obj = jsonArray.optJSONObject(0)
        if (obj != null) {
            val fObj = Gson().fromJson(obj.toString(), SendbirdUserResponseItem::class.java)
            flatshareUserMap[fObj?.id!!] = fObj
            jsonArray.remove(0)
            iterateJson(jsonArray)
        } else {
            CommonMethod.makeLog("All Users Count", "" + flatshareUserMap.size)
            getAllSendbirdUsers()
        }
    }

    private fun getAllSendbirdUsers() {
        allSendBirdUsers.addAll(
            FlatShareApplication.getDbInstance().sendBirdUserDao().getAllSendbirdUsers()
        )
        CommonMethod.makeLog("All Sendbird Users Count", "" + allSendBirdUsers.size)
        if (allSendBirdUsers.isNotEmpty()) {
            if (shouldCheckJson)
                compare()
            else startDeleteProcessFromAPI()
        } else
            CommonMethod.makeToast("Reload Sendbird Table")
    }


    private fun compare() {
        allSendBirdUsers.forEach {
            if (flatshareUserMap.containsKey(it).not()) {
                sendBirdDemoUsers.add(it)
            }
        }
        CommonMethod.makeLog("All Sendbird Demo Users Count", "" + sendBirdDemoUsers.size)
        DialogCustomProgress.hideProgress(activity)
        startDeleteProcess()
    }


    private fun startDeleteProcess() {
        if (sendBirdDemoUsers.isNotEmpty()) {
            sendBirdApiManager.deleteUser(
                sendBirdDemoUsers[0]
            ) {
                FlatShareApplication.getDbInstance().sendBirdUserDao()
                    .deleteUser(sendBirdDemoUsers[0])
                sendBirdDemoUsers.removeAt(0)
                startDeleteProcess()
            }
        } else CommonMethod.makeLog("All Users Deleted", "Clear")
    }

    private fun startDeleteProcessFromAPI() {
        if (allSendBirdUsers.isNotEmpty()) {
            CommonMethod.makeLog("Users Left", "" + allSendBirdUsers.size)
            val user = allSendBirdUsers[0]
            activity.baseApiController.getUser(false, user, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    if (resp?.message?.contains("does not exists.") == true) {
                        sendBirdApiManager.deleteUser(
                            user
                        ) {
                            CommonMethod.makeLog("Deleted User ", user)
                            allSendBirdUsers.removeAt(0)
                            Thread.sleep(1000)
                            startDeleteProcessFromAPI()
                        }
                    } else {
                        allSendBirdUsers.removeAt(0)
                        startDeleteProcessFromAPI()
                    }
                }
            })
        } else {
            CommonMethod.makeLog("All Users Deleted", "Clear")
        }

    }
}