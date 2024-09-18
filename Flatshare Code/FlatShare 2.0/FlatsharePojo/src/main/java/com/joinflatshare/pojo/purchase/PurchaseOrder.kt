package com.joinflatshare.pojo.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseOrder(

	@field:SerializedName("purchaseOrder")
	val purchaseOrder: PurchaseOrder? = null,

	@field:SerializedName("purchaseToken")
	val purchaseToken: String? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("productName")
	var productName: String? = null,

	@field:SerializedName("acknowledged")
	val acknowledged: Boolean? = null,

	@field:SerializedName("orderId")
	val orderId: String? = null,

	@field:SerializedName("purchaseTime")
	val purchaseTime: Long? = null,

	@field:SerializedName("packageName")
	val packageName: String? = null,

	@field:SerializedName("purchaseState")
	val purchaseState: Int? = null
)
