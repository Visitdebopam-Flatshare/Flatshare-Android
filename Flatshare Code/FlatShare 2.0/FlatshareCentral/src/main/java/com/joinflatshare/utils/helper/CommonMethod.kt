package com.joinflatshare.utils.helper

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import com.debopam.userinfo.AddUserRequest
import com.debopam.userinfo.SendUserInfo
import com.google.firebase.messaging.FirebaseMessaging
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.chat.SendBirdConnectionManager
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.SplashActivity
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.GlobalActivityResult.OnActivityResult
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

/**
 * Created by debopam on 17/08/23
 */
object CommonMethod {
    fun logout(activity: ComponentActivity) {
        FirebaseMessaging.getInstance().deleteToken()
        if (AppConstants.isSendbirdLive && SendBirdConstants.SENDBIRD_APPID.isNotEmpty()) SendBirdConnectionManager.disconnect()
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
        try {
            if (intent != null) {
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                if (finish) finishActivity(activity)
            }
        } catch (exception: ActivityNotFoundException) {
            AlertDialog.showAlert(activity, "Sorry, we could not find an app to open this request.")
        }
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
        try {
            if (intent != null) {
                if (onActivityResult != null) {
                    activity.activityLauncher.launch(intent, onActivityResult)
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
        } catch (exception: ActivityNotFoundException) {
            AlertDialog.showAlert(activity, "Sorry, we could not find an app to open this request.")
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
        return if (!user?.dp.isNullOrEmpty()) user?.dp!!
        else {
            val fname = user?.name?.firstName!![0]
            val lname = user.name?.lastName!![0]
            "" + fname + lname
        }
    }

    fun isDealBreakerEmpty(breaker: DealBreakers?): Boolean {
        if (breaker == null) return true
        if ((breaker.eggs < 1 || breaker.eggs > 2) && (breaker.pets < 1 || breaker.pets > 2) && (breaker.flatparty < 1 || breaker.flatparty > 2) && (breaker.nonveg < 1 || breaker.nonveg > 2) && (breaker.smoking < 1 || breaker.smoking > 2) && (breaker.workout < 1 || breaker.workout > 2)) return true
        return false
    }

    fun isLocationEmpty(location: ModelLocation?): Boolean {
        if (location?.loc?.coordinates.isNullOrEmpty()) return true
        return false
    }

    fun isLocationEmpty(location: ArrayList<ModelLocation>?): Boolean {
        if (location.isNullOrEmpty()) return true
        return isLocationEmpty(location[0])
    }

    fun sendUserToDB(user: User) {
        if (AppConstants.isAppLive && TextUtils.equals(BuildConfig.FLAVOR, "ProServerProAws")) {
            val request = AddUserRequest()
            request.mobile = user.id
            request.name = user.name?.firstName + " " + user.name?.lastName
            request.sex = if (TextUtils.equals(user.gender, "Male")) "Male"
            else if (TextUtils.equals(user.gender, "Female")) "Female" else ""
            request.dob = user.dob
            request.source = 3
            SendUserInfo.addUserToDB(request)
        }
    }

    // TODO move this method ot baseactivity after rewriting in kotlin
    fun isEliteMember(user: User?): Boolean {
        var godMode = user?.godMode
        try {
            if (!godMode.isNullOrEmpty() && godMode.contains(".")) {
                godMode = godMode.substring(0, godMode.lastIndexOf("."))
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val currentTime = System.currentTimeMillis()
                val godModeExpireTime = sdf.parse(godMode)!!
                if (godModeExpireTime.time > currentTime) {
                    return true
                }
            }
            return false
        } catch (ex: Exception) {
            return false
        }
    }

}