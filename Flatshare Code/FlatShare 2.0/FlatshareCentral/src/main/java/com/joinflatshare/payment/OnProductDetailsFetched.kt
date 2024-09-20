package com.joinflatshare.payment

import com.android.billingclient.api.ProductDetails

/**
 * Created by debopam on 26/05/23
 */
interface OnProductDetailsFetched {
    fun onProductSelected(
        product: ProductDetails
    )
}