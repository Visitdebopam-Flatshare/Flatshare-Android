package com.joinflatshare.utils.helper

import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import com.google.firebase.messaging.FirebaseMessaging
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.chat.SendBirdConnectionManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.SplashActivity
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.GlobalActivityResult.OnActivityResult
import java.io.Serializable
import java.util.Calendar
import java.util.regex.Pattern

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

    fun makeToast(message: String?) {
        Toast.makeText(FlatShareApplication.instance, message, Toast.LENGTH_SHORT).show()
    }

    fun showHtmlInTextView(textView: TextView, text: String) {
        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    @Suppress("deprecation")
    fun <T : Serializable?> getSerializable(intent: Intent, name: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(name, clazz)!!
        } else {
            intent.getSerializableExtra(name) as T
        }
    }

    fun getAge(dob: String?): String {
        if (!dob.isNullOrEmpty()) {
            val bYear = dob.split(Pattern.quote("/").toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt()
            val cYear = Calendar.getInstance()[Calendar.YEAR]
            val bMonth = dob.split(Pattern.quote("/").toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt()
            val cMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            return if (bMonth > cMonth) ("" + (cYear - bYear - 1)) else "" + (cYear - bYear)
        }
        return ""
    }

    fun getUserDP(user: User?): String {
        return if (!user?.dp.isNullOrEmpty())
            user?.dp!!
        else {
            val fname = user?.name?.firstName!![0]
            val lname = user.name?.lastName!![0]
            "" + fname + lname
        }
    }

    fun isDealBreakerEmpty(breaker: DealBreakers?): Boolean {
        if (breaker == null)
            return true
        if (breaker.eggs == 0 && breaker.pets == 0 && breaker.nonveg == 0 &&
            breaker.smoking == 0 && breaker.workout == 0 && breaker.flatparty == 0)
            return true
        return false
    }

}