package com.joinflatshare.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.constants.AwsConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.helper.CommonMethod

class DbAwsRetriever {
    private val tableName = BuildConfig.AMAZON_S3_TABLE

    fun getAwsKeys(callback: OnStringFetched?) {
        val dbBucketName =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.AMAZON_S3_BUCKET_NAME)
        if (dbBucketName.isNullOrBlank()) {
            CommonMethod.makeLog("Aws", "Fetching AWS keys")
            val db = Firebase.firestore
            val docRef = db.collection("aws").document(tableName)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        FlatShareApplication.getDbInstance().appDao()
                            .insert(AppDao.AWS_ACCESS_KEY, document.getString("AWS_ACCESS_KEY")!!)
                        FlatShareApplication.getDbInstance().appDao()
                            .insert(AppDao.AWS_SECRET_KEY, document.getString("AWS_SECRET_KEY")!!)
                        FlatShareApplication.getDbInstance().appDao()
                            .insert(
                                AppDao.AMAZON_S3_BUCKET_NAME,
                                document.getString("AMAZON_S3_BUCKET_NAME")!!
                            )
                        assignKeys(callback)
                    }
                }.addOnFailureListener { callback?.onFetched("0") }
        } else {
            assignKeys(callback)
        }
    }

    private fun assignKeys(callback: OnStringFetched?) {
        AwsConstants.AWS_ACCESS_KEY =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.AWS_ACCESS_KEY)!!
        AwsConstants.AWS_SECRET_KEY =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.AWS_SECRET_KEY)!!
        AwsConstants.AMAZON_S3_BUCKET_NAME =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.AMAZON_S3_BUCKET_NAME)!!
        AwsConstants.AMAZON_BASE_URL =
            FlatShareApplication.getDbInstance().appDao().get(AppDao.AMAZON_BASE_URL)!!
        UrlConstants.setAmazonImageUrl()
        callback?.onFetched("1")
    }
}