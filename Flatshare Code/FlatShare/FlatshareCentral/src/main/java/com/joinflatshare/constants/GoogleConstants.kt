package com.joinflatshare.constants

import com.google.android.libraries.places.api.Places
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.firestore.DbGoogleRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.system.ConnectivityListener

object GoogleConstants {
    var GOOGLE_API_KEY = ""
    var LATEST_APP_VERSION_CODE: Double? = 0.0
    var APP_UPGRADE_TITLE: String? = ""
    var APP_UPGRADE_MESSAGE: String? = ""

    fun initialiseGoogleSdk(activity: BaseActivity?, callback: OnStringFetched) {
        if (ConnectivityListener.checkInternet()) {
            if (GOOGLE_API_KEY.isEmpty()) {
                activity?.apiManager?.showProgress()
                DbGoogleRetriever().getGoogleApiKey {
                    activity?.apiManager?.hideProgress()
                    if (it.equals("1")) {
                        CommonMethod.makeLog("Google API Key", GOOGLE_API_KEY)
                        if (!Places.isInitialized()) Places.initializeWithNewPlacesApiEnabled(
                            FlatShareApplication.instance,
                            GOOGLE_API_KEY
                        )
                        callback.onFetched("1")
                    } else {
                        callback.onFetched("0")
                    }
                }
            } else {
                CommonMethod.makeLog("Google API Key", GOOGLE_API_KEY)
                if (!Places.isInitialized()) Places.initializeWithNewPlacesApiEnabled(
                    FlatShareApplication.instance,
                    GOOGLE_API_KEY
                )
                callback.onFetched("1")
            }
        }
    }
}