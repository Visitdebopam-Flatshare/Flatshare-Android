package com.joinflatshare.db.daos

import androidx.room.Dao
import androidx.room.Query
import com.google.gson.Gson
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.requests.ConnectionItem
import com.joinflatshare.pojo.requests.FlatInviteItem

@Dao
abstract class RequestDao {
    fun handleFriendRequests(list: List<ConnectionItem>?) {
        delete(ChatRequestConstants.FRIEND_REQUEST_CONSTANT)
        if (!list.isNullOrEmpty()) {
            for (item in list) {
                insert(
                    item.notiID,
                    Gson().toJson(item),
                    ChatRequestConstants.FRIEND_REQUEST_CONSTANT
                )
            }
        }
    }

    fun getFriendRequests(): ArrayList<ConnectionItem> {
        val list = ArrayList<ConnectionItem>()
        val json = getJson(ChatRequestConstants.FRIEND_REQUEST_CONSTANT)
        if (!json.isNullOrEmpty()) {
            for (item in json) {
                list.add(Gson().fromJson(item, ConnectionItem::class.java))
            }
        }
        return list
    }


    fun handleFlatRequests(list: List<FlatInviteItem>?) {
        delete(ChatRequestConstants.FLAT_REQUEST_CONSTANT)
        if (!list.isNullOrEmpty()) {
            for (item in list) {
                insert(
                    item.notiID!!,
                    Gson().toJson(item),
                    ChatRequestConstants.FLAT_REQUEST_CONSTANT
                )
            }
        }
    }

    fun getFlatRequests(): ArrayList<FlatInviteItem> {
        val list = ArrayList<FlatInviteItem>()
        val json = getJson(ChatRequestConstants.FLAT_REQUEST_CONSTANT)
        if (!json.isNullOrEmpty()) {
            for (item in json) {
                list.add(Gson().fromJson(item, FlatInviteItem::class.java))
            }
        }
        return list
    }

    fun handleChatRequests(list: List<ConnectionItem>?, requestType: String) {
        delete(requestType)
        if (!list.isNullOrEmpty()) {
            for (item in list) {
                insert(
                    item.notiID,
                    Gson().toJson(item),
                    requestType
                )
            }
        }
    }


    fun getChatRequests(requestType: String): ArrayList<ConnectionItem> {
        val list = ArrayList<ConnectionItem>()
        val json = getJson(requestType)
        if (!json.isNullOrEmpty()) {
            for (item in json) {
                list.add(Gson().fromJson(item, ConnectionItem::class.java))
            }
        }
        return list
    }

    // Clear Table
    @Query("Delete from TableRequests")
    abstract fun clearRequestTable()

    @Query("Select COUNT(*) from TableRequests where requestType=:requestType")
    abstract fun getCount(requestType: String): Int

    @Query(
        "Select COUNT(*) from TableRequests where requestType in ("
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U + ","
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F + ","
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT + ","
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL + ","
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM + ","
                + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS + ")"
    )
    abstract fun getTotalChatCount(): Int

    @Query(
        "SELECT requestType FROM " +
                "(SELECT MAX (mycount), requestType FROM" +
                "(SELECT requestType, COUNT(requestType) mycount " +
                "FROM TableRequests \n" +
                "GROUP BY requestType));"
    )
    abstract fun getRequestTypeWithMaxChatRequests(): String

    @Query("Select jsonBody from TableRequests where requestType=:requestType")
    abstract fun getJson(requestType: String): List<String>?

    @Query("Delete from TableRequests where requestType=:requestType")
    abstract fun delete(requestType: String)

    @Query("Delete from TableRequests where notificationId=:notificationId")
    abstract fun deleteRequest(notificationId: String)

    // String
    @Query(
        "INSERT OR REPLACE INTO TableRequests (notificationId,jsonBody,requestType) " +
                "VALUES(:notificationId,:jsonBody,:requestType)"
    )
    abstract fun insert(notificationId: String, jsonBody: String?, requestType: String)
}