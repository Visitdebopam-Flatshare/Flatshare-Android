package com.joinflatshare.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivitySplashBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.fcm.NotificationPermissionHandler
import com.joinflatshare.firestore.DbAppAccessRetriever
import com.joinflatshare.firestore.DbAwsRetriever
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.profile.create.ProfileCreateActivity
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.ui.register.login.LoginActivity
import com.joinflatshare.ui.register.photo.RegisterPhotoActivity
import com.joinflatshare.utils.amazonaws.AmazonFileChecker
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.appupdater.AppUpdater
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.ConfigManager
import com.joinflatshare.utils.system.ConnectivityListener
import com.joinflatshare.utils.system.DeviceInformationCollector
import com.joinflatshare.utils.system.Prefs
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class SplashActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivitySplashBinding
    private lateinit var configManager: ConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        MixpanelUtils.setupToken()
        configManager = ConfigManager(this)
        printAppVersion()
//        checkProductionUser()
//        ImageHelper.clearImageCache(this)
//        val appSignatures = SmsRetriever(this).appSignatures
//        for (text in appSignatures) {
//            CommonMethods.makeLog("Signature", text)
//        }
    }

    private fun printAppVersion() {
        if (BuildConfig.DEBUG) {
            viewBind.txtAppVersion.visibility = View.VISIBLE
            viewBind.txtAppVersion.text = "App Version ${BuildConfig.VERSION_NAME}"
        }
    }

    private fun checkConfig() {
        configManager.fetchConfigData { text ->
            if (text.equals("0")) {
                AlertImageDialog.somethingWentWrong(this)
            } else {
                if (configManager.hasAppAccess()) {
                    checkAppAccessFromFirebase()
                } else {
                    AlertDialog.showAlert(
                        this@SplashActivity,
                        "${resources.getString(R.string.app_name)} is not well. We are working on it. Please check again after sometime.",
                        "OK"
                    ) { _, _ -> finishAffinity() }
                }
            }
        }
    }


    private fun checkAppAccessFromFirebase() {
        DbAppAccessRetriever().getAccessStatus { text ->
            if (text == "1")
                getAwsKeys()
            else AlertDialog.showAlert(
                this@SplashActivity, text, "OK"
            ) { _, requestCode ->
                if (requestCode == 1)
                    finishAffinity()
            }
        }
    }


    private fun getAwsKeys() {
        DbAwsRetriever().getAwsKeys() { text ->
            if (text.equals("1"))
                getToken()
            else AlertImageDialog.somethingWentWrong(this@SplashActivity)
        }
    }

    private fun getToken() {
        val firebaseToken =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.FIREBASE_TOKEN)
        if (firebaseToken.isNullOrBlank()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    AlertImageDialog.somethingWentWrong(this)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                FlatShareApplication.getDbInstance().appDao().insert(AppDao.FIREBASE_TOKEN, token)
                CommonMethods.makeLog("FCM", token)
                migratePrefs(token)
            })
        } else {
            CommonMethods.makeLog("FCM", firebaseToken)
            migratePrefs(firebaseToken)
        }
    }

    private fun migratePrefs(firebaseToken: String) {
        val prefsApiToken = Prefs.getInstance().getString(Prefs.PREFS_KEY_API_TOKEN)
        if (prefsApiToken.isNotBlank()) {
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_KEY_API_TOKEN, prefsApiToken)
            Prefs.getInstance().clearPreferenceKey(Prefs.PREFS_KEY_API_TOKEN)
        } else {
            val newDbApiToken =
                FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_KEY_API_TOKEN)
            if (newDbApiToken.isNullOrBlank())
                FlatShareApplication.getDbInstance().userDao().delete(UserDao.USER_CONSTANT_USERID)
        }
        switchActivity(firebaseToken)
    }

    private fun switchActivity(firebaseToken: String) {
        val userId =
            FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID)
        if (userId.isNullOrBlank()) {
            val intent = Intent(this, LoginActivity::class.java)
            CommonMethod.switchActivity(this, intent, true)
        } else {
            if (configManager.isUserBlocked(userId)) {
                AlertDialog.showAlert(
                    this,
                    "Your account is restricted. If this looks like a mistake, please reach out to us at\nhello@flatshare.club.",
                    "OK"
                ) { _, requestCode ->
                    run {
                        if (requestCode == 1) {
                            GlobalScope.launch {
                                CommonMethod.logout(this@SplashActivity)
                            }
                        }
                    }
                }
            } else {
                WebserviceManager().getProfile(false, this, userId,
                    object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                        override fun onResponseCallBack(response: String) {
                            val resp: UserResponse? =
                                Gson().fromJson(response, UserResponse::class.java)
                            val userData = resp?.data
                            if (userData != null) {
                                CommonMethods.registerUser(resp)
                                var intent: Intent
                                if (!userData.name?.firstName.isNullOrBlank()) {
                                    // Requesting Notification permission for Android 13
                                    NotificationPermissionHandler(this@SplashActivity).showNotificationPermission {
                                        DeviceInformationCollector()
                                        // Check if firebase token matches with API user's token
                                        if (!userData.deviceToken.equals(firebaseToken)) {
                                            FlatShareApplication.getDbInstance().userDao()
                                                .insert(UserDao.USER_NEED_FCM_UPDATE, "1")
                                        }
                                        // Checking if user has DP
                                        hasUserDP {
                                            if (it) {
                                                intent =
                                                    Intent(
                                                        this@SplashActivity,
                                                        ExploreActivity::class.java
                                                    )
                                                checkDeepLink(intent)
                                            } else {
                                                intent = Intent(
                                                    this@SplashActivity,
                                                    RegisterPhotoActivity::class.java
                                                )
                                                CommonMethod.switchActivity(
                                                    this@SplashActivity,
                                                    intent,
                                                    true
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    intent = Intent(
                                        this@SplashActivity,
                                        ProfileCreateActivity::class.java
                                    )
                                    CommonMethod.switchActivity(this@SplashActivity, intent, true)
                                }
                            }
                        }
                    })
            }
        }
    }


    private fun hasUserDP(callback: (Boolean) -> Unit) {
        val hasUserDpInDB = FlatShareApplication.getDbInstance().userDao().get(UserDao.HAS_USER_DP)
        if (hasUserDpInDB.isNullOrBlank()) {
            val user = FlatShareApplication.getDbInstance().userDao().getUser()!!
            val url = user.dp
            AmazonFileChecker(this).checkObject(
                url
            ) { _, requestCode ->
                if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                    FlatShareApplication.getDbInstance().userDao().insert(UserDao.HAS_USER_DP, "1")
                } else {
                    FlatShareApplication.getDbInstance().userDao().insert(UserDao.HAS_USER_DP, "0")
                }
                callback(requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS)
            }
        } else {
            callback(hasUserDpInDB.equals("1"))
        }
    }

    private fun checkDeepLink(mainIntent: Intent) {
        val data = intent.data
        if (data != null && !data.host.isNullOrEmpty()) {
            val path = data.path
            if (!path.isNullOrEmpty()) {
                CommonMethods.makeLog("path", path)
                DeepLinkHandler.getContentFromLink(
                    this, intent
                ) { text ->
                    if (!text.isNullOrBlank() || !text.equals("0")) {
                        CommonMethods.makeLog("dynamic link", text)
                        mainIntent.putExtra("deepLink", true)
                        mainIntent.putExtra("param1", text)
                        CommonMethod.switchActivity(this, mainIntent, true)
                    } else CommonMethod.switchActivity(this, mainIntent, true)
                }

            } else
                CommonMethod.switchActivity(this, mainIntent, true)
        } else checkNotification(mainIntent)
    }

    private fun checkNotification(mainIntent: Intent) {
        val data = intent.extras
        if (data != null && data.containsKey("type")) {
            mainIntent.putExtra("type", data.getString("type"))
            mainIntent.putExtra("notification", true)
            if (data.containsKey("flatId"))
                mainIntent.putExtra("flatId", data.getString("flatId"))
            if (data.containsKey("userId"))
                mainIntent.putExtra("userId", data.getString("userId"))
        }
        CommonMethod.switchActivity(this, mainIntent, true)
    }

    override fun onResume() {
        super.onResume()
        if (ConnectivityListener.checkInternet()) {
            AppConstants.hasNetworkConnection = true
            if (BuildConfig.DEBUG || !AppConstants.isAppLive)
                checkConfig()
            else
                AppUpdater(this).checkStore { text ->
                    if (text.equals("1"))
                        checkConfig()
                }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodeConstants.REQUEST_CODE_UPDATE_APP) {
            if (resultCode != RESULT_OK) {
                finishAffinity()
            }
        }
    }

    private fun checkProductionUser() {
        if (!AppConstants.isAppLive && BuildConfig.FLAVOR == "ProServerProAws"
            && BuildConfig.DEBUG
        ) {
            FlatShareApplication.getDbInstance().userDao()
                .insert(UserDao.USER_CONSTANT_USERID, "9263471358")
            prefs.setString(
                Prefs.PREFS_KEY_API_TOKEN,
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjgxNjk1MzM5MjkiLCJpYXQiOjE2ODI3Nzg1MDUsImV4cCI6MTY4Nzk2MjUwNX0.jItL1wVUOcfgb3Aw8WNOdBL5yRjf5fcLkzGtPRTqfSA"
            )
        }
    }
}