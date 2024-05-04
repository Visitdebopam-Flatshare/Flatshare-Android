package com.joinflatshare.payment

import com.android.billingclient.api.Purchase

/**
 * Created by debopam on 30/05/23
 */
interface OnPurchaseProgressListener {
    fun onPurchaseFailed()
    fun onProductPurchasePending()
    fun onPurchaseSuccess(purchase: Purchase)
    fun onProductPaymentSuccess(purchase: Purchase)
}