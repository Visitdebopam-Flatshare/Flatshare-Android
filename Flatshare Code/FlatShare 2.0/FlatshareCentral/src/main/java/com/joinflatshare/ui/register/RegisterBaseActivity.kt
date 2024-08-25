package com.joinflatshare.ui.register

import com.google.gson.Gson
import com.joinflatshare.api.retrofit.WebserviceCustomResponseHandler
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.gpsfetcher.GpsHandler
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 03/02/24
 */
open class RegisterBaseActivity : GpsHandler() {
    fun getUser(
        activity: RegisterBaseActivity,
        showProgress: Boolean,
        userId: String?,
        callback: OnUserFetched
    ) {
        WebserviceManager().getProfile(showProgress, activity, userId,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: UserResponse? = Gson().fromJson(response, UserResponse::class.java)
                    WebserviceCustomResponseHandler.handleUserResponse(resp)
                    callback.userFetched(resp)
                }
            })
    }

    fun updateUser(user: User?, callback: OnUserFetched?) {
        WebserviceManager().updateProfile(true, this, user,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: UserResponse? = Gson().fromJson(response, UserResponse::class.java)
                    WebserviceCustomResponseHandler.handleUserResponse(resp)
                    callback?.userFetched(resp)
                }
            })
    }
}