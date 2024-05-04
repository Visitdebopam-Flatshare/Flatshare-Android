package com.joinflatshare.payment

import com.android.billingclient.api.ProductDetails

/**
 * Created by debopam on 26/05/23
 */
interface OnProductPricesFetched {
    fun onPriceFetched(products: List<ProductDetails>?)
}