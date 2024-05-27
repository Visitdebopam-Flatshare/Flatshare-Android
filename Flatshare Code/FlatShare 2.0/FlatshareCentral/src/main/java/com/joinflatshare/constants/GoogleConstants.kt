package com.joinflatshare.constants

import androidx.activity.ComponentActivity
import com.debopam.progressdialog.DialogCustomProgress
import com.google.android.libraries.places.api.Places
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.firestore.DbGoogleRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.system.ConnectivityListener

object GoogleConstants {
    var GOOGLE_API_KEY = ""
    var LATEST_APP_VERSION_CODE: Double? = 0.0
    var APP_UPGRADE_TITLE: String? = ""
    var APP_UPGRADE_MESSAGE: String? = ""

    fun initialiseGoogleSdk(activity: ComponentActivity?, callback: OnStringFetched) {
        if (ConnectivityListener.checkInternet()) {
            if (GOOGLE_API_KEY.isEmpty()) {
                if (activity != null)
                    DialogCustomProgress.showProgress(activity)
                DbGoogleRetriever().getGoogleApiKey {
                    if (activity != null)
                        DialogCustomProgress.hideProgress(activity)
                    if (it.equals("1")) {
                        if (!Places.isInitialized()) Places.initialize(
                            FlatShareApplication.instance,
                            GOOGLE_API_KEY
                        )
                        callback.onFetched("1")
                    } else {
                        callback.onFetched("0")
                    }
                }
            } else {
                if (!Places.isInitialized()) Places.initialize(
                    FlatShareApplication.instance,
                    GOOGLE_API_KEY
                )
                callback.onFetched("1")
            }
        }
    }
}