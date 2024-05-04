package com.joinflatshare.utils.logger

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.mixpanel.MixpanelUtils

object Logger {
    const val LOG_TYPE_ERROR = "ERROR"
    const val LOG_TYPE_WARNING = "WARNING"
    const val LOG_TYPE_VERBOSE = "VERBOSE"
    const val LOG_TYPE_API = "API"
    const val LOG_TYPE_PERMISSION = "PERMISSION"
    const val LOG_TYPE_LOCATION = "LOCATION"
    const val LOG_TYPE_PAYMENT = "PAYMENT"

    @JvmStatic
    fun log(reason: String?, type: String) {
        if (!reason.isNullOrBlank())
            if (!BuildConfig.DEBUG && AppConstants.isAppLive) {
                val model = getModel()
                model.reason = reason
                model.type = type
                val db = Firebase.firestore
                db.collection("Logger").document().set(model)
                MixpanelUtils.logError(reason, type)
            }
    }

    fun log(document: String, reason: String, type: String) {
        if (!BuildConfig.DEBUG && AppConstants.isAppLive) {
            val model = getModel()
            model.reason = reason
            model.type = type
            val db = Firebase.firestore
            db.collection("Logger").document(document).set(model)
            MixpanelUtils.logError(reason, type)
        }
    }

    fun log(document: String, reason: String, type: String, userId: String?) {
        if (!BuildConfig.DEBUG && AppConstants.isAppLive) {
            val model = getModel()
            model.userId = userId
            model.reason = reason
            model.type = type
            val db = Firebase.firestore
            db.collection("Logger").document(document).set(model)
            MixpanelUtils.logError(reason, type)
        }
    }

    fun logPayment(reason: String, type: String) {
        if (!BuildConfig.DEBUG && AppConstants.isAppLive) {
            val model = getModel()
            model.userId = AppConstants.loggedInUser?.id
            model.reason = reason
            model.type = type
            val db = Firebase.firestore
            db.collection("Logger").document().set(model)
        }
    }

    private fun getModel(): ModelLogger {
        val model = ModelLogger()

        // User ID
        val userId =
            FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID)
        if (userId.isNullOrBlank())
            model.userId = "NA"
        else model.userId = userId

        // DATE
        model.timestamp = DateUtils.getLoggerDate()
        model.millis = System.currentTimeMillis()

        return model
    }

}