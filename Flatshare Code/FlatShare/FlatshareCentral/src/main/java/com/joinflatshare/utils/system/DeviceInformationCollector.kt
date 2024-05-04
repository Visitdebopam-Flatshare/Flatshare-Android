package com.joinflatshare.utils.system

import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.GoogleConstants
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.db.tables.TableDeviceConfiguration
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

class DeviceInformationCollector {
    val TAG = DeviceInformationCollector::class.java.simpleName

    init {
        getConfiguration()
    }

    private fun getConfiguration() {
        val tb = TableDeviceConfiguration()
        tb.manufacturer = Build.MANUFACTURER
        tb.brand = Build.BRAND
        tb.model = Build.MODEL
        tb.screenResolution =
            "${CommonMethods.getScreenWidth()} * ${CommonMethods.getScreenHeight()} pixels"
        tb.osVersion = Build.VERSION.RELEASE
        tb.sdkVersion = Build.VERSION.SDK_INT
        tb.appVersionCode = BuildConfig.VERSION_CODE
        tb.appVersionName = BuildConfig.VERSION_NAME

        // User
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        if (user != null) {
            tb.userId = user.id
            if (user.name != null) {
                tb.firstName = user.name!!.firstName
                tb.lastName = user.name!!.lastName
            }
            tb.gender = user.gender
            tb.apiToken =
                FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_KEY_API_TOKEN)!!
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            tb.hasApprovedNotificationPermission = true
        } else {
            tb.hasApprovedNotificationPermission =
                FlatShareApplication.instance.checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        }
        getConfigurationFromDevice(tb)
        CommonMethod.makeLog(
            TAG, "${tb.manufacturer}-${tb.brand}-${tb.model}-" +
                    "${tb.screenResolution}-${tb.osVersion}-${tb.sdkVersion}-" +
                    "${tb.appVersionCode}-${tb.appVersionName}-${tb.userId}"
        )
    }

    private fun getConfigurationFromDevice(newConfiguration: TableDeviceConfiguration) {
        val oldConfiguration =
            FlatShareApplication.getDbInstance().deviceConfigurationDao().getConfiguration()
        if (oldConfiguration == null) {
            FlatShareApplication.getDbInstance().deviceConfigurationDao()
                .insert(newConfiguration)
            getKeys(newConfiguration)
        } else {
            var hasDbChanged = false
            if (!oldConfiguration.manufacturer.equals(newConfiguration.manufacturer)
                || !oldConfiguration.brand.equals(newConfiguration.brand)
                || !oldConfiguration.model.equals(newConfiguration.model)
                || !oldConfiguration.screenResolution.equals(newConfiguration.screenResolution)
                || !oldConfiguration.osVersion.equals(newConfiguration.osVersion)
                || oldConfiguration.hasApprovedNotificationPermission != newConfiguration.hasApprovedNotificationPermission
                || oldConfiguration.sdkVersion != newConfiguration.sdkVersion
                || oldConfiguration.appVersionCode != newConfiguration.appVersionCode
                || !oldConfiguration.appVersionName.equals(newConfiguration.appVersionName)
                || !oldConfiguration.userId.equals(newConfiguration.userId)
            )
                hasDbChanged = true
            if (hasDbChanged) {
                FlatShareApplication.getDbInstance().deviceConfigurationDao()
                    .insert(newConfiguration)
                getKeys(newConfiguration)
            }
        }
    }

    private fun getKeys(newConfiguration: TableDeviceConfiguration) {
        if (GoogleConstants.GOOGLE_API_KEY.isEmpty()) {
            GoogleConstants.initialiseGoogleSdk(
                null
            ) { text ->
                if (text.equals("1"))
                    updateToFirebaseDB(newConfiguration)
            }
        } else updateToFirebaseDB(newConfiguration)

    }

    private fun updateToFirebaseDB(newConfiguration: TableDeviceConfiguration) {
        val db = Firebase.firestore
        val docRef = db.collection("device_information").document(newConfiguration.userId)
        docRef.set(newConfiguration)
    }
}