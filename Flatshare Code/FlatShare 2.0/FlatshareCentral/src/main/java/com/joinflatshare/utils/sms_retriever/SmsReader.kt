package com.joinflatshare.utils.sms_retriever

import android.app.Activity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.Task
import com.joinflatshare.utils.helper.CommonMethod


class SmsReader(private val activity: Activity) {
    private val LOG_TAG = "SmsReader"

    fun initialiseRetriever() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(activity)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {
            CommonMethod.makeLog(
                LOG_TAG,
                "Successfully started retriever, expect broadcast intent"
            )
        }

        task.addOnFailureListener {
            CommonMethod.makeLog(
                LOG_TAG,
                "Failed to start retriever, inspect Exception for more details"
            )
        }
    }
}