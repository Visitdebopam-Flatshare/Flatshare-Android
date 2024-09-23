package com.joinflatshare.ui.admin.features

import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.db.tables.TableFlatshareUser
import com.joinflatshare.ui.admin.features.model.SendbirdUserResponseItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import org.json.JSONArray

/**
 * Created by debopam on 06/09/24
 */
class FlatshareUsersTableReload {
    private lateinit var activity: BaseActivity
    private val userLists = ArrayList<TableFlatshareUser>()

    fun reload(activity: BaseActivity) {
        this.activity = activity
        openFile()
    }

    private fun openFile() {
        val str = StringBuilder()
        /*activity.assets.open("all-users.json").bufferedReader().use {
            str.append(it.readText())
        }*/
        activity.assets.open("fake-users.json").bufferedReader().use {
            str.append(it.readText())
        }
        val jsonArray = JSONArray(str.toString())
        iterateJson(jsonArray)
    }

    private fun iterateJson(jsonArray: JSONArray) {
        val obj = jsonArray.optJSONObject(0)
        if (obj != null) {
            val fObj = Gson().fromJson(obj.toString(), SendbirdUserResponseItem::class.java)
            val fname = fObj.name?.firstName
            var lname = fObj.name?.lastName
            if (lname.isNullOrEmpty())
                lname = "Mehta"
            val dp = if (fObj?.dp.isNullOrEmpty()) "" else fObj.dp
            userLists.add(TableFlatshareUser(fObj.id!!, "$fname $lname", dp))
            jsonArray.remove(0)
            iterateJson(jsonArray)
        } else {
            CommonMethod.makeLog("All Users Count", "" + userLists.size)
            FlatShareApplication.getDbInstance().flatshareUserDao().deleteAllFlatshareUsers()
            FlatShareApplication.getDbInstance().flatshareUserDao().insertUser(userLists)
        }
    }
}