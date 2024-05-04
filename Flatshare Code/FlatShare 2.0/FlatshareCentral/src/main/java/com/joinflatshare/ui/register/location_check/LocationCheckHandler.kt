package com.joinflatshare.ui.register.location_check

import android.content.Intent
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import com.debopam.flatshareprogress.DialogCustomProgress
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.location.LocationCheckResponse
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.base.gpsfetcher.GpsHandlerCallback
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class LocationCheckHandler(
    private val activity: BaseActivity,
    private val userId: String?,
    private val showLoader: Boolean,
    private val callback: OnStringFetched
) {
    val apiManager = ApiManager(activity)

    init {
        val lastUpdateTime =
            FlatShareApplication.getDbInstance()?.userDao()
                ?.getLong(UserDao.USER_CONSTANT_LAST_UPDATED_LOCATION)
        if (lastUpdateTime == null || lastUpdateTime == 0L) {
            // User location is updated but update time is wrong. We will recheck again
            getLocation()
        } else {
            val nowTime = System.currentTimeMillis()
            val waitTime = DateUtils.DAY_IN_MILLIS * 15
            if ((nowTime - lastUpdateTime) > waitTime) {
                // the last updated time has crossed the wait time. Need to recheck again
                getLocation()
            } else callback.onFetched("1")
        }
    }

    private fun getLocation() {
        if (showLoader)
            activity.apiManager.showProgress()
        activity.getLocation(activity, object : GpsHandlerCallback {
            override fun onLocationUpdate(
                latitude: Double, longitude: Double
            ) {
                DialogCustomProgress.hideProgress(activity);
                callback.onFetched("1")
//                checkCity(latitude, longitude)
            }

            override fun onLocationFailed() {
                DialogCustomProgress.hideProgress(activity);
                activity.finishAffinity()
            }
        })
    }

    private fun updateTimeInDB(nowTime: Long) {
        FlatShareApplication.getDbInstance().userDao()
            .insert(UserDao.USER_CONSTANT_LAST_UPDATED_LOCATION, nowTime)
    }

    private fun checkCity(activity: ComponentActivity, latitude: Double, longitude: Double) {
        val location = ModelLocation()
        location.loc.coordinates.add(latitude)
        location.loc.coordinates.add(longitude)
        WebserviceManager().getCityNameForLocation(
            activity,
            "" + latitude,
            "" + longitude,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: LocationCheckResponse? =
                        Gson().fromJson(response, LocationCheckResponse::class.java)
                    if (!AppConstants.isAppLive) {
                        location.name = "Admin Location"
                        updateUserWithLocation(location, true)
                    } else {
                        val userId = FlatShareApplication.getDbInstance().userDao().getUser()?.id
                        if (userId.isNullOrBlank()) {
                            val resp = response as LocationCheckResponse
                            if (!resp.data?.city.isNullOrBlank())
                                location.name = resp.data?.city!!
                            updateTimeInDB(0L)
                            updateUserWithLocation(location, resp.allowed!!)
                        } else {
                            if (ConfigConstants.locationBypassNumbers.contains(userId)
                            ) {
                                location.name = "Admin Location"
                                updateUserWithLocation(location, true)
                            } else {
                                val resp = response as LocationCheckResponse
                                if (!resp.data?.city.isNullOrBlank())
                                    location.name = resp.data?.city!!
                                updateTimeInDB(0L)
                                updateUserWithLocation(location, resp.allowed!!)
                            }
                        }
                    }
                }
            })
    }

    private fun updateUserWithLocation(location: ModelLocation, isAppAvailableInCity: Boolean) {
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        if (user == null || user.name?.firstName.isNullOrBlank()) {
            // User is not registered
            if (isAppAvailableInCity) {
                activity.temporaryLocation = location
                callback.onFetched("1")
            } else {
                Logger.log(
                    "${userId}_Location",
                    "App not available in city ${location.name} at ${location.loc.coordinates[0]}," +
                            "${location.loc.coordinates[1]} for user ID $userId",
                    Logger.LOG_TYPE_LOCATION, userId
                )
                val intent = Intent(activity, LocationCheckActivity::class.java)
                CommonMethod.switchActivity(activity, intent, true)
            }
        } else {
            user.location = location
            activity.baseApiController.updateUser(
                showLoader,
                user,
                object :
                    OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        if (isAppAvailableInCity) {
                            val nowTime = System.currentTimeMillis()
                            updateTimeInDB(nowTime)
                            callback.onFetched("1")
                        } else {
                            Logger.log(
                                "${userId}_Location",
                                "App not available in city ${location.name} at ${location.loc.coordinates[0]}," +
                                        "${location.loc.coordinates[1]} for user ID $userId",
                                Logger.LOG_TYPE_LOCATION
                            )
                            val intent = Intent(activity, LocationCheckActivity::class.java)
                            CommonMethod.switchActivity(activity, intent, true)
                        }
                    }
                })
        }
        MixpanelUtils.locationChecked(
            location.loc.coordinates[0], location.loc.coordinates[1],
            if (location.name.isNullOrBlank()) "" else location.name, isAppAvailableInCity
        )
    }

}