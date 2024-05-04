package com.joinflatshare

import android.app.Application
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieConfig
import com.debopam.retrofit.RetrofitHandler
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.api.retrofit.IWebserviceImpl
import com.joinflatshare.db.FlatshareDbManager
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.logger.Logger.log
import com.joinflatshare.utils.system.ConnectivityListener.initializeNetworkCallback
import com.joinflatshare.utils.system.Prefs
import com.joinflatshare.webservice.api.WebserviceManager
import java.io.File
import java.io.IOException

/**
 * Created by debopam on 01/02/24
 */
class FlatShareApplication : Application() {
    companion object {
        lateinit var instance: FlatShareApplication

        fun getDbInstance(): FlatshareDbManager {
            return FlatshareDbManager.getInstance(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Prefs.init(this)
        initializeNetworkCallback()
        loadLottie()
        initializeInterfaces()
        initializeLibraries()
    }

    private fun loadLottie() {
        try {
            val file = File(instance.externalCacheDir.toString() + "/" + "Lottie")
            if (!file.exists()) file.createNewFile()
            val config = LottieConfig.Builder().setEnableSystraceMarkers(true)
                .setNetworkCacheDir(file).build()
            Lottie.initialize(config)
        } catch (exception: IOException) {
            log("Failed to create Lottie cache folder", Logger.LOG_TYPE_VERBOSE)
        }
    }

    private fun initializeInterfaces() {
        WebserviceManager.uiWebserviceHandler = IWebserviceImpl()
    }

    private fun initializeLibraries() {
        // Retrofit
        val handler = RetrofitHandler()
        handler.init(BuildConfig.DOMAIN, instance)
        handler.printRequestBody(true)
        handler.useUnsafeHttp(false)
    }
}