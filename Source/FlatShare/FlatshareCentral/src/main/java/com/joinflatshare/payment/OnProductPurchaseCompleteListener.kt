package com.joinflatshare.payment

import com.android.billingclient.api.Purchase

/**
 * Created by debopam on 30/05/23
 */
interface OnProductPurchaseCompleteListener {
    fun onProductPurchased(purchase: Purchase?)
    fun onProductPurchaseFailed()
}