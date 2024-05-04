package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.MixPanelConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnStringFetched

class DbMixpanelRetriever {
    private val tableName = BuildConfig.MIXPANEL_TABLE

    fun getMixpanelApiKey(callback: OnStringFetched?) {
        val mixpanelToken =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.MIXPANEL_TOKEN)
        if (mixpanelToken.isNullOrBlank()) {
            val db = Firebase.firestore
            val docRef = db.collection("mixpanel").document(tableName)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        MixPanelConstants.MIXPANEL_TOKEN = document.getString("API_KEY")!!
                        FlatShareApplication.getDbInstance().appDao()
                            .insert(AppDao.MIXPANEL_TOKEN, MixPanelConstants.MIXPANEL_TOKEN)
                        callback?.onFetched("1")
                    }
                }.addOnFailureListener { callback?.onFetched("0") }
        } else {
            MixPanelConstants.MIXPANEL_TOKEN = mixpanelToken
            callback?.onFetched("1")
        }

    }
}