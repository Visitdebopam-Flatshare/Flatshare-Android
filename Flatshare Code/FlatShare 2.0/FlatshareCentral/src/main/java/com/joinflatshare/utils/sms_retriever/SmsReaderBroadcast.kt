package com.joinflatshare.utils.sms_retriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.utils.helper.CommonMethod

class SmsReaderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent!!.action) {
            val extras: Bundle? = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                    CommonMethod.makeLog("Message", message)
                    if (!message.isNullOrBlank()) {
                        val intent = Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_READ_SMS)
                        intent.putExtra("valid", true)
                        intent.putExtra("message", message)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    CommonMethod.makeLog("Message", "Timeout")
                    val intent = Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_READ_SMS)
                    intent.putExtra("valid", false)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                }
            }
        }

    }

}