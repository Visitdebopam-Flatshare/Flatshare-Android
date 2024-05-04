package com.joinflatshare.utils.system

import android.content.Intent
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.config.Cdn
import com.joinflatshare.pojo.config.ConfigResponse
import com.joinflatshare.ui.SplashActivity
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.webservice.api.ApiManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

class ConfigManager(private val activity: ApplicationBaseActivity) {

    fun fetchConfigData(callback: OnStringFetched?) {
        if (AppData.flatData == null) {
            val observable = ApiManager.getApiInterface().getConfig()
            ApiManager().callApiWithoutProgress(activity, observable, object :
                OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: ConfigResponse? =
                        Gson().fromJson(response, ConfigResponse::class.java)
                    if (resp == null) {
                        AlertDialog.showAlert(
                            activity,
                            "We could not retrieve some information to run flatshare.",
                            "Restart"
                        ) { _, _ ->
                            val intent = Intent(activity, SplashActivity::class.java)
                            CommonMethod.switchActivity(activity, intent, false)
                            activity.finishAffinity()
                        }
                        return
                    }
                    FlatShareApplication.getDbInstance().appDao().insertConfigResponse(resp)

                    assignAwsBaseUrl(resp.data?.cdn)
                    if (resp.data?.flatData != null) {
                        AppData.flatData = resp.data!!.flatData
                        callback?.onFetched("1")
                    } else callback?.onFetched("0")
                }
            })
        } else callback?.onFetched("1")
    }

    private fun assignAwsBaseUrl(cdn: Cdn?) {
        var configImageUrl = ""
        if (cdn != null) {
            configImageUrl =
                if (BuildConfig.FLAVOR == "ProServerDevAws" || BuildConfig.FLAVOR == "ProServerProAws") {
                    cdn.pro
                } else {
                    cdn.dev
                }
        }
        val dbImageUrl = FlatShareApplication.getDbInstance().appDao().get(AppDao.AMAZON_BASE_URL)
        if (dbImageUrl == null || dbImageUrl != configImageUrl) {
            FlatShareApplication.getDbInstance().appDao().delete(AppDao.AMAZON_S3_BUCKET_NAME)
            FlatShareApplication.getDbInstance().appDao()
                .insert(
                    AppDao.AMAZON_BASE_URL,
                    configImageUrl
                )
        }
    }

    fun hasAppAccess(): Boolean {
        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        return (response != null && response.data?.appAccess == true)
    }

    fun isUserBlocked(id: String): Boolean {
        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        return (response == null || response.data?.userAccessDenied?.contains(id) == true)
    }
}