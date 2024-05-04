package com.joinflatshare.fcm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_NOTIFICATION
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.permission.PermissionManager
import com.joinflatshare.utils.system.GlobalActivityResult

class NotificationPermissionHandler(private val activity: ApplicationBaseActivity) {
    fun showNotificationPermission(onStringFetched: OnStringFetched) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                onStringFetched.onFetched("1")
            else {
                val intent = PermissionManager.getPermissionIntent(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS, REQUEST_CODE_NOTIFICATION
                )
                var activityLauncher: GlobalActivityResult<Intent, ActivityResult>? = null
                if (activity is RegisterBaseActivity) {
                    activityLauncher = activity.activityLauncher
                } else if (activity is BaseActivity) {
                    activityLauncher = activity.activityLauncher
                }
                activityLauncher?.launch(
                    intent,
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        onStringFetched.onFetched("1")
                    } else onStringFetched.onFetched("0")
                }
            }
        } else onStringFetched.onFetched("1")
    }
}