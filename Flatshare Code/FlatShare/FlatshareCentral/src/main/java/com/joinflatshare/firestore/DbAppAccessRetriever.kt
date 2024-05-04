package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.interfaces.OnStringFetched

/**
 * Created by debopam on 27/10/23
 */
class DbAppAccessRetriever {
    fun getAccessStatus(callback: OnStringFetched?) {
        val db = Firebase.firestore
        val docRef = db.collection("app_block").document(BuildConfig.APPLICATION_ID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val hasAccess = document.getBoolean("allow")
                    if (hasAccess == null || hasAccess) {
                        callback?.onFetched("1")
                    } else {
                        val message = document.getString("message")
                        if (message.isNullOrEmpty())
                            callback?.onFetched("1")
                        else {
                            callback?.onFetched(message)
                        }
                    }
                }
            }.addOnFailureListener { callback?.onFetched("1") }
    }
}