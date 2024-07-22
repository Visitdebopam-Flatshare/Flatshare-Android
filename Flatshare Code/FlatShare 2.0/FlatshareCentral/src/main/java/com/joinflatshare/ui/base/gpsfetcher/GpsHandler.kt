package com.joinflatshare.ui.base.gpsfetcher

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes.RESOLUTION_REQUIRED
import com.google.android.gms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.permission.PermissionUtil

/**
 * Created by debopam on 29/10/22
 */
open class GpsHandler : ApplicationBaseActivity() {
    private val TAG = GpsHandler::class.java.simpleName
    private val INTERVAL: Long = 500
    var temporaryLocation: ModelLocation? = null

    var hasMovedToSettings = false
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var activityCallBack: GpsHandlerCallback? = null
    private var activity: ApplicationBaseActivity? = null


    override fun onResume() {
        super.onResume()
        if (hasMovedToSettings) {
            hasMovedToSettings = false
            if (activity != null)
                getLocation(activity!!, activityCallBack)
        }
    }

    fun getLocation(activity: ApplicationBaseActivity, activityCallBack: GpsHandlerCallback?) {
        if (AppConstants.isAppLive) {
            this.activityCallBack = activityCallBack
            val googleApiAvailability =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
            if (googleApiAvailability == ConnectionResult.SUCCESS) {
                checkPermission(activity)
            } else {
                val message = "Google play services not available"
                CommonMethod.makeLog(TAG, message)
                Logger.log("$TAG $message", Logger.LOG_TYPE_ERROR)
                MixpanelUtils.locationFailed(message)
                activityCallBack?.onLocationFailed()
            }
        } else {
            val fakeLat = 26.7251165
            val fakeLng = 88.4393758
            CommonMethod.makeLog(
                TAG,
                "Location: $fakeLat $fakeLng"
            )
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_CONSTANT_LOCATION_LATITUDE, fakeLat)
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_CONSTANT_LOCATION_LONGITUDE, fakeLng)
            FlatShareApplication.getDbInstance().userDao().insert(
                UserDao.USER_CONSTANT_LAST_FETCHED_LOCATION,
                System.currentTimeMillis()
            )
            activityCallBack?.onLocationUpdate(fakeLat, fakeLng)
        }
    }

    private fun checkPermission(activity: ApplicationBaseActivity) {
        PermissionUtil.validatePermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION,
            object : OnPermissionCallback {
                override fun onCallback(granted: Boolean) {
                    if (granted) {
                        buildLocationRequest()
                    } else {
                        val message = "Location permission is not provided"
                        Logger.log(message, Logger.LOG_TYPE_PERMISSION)
                        MixpanelUtils.locationFailed(message)
                        AlertDialog.showAlert(this@GpsHandler,
                            "Error",
                            "Looks like your location permission is restricted",
                            "Enable",
                            "Later",
                            OnUiEventClick { _: Intent?, requestCode: Int ->
                                run {
                                    if (requestCode == 1) {
                                        val i = Intent()
                                        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        i.addCategory(Intent.CATEGORY_DEFAULT)
                                        i.data = Uri.parse("package:$packageName")
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                        startActivity(i)
                                        hasMovedToSettings = true
                                    } else {
                                        val message = "Location Permission not granted"
                                        CommonMethod.makeLog(TAG, message)
                                        Logger.log("$TAG $message", Logger.LOG_TYPE_ERROR)
                                        activityCallBack?.onLocationFailed()
                                    }
                                }
                            })
                    }
                }

            })
    }

    /*private fun checkLastFetchedLocationTime() {
        val lastLocationFetchedTime = FlatShareApplication.getDbInstance()?.userDao()
            ?.getLong(UserDao.USER_CONSTANT_LAST_FETCHED_LOCATION)
        if (lastLocationFetchedTime == null || lastLocationFetchedTime == 0L) {
            CommonMethods.makeLog(TAG, "Database has no records on past location")
            buildLocationRequest()
        } else {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastLocationFetchedTime) > (DateUtils.HOUR_IN_MILLIS * 6)) {
                CommonMethods.makeLog(TAG, "Last location fetched is more than 6 hours")
                buildLocationRequest()
            } else {
                val latitude = FlatShareApplication.getDbInstance()?.userDao()
                    ?.getDouble(UserDao.USER_CONSTANT_LOCATION_LATITUDE)
                val longitude = FlatShareApplication.getDbInstance()?.userDao()
                    ?.getDouble(UserDao.USER_CONSTANT_LOCATION_LONGITUDE)
                if (latitude == null || longitude == null) {
                    CommonMethods.makeLog(TAG, "Database does not have lat and long stored")
                    buildLocationRequest()
                } else {
                    CommonMethods.makeLog(TAG, "Location: $latitude, $longitude")
                    activityCallBack?.onLocationUpdate(latitude, longitude)
                }
            }
        }
    }*/

    private fun buildLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL)
            .setIntervalMillis(INTERVAL).setMaxUpdates(1).build()
        stopLocationUpdates()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@GpsHandler)
        checkDeviceSettings()
    }

    private fun checkDeviceSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            initCallback()
        }.addOnFailureListener {
            CommonMethod.makeLog(TAG, "GPS facing exception on failure. ${it.message}")
            if (it is ApiException) {
                when (it.statusCode) {
                    SETTINGS_CHANGE_UNAVAILABLE -> {
                        val message = "Device location cannot be turned on"
                        CommonMethod.makeLog(TAG, message)
                        Logger.log("$TAG $message", Logger.LOG_TYPE_ERROR)
                        MixpanelUtils.locationFailed(message)
                        activityCallBack?.onLocationFailed()
                        CommonMethod.makeLog(
                            TAG,
                            "GPS could not be turned on. Settings unavailable"
                        )
                    }

                    RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = ResolvableApiException(it.status);
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(resolvable.resolution).build()
                            resolutionForResult.launch(intentSenderRequest)
                            CommonMethod.makeLog(TAG, "Launching resolution to turn on GPS")
                        } catch (sendEx: Exception) {
                            // Ignore the error.
                            val message =
                                "Device location cannot be turned on. Exception - ${sendEx.message}"
                            CommonMethod.makeLog(TAG, message)
                            Logger.log("$TAG $message", Logger.LOG_TYPE_ERROR)
                            MixpanelUtils.locationFailed(message)
                            activityCallBack?.onLocationFailed()
                        }
                    }
                }
            }
        }
    }

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK)
            initCallback()
        else {
            val message = "Device location cannot be turned on"
            CommonMethod.makeLog(TAG, message)
            Logger.log("$TAG $message", Logger.LOG_TYPE_ERROR)
            MixpanelUtils.locationFailed(message)
            activityCallBack?.onLocationFailed()
        }
    }


    @SuppressLint("MissingPermission")
    private fun initCallback() {
        CommonMethod.makeLog(TAG, "Waiting for GPS callback")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    CommonMethod.makeLog(
                        TAG,
                        "Location: " + location.latitude + " " + location.longitude
                    )
                    FlatShareApplication.getDbInstance().userDao()
                        .insert(UserDao.USER_CONSTANT_LOCATION_LATITUDE, location.latitude)
                    FlatShareApplication.getDbInstance().userDao()
                        .insert(UserDao.USER_CONSTANT_LOCATION_LONGITUDE, location.longitude)
                    FlatShareApplication.getDbInstance().userDao().insert(
                        UserDao.USER_CONSTANT_LAST_FETCHED_LOCATION,
                        System.currentTimeMillis()
                    )
                    val intent =
                        Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_USER_LOCATION_UPDATED)
                    LocalBroadcastManager.getInstance(this@GpsHandler).sendBroadcast(intent)
                    activityCallBack?.onLocationUpdate(location.latitude, location.longitude)
                }
                stopLocationUpdates()
            }
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            CommonMethod.makeLog(TAG, "Location Updates Turned off")
            fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
            locationCallback = null
            fusedLocationClient!!.flushLocations()
            fusedLocationClient = null
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
}