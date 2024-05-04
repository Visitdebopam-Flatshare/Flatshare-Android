package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.constants.GoogleConstants
import com.joinflatshare.interfaces.OnStringFetched

class DbAppVersionRetriever {
    fun getLatestVersion(callback: OnStringFetched?) {
        val db = Firebase.firestore
        val docRef = db.collection("app_version").document("latest")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    GoogleConstants.LATEST_APP_VERSION_CODE = document.getDouble("code")
                    GoogleConstants.APP_UPGRADE_TITLE = document.getString("title")
                    GoogleConstants.APP_UPGRADE_MESSAGE = document.getString("message")
                    callback?.onFetched("1")
                }
            }.addOnFailureListener { callback?.onFetched("0") }
    }
}