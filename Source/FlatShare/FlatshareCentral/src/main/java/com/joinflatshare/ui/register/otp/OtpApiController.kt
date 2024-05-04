package com.joinflatshare.ui.register.otp

import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.fcm.NotificationPermissionHandler
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.user.OtpRequest
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.profile.create.ProfileCreateActivity
import com.joinflatshare.ui.register.setup.RegisterInvitedActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils.otpEntered
import com.joinflatshare.utils.sms_retriever.SmsReader
import com.joinflatshare.utils.system.DeviceInformationCollector
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class OtpApiController(private val activity: OtpActivity) {
    var apiManager = ApiManager(activity)

    fun sendOtp() {
        if (activity.phone == null) {
            AlertImageDialog.somethingWentWrong(activity)
            return
        }
        if ((BuildConfig.FLAVOR == "ProServerDevAws" || BuildConfig.FLAVOR == "ProServerProAws"
                    && AppConstants.isAppLive && !activity.phone.equals("0123456789"))
        ) {
            WebserviceManager().sendOtp(
                activity,
                activity.phone,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        val resp = Gson().fromJson(response, BaseResponse::class.java)
                        if (resp.success) {
                            CommonMethods.makeToast("An OTP is sent to ${activity.phone}")
                            SmsReader(activity).initialiseRetriever()
                            activity.initReceiver()
                            LocalBroadcastManager.getInstance(activity)
                                .registerReceiver(
                                    activity.smsReceiver,
                                    IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_READ_SMS)
                                )
                        } else CommonMethods.makeToast(resp.message)
                    }
                })
        }
    }

    fun login(otp: String) {
        val imei = if (AppConstants.isAppLive) "" else "3518660922081972"
        val firebaseToken =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.FIREBASE_TOKEN)
        val request = OtpRequest(activity.phone, firebaseToken, otp, false, imei)
        WebserviceManager().login(activity,request,object :OnFlatshareResponseCallBack<Response<ResponseBody>>{
            override fun onResponseCallBack(response: String) {
                val resp=Gson().fromJson(response,UserResponse::class.java)
                LocalBroadcastManager.getInstance(activity)
                    .unregisterReceiver(activity.smsReceiver)
                otpEntered(activity.phone, otp)
                FlatShareApplication.getDbInstance().userDao().clearUserTable()
                val user = resp.data
                FlatShareApplication.getDbInstance().userDao()
                    .insert(UserDao.USER_KEY_API_TOKEN, resp.token)
                if (user != null) {
                    if (user.name == null || user.name!!.firstName.isBlank()) {
                        activity.userResponse = resp
                        checkLocation(user.id)
                    } else {
                        NotificationPermissionHandler(activity).showNotificationPermission {
                            CommonMethods.registerUser(resp)
                            DeviceInformationCollector()
                            val intent = Intent(activity, ExploreActivity::class.java)
                            CommonMethod.switchActivity(activity, intent, true)
                            activity.finishAffinity()
                        }
                    }
                }
            }
        })
    }

    fun checkLocation(userID: String) {
        /* LocationCheckHandler(activity, userID, true) { text ->
             if (text.equals("1")) {

             } else viewBind.otp_confirm.text = "Check Location"
         }*/
        val intent: Intent
        if (activity.userResponse.invite?.inv?.inviter.equals("0123456789")) {
            intent = Intent(
                activity, ProfileCreateActivity::class.java
            )
            intent.putExtra("user", activity.userResponse)
        } else {
            intent = Intent(
                activity, RegisterInvitedActivity::class.java
            )
            intent.putExtra("user", activity.userResponse)
            intent.putExtra("phone", activity.phone)
        }
        CommonMethod.switchActivity(activity, intent, true)
    }
}