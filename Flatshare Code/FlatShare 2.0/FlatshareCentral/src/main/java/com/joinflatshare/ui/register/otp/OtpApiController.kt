package com.joinflatshare.ui.register.otp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.chat.SendBirdUser
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.fcm.NotificationPermissionHandler
import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.user.AdhaarOtp
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.dialogs.DialogLottieViewer
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.register.create.ProfileCreateActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.sms_retriever.SmsReader
import com.joinflatshare.utils.system.DeviceInformationCollector
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class OtpApiController(private val activity: OtpActivity) {
    var apiManager = ApiManager(activity)

    fun sendOtp() {
        if ((BuildConfig.FLAVOR == "ProServerDevAws" || BuildConfig.FLAVOR == "ProServerProAws"
                    && AppConstants.isAppLive && !activity.phone.equals("0123456789"))
        ) {
            WebserviceManager().sendOtp(
                activity,
                activity.phone,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        val resp = Gson().fromJson(response, com.joinflatshare.pojo.BaseResponse::class.java)
                        if (resp.success) {
                            activity.showError(false, "An OTP is sent to ${activity.phone}")
                            SmsReader(activity).initialiseRetriever()
                            activity.initReceiver()
                            LocalBroadcastManager.getInstance(activity)
                                .registerReceiver(
                                    activity.smsReceiver,
                                    IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_READ_SMS)
                                )
                        } else CommonMethod.makeToast(resp.message)
                    }
                })
        }
    }

    fun login(otp: String) {
        activity.showError(true, "")
        val imei = if (AppConstants.isAppLive) "" else "3518660922081972"
        val firebaseToken =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.FIREBASE_TOKEN)
        val request = com.joinflatshare.pojo.user.OtpRequest(
            activity.phone,
            firebaseToken,
            otp,
            false,
            imei
        )
        WebserviceManager().login(
            activity,
            request,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, UserResponse::class.java)
                    LocalBroadcastManager.getInstance(activity)
                        .unregisterReceiver(activity.smsReceiver)
                    MixpanelUtils.otpEntered(activity.phone, otp)
                    FlatShareApplication.getDbInstance().userDao().clearUserTable()
                    val user = resp.data
                    CommonMethods.registerUser(resp)
                    FlatShareApplication.getDbInstance().userDao()
                        .insert(UserDao.USER_KEY_API_TOKEN, resp.token)
                    if (user != null) {
                        if (user.name == null || user.name!!.firstName.isBlank()) {
                            val intent = Intent(activity, ProfileCreateActivity::class.java)
                            intent.putExtra(IntentConstants.INTENT_USER, user)
                            CommonMethod.switchActivity(activity, intent, false)
                            activity.finishAffinity()
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

                override fun onCallBackError(message: String) {
                    activity.showError(true, message)
                }
            })
    }


    fun verifyAadhar(adhaarOTP: String) {
        WebserviceManager().verifyAadhar(
            activity,
            AdhaarOtp(adhaarOTP),
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp = Gson().fromJson(response, com.joinflatshare.pojo.BaseResponse::class.java)
                    if (resp.status == 200) {
                        MixpanelUtils.onButtonClicked("Aadhar OTP Submit")
                        getProfile()
                    }
                }
            })
    }

    private fun getProfile() {
        WebserviceManager().getProfile(
            true,
            activity,
            AppConstants.loggedInUser?.id,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val sendBirdUser = SendBirdUser(activity)
                    val params = HashMap<String, String>()
                    params["nickname"] =
                        AppConstants.loggedInUser?.name?.firstName + " " + AppConstants.loggedInUser?.name?.lastName
                    params["profile_url"] =
                        if (AppConstants.loggedInUser?.dp.isNullOrBlank()) "" else AppConstants.loggedInUser?.dp!!
                    sendBirdUser.updateUser(
                        params
                    ) { }
                    DialogLottieViewer(activity, R.raw.lottie_verify_profile, null)
                    activity.setResult(Activity.RESULT_OK)
                    CommonMethod.finishActivity(activity)
                }
            })
    }
}