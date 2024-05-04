package com.joinflatshare.constants

import com.joinflatshare.firestore.DbAwsRetriever
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.utils.system.ConnectivityListener

object AwsConstants {
    var AWS_ACCESS_KEY = ""
    var AWS_SECRET_KEY = ""
    var AMAZON_S3_BUCKET_NAME = ""
    var AMAZON_BASE_URL = ""

    fun initialiseAwsSdk(callback: OnStringFetched) {
        if (ConnectivityListener.checkInternet()) {
            if (AMAZON_S3_BUCKET_NAME.isEmpty()) {
                DbAwsRetriever().getAwsKeys(callback)
            } else {
                callback.onFetched("1")
            }
        }
    }
}