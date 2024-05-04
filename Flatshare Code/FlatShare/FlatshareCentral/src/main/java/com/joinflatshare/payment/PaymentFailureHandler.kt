package com.joinflatshare.payment

import com.android.billingclient.api.BillingClient
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

/**
 * Created by debopam on 30/05/23
 */
object PaymentFailureHandler {
    private var TAG = PaymentFailureHandler::class.java.simpleName

    fun handleConnectionFailure(
        responseCode: Int,
        callbackListener: OnPurchaseProgressListener?
    ) {
        CommonMethod.makeLog(TAG, "Google Play Billing Failed with response code $responseCode")
        when (responseCode) {
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE ->
                CommonMethods.makeToast("Google Play Service is currently unavailable. Please try again later.")


            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED ->
                CommonMethods.makeToast("Google Play Service disconnected unexpectedly. Please try again later.")

            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE ->
                CommonMethods.makeToast("Your Google Play Store seems to be outdated. Please update to the latest version.")

            BillingClient.BillingResponseCode.ERROR ->
                CommonMethods.makeToast("We faced an unknown error during the payment. Please try again later.")

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
                CommonMethods.makeToast("You have already purchased this offer. Kindly select a different offer.")

            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED ->
                CommonMethods.makeToast("Sorry, this payment feature is not supported on your device.")

            /*BillingClient.BillingResponseCode.USER_CANCELED ->
                CommonMethods.makeToast("It seems you have cancelled the payment. Please try again later.")*/

            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE ->
                CommonMethods.makeToast("We are sorry. This feature is currently not available.")

            BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                CommonMethods.makeToast("There seems to be something wrong on our end. Please try again later.")
        }
        callbackListener?.onPurchaseFailed()
    }
}