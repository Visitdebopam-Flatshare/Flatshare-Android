package com.joinflatshare.ui.notifications

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.pojo.requests.ConnectionRequestResponse
import com.joinflatshare.pojo.requests.FlatInviteResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 31/05/23
 */
object RequestHandler {
    fun calculateTotalRequestCount(activity: BaseActivity) {
        val dao = FlatShareApplication.getDbInstance().requestDao()
        val totalCount = dao.getTotalChatCount()
        val frame: FrameLayout? = activity.findViewById(R.id.frame_count_notification)
        if (totalCount > 0) {
            val count: TextView? = activity.findViewById(R.id.txt_count_notification)
            count?.text = "" + totalCount
            frame?.visibility = View.VISIBLE
        } else frame?.visibility = View.GONE
        FlatShareApplication.getDbInstance().userDao().insert(UserDao.USER_REQUEST_API_PENDING, "0")
        LocalBroadcastManager.getInstance(activity)
            .sendBroadcast(Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_USER_REQUESTS_UPDATED))
    }

    // Request APIs
    fun getFriendRequest(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getFriendRequests(activity,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleFriendRequests(resp.data)
                    if (callAllApis)
                        getFlatRequests(activity, true)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getFlatRequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getFlatRequests(activity,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, FlatInviteResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao().handleFlatRequests(resp.data)
                    if (callAllApis)
                        getU2FRequests(activity, true)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getU2FRequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F
                        )
                    if (callAllApis)
                        getF2URequests(activity, true)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getF2URequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U
                        )
                    if (callAllApis)
                        getFHTRequests(activity, callAllApis)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getFHTRequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT
                        )
                    /*if (callAllApis)
                        getCasualDateRequests(activity, callAllApis)
                    else */calculateTotalRequestCount(activity)
                }
            })
    }

    fun getCasualDateRequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL.toString()
                        )
                    if (callAllApis)
                        getLongTermRequests(activity, callAllApis)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getLongTermRequests(activity: BaseActivity, callAllApis: Boolean) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM.toString()
                        )
                    if (callAllApis)
                        getActivityPartnerRequests(activity)
                    else calculateTotalRequestCount(activity)
                }
            })
    }

    fun getActivityPartnerRequests(activity: BaseActivity) {
        WebserviceManager().getChatRequests(
            activity,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS.toString(),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, ConnectionRequestResponse::class.java)
                    FlatShareApplication.getDbInstance().requestDao()
                        .handleChatRequests(
                            resp.data,
                            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS.toString()
                        )
                    calculateTotalRequestCount(activity)
                }
            })
    }
}