package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.GoogleConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnStringFetched

class DbGoogleRetriever {
    private val tableName = BuildConfig.GOOGLE_TABLE

    fun getGoogleApiKey(callback: OnStringFetched?) {
        val firebaseKey = FlatShareApplication.getDbInstance().appDao().get(AppDao.FIREBASE_KEY)
        if (firebaseKey.isNullOrBlank()) {
            val db = Firebase.firestore
            val docRef = db.collection("google").document(tableName)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        GoogleConstants.GOOGLE_API_KEY = document.getString("API_KEY")!!
                        FlatShareApplication.getDbInstance().appDao()
                            .insert(AppDao.FIREBASE_KEY, GoogleConstants.GOOGLE_API_KEY)
                        callback?.onFetched("1")
                    }
                }.addOnFailureListener { callback?.onFetched("0") }
        } else {
            GoogleConstants.GOOGLE_API_KEY = firebaseKey
            callback?.onFetched("1")
        }

    }
}