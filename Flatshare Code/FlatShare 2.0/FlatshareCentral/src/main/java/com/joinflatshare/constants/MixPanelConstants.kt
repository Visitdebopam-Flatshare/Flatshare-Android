package com.joinflatshare.constants

import com.joinflatshare.firestore.DbMixpanelRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.system.ConnectivityListener

object MixPanelConstants {
    var MIXPANEL_TOKEN = ""

    fun initialiseMixPanelToken(callback: OnStringFetched) {
        if (ConnectivityListener.checkInternet()) {
            if (MIXPANEL_TOKEN.isEmpty()) {
                DbMixpanelRetriever().getMixpanelApiKey { text ->
                    if (text.equals("1")) {
                        callback.onFetched("1")
                    } else {
                        callback.onFetched("0")
                    }
                }
            } else {
                callback.onFetched("1")
            }
        }
    }
}