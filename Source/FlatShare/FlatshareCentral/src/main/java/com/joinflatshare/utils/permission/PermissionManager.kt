package com.joinflatshare.utils.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod

class PermissionManager : ApplicationBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermission()
    }

    private fun setupPermission() {
        val permissionName = intent.getStringExtra("permissionName")!!
        val requestCode = intent.getIntExtra("requestCode", 0)
        PermissionUtil.checkPermission(this, permissionName, object : PermissionCallbackListener {
            override fun onPermissionGranted() {
                setResult(Activity.RESULT_OK)
                CommonMethod.finishActivity(this@PermissionManager)
            }

            override fun onPermissionDisabled() {
                var permName = ""
                when (permissionName) {
                    Manifest.permission.READ_EXTERNAL_STORAGE ->
                        permName = "Storage"
                    Manifest.permission.READ_CONTACTS ->
                        permName = "Contacts"
                    Manifest.permission.ACCESS_FINE_LOCATION ->
                        permName = "Location"
                    Manifest.permission.CAMERA ->
                        permName = "Camera"
                }
                AlertDialog.showAlert(
                    this@PermissionManager, "Enable Permission",
                    "Looks like your $permName permission is restricted.",
                    "Enable", "Later"
                ) { _, requestCode ->
                    setResult(Activity.RESULT_CANCELED)
                    CommonMethod.finishActivity(this@PermissionManager)
                    if (requestCode == 1) {
                        val i = Intent()
                        i.action = ACTION_APPLICATION_DETAILS_SETTINGS
                        i.addCategory(Intent.CATEGORY_DEFAULT)
                        i.data = Uri.parse("package:$packageName")
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        Handler(Looper.myLooper()!!).postDelayed({
                            startActivity(i)
                        }, 500)

                    }
                }
            }

            override fun onPermissionPreviouslyDenied() {
                requestPermissions(arrayOf(permissionName), requestCode)
            }

            override fun onPermissionAsk() {
                requestPermissions(arrayOf(permissionName), requestCode)
            }

        })
    }

    companion object {
        fun getPermissionIntent(
            activity: ApplicationBaseActivity,
            permissionName: String, requestCode: Int
        ): Intent {
            val intent = Intent(activity, PermissionManager::class.java)
            intent.putExtra("permissionName", permissionName)
            intent.putExtra("requestCode", requestCode)
            return intent
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val requestedCode = intent.getIntExtra("requestCode", 0)
        if (requestCode == requestedCode
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            setResult(Activity.RESULT_OK)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        CommonMethod.finishActivity(this)
    }
}