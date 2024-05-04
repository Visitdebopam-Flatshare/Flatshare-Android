package com.joinflatshare.api.retrofit

import androidx.activity.ComponentActivity
import com.debopam.flatshareprogress.DialogCustomProgress
import com.debopam.retrofit.RetrofitHandler
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.system.ConnectivityListener
import com.joinflatshare.webservice.api.interfaces.IWebservice

/**
 * Created by debopam on 31/01/24
 */
class IWebserviceImpl : IWebservice {
    val TAG = IWebserviceImpl::class.java.simpleName

    override fun hasInternet(): Boolean {
        val hasInternet = ConnectivityListener.checkInternet()
        return if (hasInternet) {
            val apiToken =
                FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_KEY_API_TOKEN)
            val retrofitHandler = RetrofitHandler()
            if (apiToken.isNullOrBlank()) {
                if (retrofitHandler.getHeaderMap().isNotEmpty())
                    retrofitHandler.setHeaderMap(HashMap())
            } else {
                if (!retrofitHandler.getHeaderMap().containsKey("x-access-token")) {
                    val headerMap = HashMap<String, String>()
                    headerMap["x-access-token"] = apiToken
                    retrofitHandler.setHeaderMap(headerMap)
                }
            }
            true
        } else false
    }

    override fun showProgress(activity: ComponentActivity) {
        DialogCustomProgress.showProgress(activity)
    }

    override fun hideProgress(activity: ComponentActivity) {
        DialogCustomProgress.hideProgress(activity)
    }

    override fun onSomethingWentWrong(activity: ComponentActivity) {
        AlertImageDialog.somethingWentWrong(activity)
    }

    override fun onLogout(activity: ComponentActivity) {
        CommonMethod.logout(activity)
    }

    override fun onLogMessage(message: String?) {
        Logger.log(message, Logger.LOG_TYPE_API)
    }

    override fun onLogConsole(message: String) {
        CommonMethods.makeLog(TAG, message)
    }

    override fun onCallBackError(activity: ComponentActivity, message: String) {
        AlertDialog.showAlert(activity, message)
    }
}