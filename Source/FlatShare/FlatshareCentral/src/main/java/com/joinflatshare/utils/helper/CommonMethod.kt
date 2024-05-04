package com.joinflatshare.utils.helper

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import com.google.firebase.messaging.FirebaseMessaging
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.chat.SendBirdConnectionManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.ui.SplashActivity
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.GlobalActivityResult.OnActivityResult

/**
 * Created by debopam on 17/08/23
 */
object CommonMethod {
    fun logout(activity: ComponentActivity) {
        FirebaseMessaging.getInstance().deleteToken()
        if (AppConstants.isSendbirdLive && !SendBirdConstants.SENDBIRD_APPID.isEmpty()) SendBirdConnectionManager.disconnect()
        FlatShareApplication.getDbInstance().userDao().clearUserTable()
        FlatShareApplication.getDbInstance().requestDao().clearRequestTable()
        FlatShareApplication.getDbInstance().appDao().delete(AppDao.FIREBASE_TOKEN)
        FlatShareApplication.getDbInstance().deviceConfigurationDao().clearConfigurationTable()
        clearFlat()
        MixpanelUtils.logout()
        activity.finishAffinity()
        val intent = Intent(activity, SplashActivity::class.java)
        switchActivity(activity, intent, true)
    }

    fun clearFlat() {
        FlatShareApplication.getDbInstance().userDao().updateFlatResponse(null)
    }

    fun switchActivity(activity: ComponentActivity, intent: Intent?, finish: Boolean) {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        if (finish) finishActivity(activity)
    }

    fun finishActivity(activity: ComponentActivity) {
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun switchActivity(
        activity: ApplicationBaseActivity,
        intent: Intent?,
        onActivityResult: OnActivityResult<ActivityResult?>?
    ) {
        if (onActivityResult != null) {
            activity.activityLauncher.launch(intent, onActivityResult)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    fun makeLog(tag: String?, message: String?) {
        if (!AppConstants.isAppLive) {
            Log.e(tag, message!!)
        }
    }

}