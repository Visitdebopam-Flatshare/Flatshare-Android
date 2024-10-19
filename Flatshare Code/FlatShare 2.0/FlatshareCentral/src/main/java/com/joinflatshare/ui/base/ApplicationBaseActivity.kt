package com.joinflatshare.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ConfigConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.ui.SplashActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.system.ConnectivityListener
import com.joinflatshare.utils.system.GlobalActivityResult
import com.joinflatshare.utils.system.Prefs

open class ApplicationBaseActivity : AppCompatActivity() {
    private var dialog: androidx.appcompat.app.AlertDialog? = null

    @kotlin.jvm.JvmField
    val prefs: Prefs = Prefs.getInstance()

    // Global Activity result
    @kotlin.jvm.JvmField
    val activityLauncher = GlobalActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            if (AppConstants.loggedInUser != null &&
                !ConfigConstants.locationBypassNumbers.contains(AppConstants.loggedInUser!!.id)
            ) {
                try {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } catch (ex: Exception) {
                    Logger.log("Failed to block screenshot", Logger.LOG_TYPE_ERROR)
                }
            }

        }
        handleBackClick()
    }

    override fun onResume() {
        super.onResume()
        if (!AppConstants.hasNetworkConnection) {
            if (ConnectivityListener.isNetworkOnline())
                reloadActivity()
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            internetBroadcastReceiver,
            IntentFilter(IntentFilterConstants.INTENT_FILTER_INTERNET)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(internetBroadcastReceiver)
    }

    private fun handleBackClick() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    CommonMethod.finishActivity(this@ApplicationBaseActivity)
                }
            })
    }

    // Internet Connectivity Work
    private val internetBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val hasNetwork = intent?.getBooleanExtra("hasInternet", true)!!
            if (hasNetwork) {
                reloadActivity()
            } else {
                AppConstants.hasNetworkConnection = false
                AlertImageDialog.noInternet(this@ApplicationBaseActivity)
            }
        }
    }

    private fun reloadActivity() {
        if (!AppConstants.hasNetworkConnection) {
            AppConstants.hasNetworkConnection = true
            if (dialog != null && dialog!!.isShowing)
                dialog!!.dismiss()
            finish();
            if (intent == null)
                intent = Intent(this, SplashActivity::class.java)
            startActivity(intent);
            overridePendingTransition(0, 0);

        }
    }

}