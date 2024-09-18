package com.joinflatshare.ui.base

import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.api.retrofit.WebserviceCustomResponseHandler
import com.joinflatshare.chat.api.SendBirdApiManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnFlatFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.utils.helper.CommonMethod.makeLog
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import com.sendbird.android.SendbirdChat.registerPushToken
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 01/02/24
 */
class BaseApiController(private val activity: BaseActivity) {
    private val TAG = BaseApiController::class.java.simpleName

    protected fun updateUserOnFirebaseTokenUpdate() {
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        val fcmToken = FlatShareApplication.getDbInstance().appDao().get(AppDao.FIREBASE_TOKEN)
        if (user != null && fcmToken != null) {
            user.deviceToken = fcmToken
            updateUser(false, user, null)
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_NEED_FCM_UPDATE, "0")
            // Inform sendbird on new push token
            SendBirdApiManager().deleteAllPushTokens { response: com.joinflatshare.pojo.BaseResponse? ->
                registerPushToken(
                    fcmToken,
                    null
                )
            }
        }
    }

    fun updateUser(showProgress: Boolean, user: User?, callback: OnUserFetched?) {
        WebserviceManager().updateProfile(showProgress, activity, user,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: UserResponse? = Gson().fromJson(response, UserResponse::class.java)
                    WebserviceCustomResponseHandler.handleUserResponse(resp)
                    callback?.userFetched(resp)
                }
            })
    }

    fun getUser(showProgress: Boolean, userId: String?, callback: OnUserFetched?) {
        WebserviceManager().getProfile(showProgress, activity, userId,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: UserResponse? = Gson().fromJson(response, UserResponse::class.java)
                    WebserviceCustomResponseHandler.handleUserResponse(resp)
                    callback?.userFetched(resp)
                }
            })
    }

    fun getFlat(showProgress: Boolean, flatId: String?, callback: OnFlatFetched) {
        WebserviceManager().getFlat(showProgress, activity, flatId,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: FlatResponse? = Gson().fromJson(response, FlatResponse::class.java)
                    WebserviceCustomResponseHandler.handleFlatResponse(resp)
                    callback.flatFetched(resp)
                }
            })
    }

    fun updateUserLocation() {
        val user = AppConstants.loggedInUser
        if (user != null) {
            val latitude = FlatShareApplication.getDbInstance().userDao()
                .getDouble(UserDao.USER_CONSTANT_LOCATION_LATITUDE)
            val longitude = FlatShareApplication.getDbInstance().userDao()
                .getDouble(UserDao.USER_CONSTANT_LOCATION_LONGITUDE)
            val location = ModelLocation()
            location.loc.coordinates.add(0, longitude)
            location.loc.coordinates.add(1, latitude)
            user.location = location
            updateUser(false, user, null)
            makeLog(TAG, "User Location Updated")
        }
    }
}