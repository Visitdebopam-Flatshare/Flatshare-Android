package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnStringFetched

class DbSendbirdRetriever {
    private val tableName = BuildConfig.SENDBIRD_TABLE

    fun getSendbirdKeys(callback: OnStringFetched?) {
        val sendbirdKey = FlatShareApplication.getDbInstance().appDao().get(AppDao.SENDBIRD_APPID)
        if (sendbirdKey.isNullOrBlank()) {
            val db = Firebase.firestore
            val docRef = db.collection("sendbird").document(tableName)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    SendBirdConstants.SENDBIRD_APPID = document.getString("APPID")!!
                    SendBirdConstants.SENDBIRD_API_TOKEN = document.getString("TOKEN")!!
                    FlatShareApplication.getDbInstance().appDao()
                        .insert(AppDao.SENDBIRD_APPID, SendBirdConstants.SENDBIRD_APPID)
                    FlatShareApplication.getDbInstance().appDao().insert(
                        AppDao.SENDBIRD_API_TOKEN, SendBirdConstants.SENDBIRD_API_TOKEN
                    )
                    SendBirdConstants.setSendbirdBaseurl()
                    callback?.onFetched("1")
                }
            }.addOnFailureListener { callback?.onFetched("0") }
        } else {
            SendBirdConstants.SENDBIRD_APPID = sendbirdKey
            SendBirdConstants.SENDBIRD_API_TOKEN =
                FlatShareApplication.getDbInstance().appDao().get(AppDao.SENDBIRD_API_TOKEN)!!
            SendBirdConstants.setSendbirdBaseurl()
            callback?.onFetched("1")
        }
    }
}