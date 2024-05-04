package com.joinflatshare.db.daos

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Query
import com.google.gson.Gson
import com.joinflatshare.db.tables.TableUser
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse

@Dao
abstract class UserDao {
    companion object {
        const val USER_CONSTANT_USERID = "USER_ID"
        const val USER_CONSTANT_LAST_UPDATED_LOCATION = "LAST_UPDATED_LOCATION"
        const val USER_CONSTANT_LAST_FETCHED_LOCATION = "LAST_FETCHED_LOCATION"
        const val USER_CONSTANT_LOCATION_LATITUDE = "LOCATION_LATITUDE"
        const val USER_CONSTANT_LOCATION_LONGITUDE = "LOCATION_LONGITUDE"
        const val USER_CONSTANT_FLATRESPONSE = "FLATRESPONSE"
        const val USER_CONSTANT_USERRESPONSE = "USERRESPONSE"
        const val LAST_GLIDE_CACHE_CLEAR = "LAST_GLIDE_CACHE_CLEAR"
        const val USER_NEED_FCM_UPDATE = "USER_NEED_FCM_UPDATE"
        const val USER_PURCHASE_ORDER_LIST = "USER_PURCHASE_ORDER_LIST"
        const val USER_REQUEST_API_PENDING = "USER_REQUEST_API_PENDING"
        const val USER_KEY_API_TOKEN = "API_TOKEN"
        const val USER_KEY_GET_FLAT_REQUIRED = "USER_KEY_GET_FLAT_REQUIRED"
    }

    @Query("Select * from TableUser")
    abstract fun getAppInformation(): TableUser?

    // Clear Table
    @Query("Delete from TableUser")
    abstract fun clearUserTable()

    // Long
    fun getLong(key: String): Long {
        val stringValue = get(key)
        if (stringValue == null || stringValue == "")
            return 0L
        else return stringValue.toLong()
    }

    @Query("INSERT OR REPLACE INTO TableUser (keyParam,valueParam) VALUES(:key,:value)")
    fun insert(key: String, value: Long) {
        insert(key, "" + value)
    }

    @Query("Delete from TableUser where keyParam=:key")
    abstract fun delete(key: String)

    @Query("INSERT OR REPLACE INTO TableUser (keyParam,valueParam) VALUES(:key,:value)")
    fun insert(key: String, value: Int) {
        insert(key, "" + value)
    }

    // Double
    fun getDouble(key: String): Double {
        val stringValue = get(key)
        if (stringValue == null || stringValue == "")
            return 0.0
        else return stringValue.toDouble()
    }

    // Int
    fun getInt(key: String): Int {
        val stringValue = get(key)
        if (stringValue == null || stringValue == "")
            return 0
        else return stringValue.toInt()
    }

    @Query("INSERT OR REPLACE INTO TableUser (keyParam,valueParam) VALUES(:key,:value)")
    fun insert(key: String, value: Double) {
        insert(key, "" + value)
    }

    // String
    @Query("INSERT OR REPLACE INTO TableUser (keyParam,valueParam) VALUES(:key,:value)")
    abstract fun insert(key: String, value: String?)

    @Query("Select valueParam from TableUser where keyParam=:key")
    abstract fun get(key: String): String?

    // Flat
    fun updateFlatResponse(flatData: FlatResponse?) {
        val json: String
        if (flatData == null)
            json = ""
        else
            json = Gson().toJson(flatData)
        insert(USER_CONSTANT_FLATRESPONSE, json)
    }

    fun getFlatResponse(): FlatResponse? {
        return Gson().fromJson(get(USER_CONSTANT_FLATRESPONSE), FlatResponse::class.java)
    }

    fun getFlatData(): MyFlatData? {
        return Gson().fromJson(get(USER_CONSTANT_FLATRESPONSE), FlatResponse::class.java)?.data
    }

    // User
    fun updateUserResponse(userResponse: UserResponse?) {
        val json: String
        if (userResponse == null)
            json = ""
        else
            json = Gson().toJson(userResponse)
        insert(USER_CONSTANT_USERRESPONSE, json)
        insert(USER_CONSTANT_USERID, userResponse?.data?.id!!)
    }

    fun getUserResponse(): UserResponse? {
        return Gson().fromJson(get(USER_CONSTANT_USERRESPONSE), UserResponse::class.java)
    }

    fun getUser(): User? {
        return Gson().fromJson(
            get(USER_CONSTANT_USERRESPONSE),
            UserResponse::class.java
        )?.data
    }

    fun isPurchaseOrderGenuine(orderId: String?): Boolean {
        var isGenuine = false
        if (!orderId.isNullOrEmpty()) {
            val orderList = get(USER_PURCHASE_ORDER_LIST)
            if (!orderList.isNullOrEmpty()) {
                val orderArray = TextUtils.split(orderList, ",")
                if (!orderArray.contains(orderId)) {
                    isGenuine = true
                    val list = ArrayList<String>()
                    list.addAll(orderArray)
                    list.add(orderId)
                    insert(USER_PURCHASE_ORDER_LIST, TextUtils.join(",", list))
                }
            } else {
                // This is a first time order for user. Add to DB
                insert(USER_PURCHASE_ORDER_LIST, orderId)
                isGenuine = true
            }
        }
        return isGenuine
    }
}