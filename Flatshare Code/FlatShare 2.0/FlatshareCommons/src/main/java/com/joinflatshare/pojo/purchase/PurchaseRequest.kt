package com.joinflatshare.pojo.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseRequest(

    @field:SerializedName("payment")
    val payment: Payment = Payment()
)

data class Payment(

    @field:SerializedName("isPurchasedThroughGoogle")
    var isPurchasedThroughGoogle: Boolean? = null,

    @field:SerializedName("isPurchasedThroughCoin")
    var isPurchasedThroughCoin: Boolean? = null,

    @field:SerializedName("coinAmount")
    var coinAmount: Int? = null,

    @field:SerializedName("purchaseOrder")
    var purchaseOrder: PurchaseOrder = PurchaseOrder(),

    @field:SerializedName("purchaseAmount")
    var purchaseAmount: Int? = null,

    @field:SerializedName("purchaseType")
    var purchaseType: String? = null
)
