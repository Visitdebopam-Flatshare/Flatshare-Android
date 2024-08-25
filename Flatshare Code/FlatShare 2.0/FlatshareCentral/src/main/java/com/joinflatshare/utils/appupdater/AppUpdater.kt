package com.joinflatshare.utils.appupdater

import android.content.pm.PackageManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.GoogleConstants
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_UPDATE_APP
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.firestore.DbAppVersionRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.ApplicationBaseActivity


class AppUpdater(
    private val activity: ApplicationBaseActivity,
) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkStore(callback: OnStringFetched) {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            handleAppUpdateInfo(appUpdateManager, appUpdateInfo, callback)
        }.addOnFailureListener {
            callback.onFetched("1")
        }
    }

    private fun handleAppUpdateInfo(
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo?,
        callback: OnStringFetched
    ) {
        when (appUpdateInfo?.updateAvailability()) {
            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                // If an in-app update is already running, resume the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    IMMEDIATE,
                    activity,
                    REQUEST_CODE_UPDATE_APP
                )
            }

            UpdateAvailability.UPDATE_AVAILABLE -> {
                if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
                ) {
                    checkLatestVersion(appUpdateInfo, callback)
                } else callback.onFetched("1")
            }

            else -> {
                callback.onFetched("1")
            }
        }
    }

    private fun checkLatestVersion(
        appUpdateInfo: AppUpdateInfo,
        callback: OnStringFetched
    ) {
        DbAppVersionRetriever().getLatestVersion {
            var appVersionCode:Long
            try {
                appVersionCode =
                    activity.packageManager.getPackageInfo(activity.packageName, 0).longVersionCode
            } catch (exception: Exception) {
                appVersionCode = 0L
            }

            val latestVersionCode = GoogleConstants.LATEST_APP_VERSION_CODE
            if (latestVersionCode != null) {
                if (latestVersionCode > appVersionCode) {
                    AlertDialog.showAlert(
                        activity,
                        GoogleConstants.APP_UPGRADE_TITLE,
                        GoogleConstants.APP_UPGRADE_MESSAGE,
                        "Update Now", "Don't Update",
                    ) { _, requestCode ->
                        if (requestCode == 1)
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, IMMEDIATE,
                                activity, REQUEST_CODE_UPDATE_APP
                            )
                        else {
                            activity.finishAffinity()
                        }
                    }
                    return@getLatestVersion
                }
            }
            callback.onFetched("1")
        }
    }
}