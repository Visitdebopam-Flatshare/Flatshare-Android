package com.joinflatshare.utils.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.interfaces.OnPermissionCallback
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod

object PermissionUtil {
    private val permissionCodeMap: HashMap<String, Int> = HashMap()

    init {
        permissionCodeMap[Manifest.permission.READ_EXTERNAL_STORAGE] =
            RequestCodeConstants.REQUEST_CODE_STORAGE
        permissionCodeMap[Manifest.permission.READ_CONTACTS] =
            RequestCodeConstants.REQUEST_CODE_CONTACT
        permissionCodeMap[Manifest.permission.ACCESS_FINE_LOCATION] =
            RequestCodeConstants.REQUEST_CODE_LOCATION
        permissionCodeMap[Manifest.permission.CAMERA] =
            RequestCodeConstants.REQUEST_CODE_CAMERA
    }

    /*
    * Check if version is marshmallow and above.
    * Used in deciding to ask runtime permission
    * */
    private fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun shouldAskPermission(activity: AppCompatActivity, permission: String): Boolean {
        if (shouldAskPermission()) {
            val permissionResult: Int = activity.checkSelfPermission(permission)
            return permissionResult != PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    internal fun checkPermission(
        activity: AppCompatActivity, permission: String, listener: PermissionCallbackListener
    ) {
        // Check if required permission is STORAGE permission and OS version is 13 and above
        // We do not require permission further to get media
        if (permission == Manifest.permission.READ_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listener.onPermissionGranted()
            return
        }

        /*
        * If permission is not granted
        * */
        if (shouldAskPermission(activity, permission)) {
            /*
            * If permission denied previously
            * */
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionPreviouslyDenied()
            } else {
                /*
                * Permission denied or first time requested
                * */
                if (PermissionPreferenceUtil.isFirstTimeAskingPermission(permission)) {
                    PermissionPreferenceUtil.firstTimeAskingPermission(permission, false)
                    listener.onPermissionAsk()
                } else {
                    /*
                    * Handle the feature without permission or ask user to manually allow permission
                    * */
                    listener.onPermissionDisabled()
                }
            }
        } else {
            listener.onPermissionGranted()
        }
    }

    fun validatePermission(
        activity: ApplicationBaseActivity, permission: String, callback: OnPermissionCallback
    ) {
        val intent = PermissionManager.getPermissionIntent(
            activity, permission, permissionCodeMap[permission]!!
        )
        CommonMethod.switchActivity(
            activity, intent
        ) { result ->
            callback.onCallback(result?.resultCode == Activity.RESULT_OK)
        }
    }
}