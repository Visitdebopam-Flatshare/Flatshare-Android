package com.joinflatshare.payment

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

/**
 * Created by debopam on 26/05/23
 */
class GooglePlayBillingClient(private val activity: BaseActivity) : PurchasesUpdatedListener {
    val TAG = GooglePlayBillingClient::class.java.simpleName
    var selectedProduct: ProductDetails? = null

    var callbackListener: OnPurchaseProgressListener? = null

    private lateinit var billingClient: BillingClient
    private lateinit var purchasesUpdatedListener: PurchasesUpdatedListener

    fun fetchPrice(
        products: ArrayList<String>,
        callback: OnProductPricesFetched
    ) {
        if (products.isNotEmpty()) {
            initialiseClient()
            startConnection { text ->
                if (text.equals("1")) {
                    // Connection created. Fetch Product Details
                    val queryList = ArrayList<QueryProductDetailsParams.Product>()
                    for (item in products) {
                        queryList.add(
                            QueryProductDetailsParams.Product.newBuilder().setProductId(item)
                                .setProductType(ProductType.INAPP).build()
                        )
                    }
                    queryProducts(queryList, callback)
                } else {
                    CommonMethod.makeLog(TAG, "Failed to launch Google Payment")
                }
            }
        } else CommonMethod.makeLog(TAG, "No products to query")
    }


    private fun initialiseClient() {
        purchasesUpdatedListener = this
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

    }

    private fun startConnection(callback: OnStringFetched) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.debugMessage.isNotBlank()) {
                    CommonMethod.makeLog(TAG, billingResult.debugMessage)
                }
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    callback.onFetched("1")
                } else {
                    PaymentFailureHandler.handleConnectionFailure(
                        billingResult.responseCode,
                        callbackListener
                    )
                    callback.onFetched("0")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                CommonMethod.makeLog(TAG, "Billing connection disconnected")
                callback.onFetched("0")
            }
        })
    }

    private fun queryProducts(
        queryList: ArrayList<QueryProductDetailsParams.Product>,
        callback: OnProductPricesFetched
    ) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(queryList)
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.debugMessage.isNotBlank()) {
                CommonMethod.makeLog(TAG, billingResult.debugMessage)
            }
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    callback.onPriceFetched(productDetailsList)
                } else {
                    CommonMethod.makeLog(TAG, "Products List is empty")
                    callback.onPriceFetched(null)
                }
            } else {
                PaymentFailureHandler.handleConnectionFailure(
                    billingResult.responseCode,
                    callbackListener
                )
            }
        }

    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        CommonMethod.makeLog(TAG, "Product Purchase Updated")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {
            handlePurchase(purchaseList)
        } else {
            PaymentFailureHandler.handleConnectionFailure(
                billingResult.responseCode,
                callbackListener
            )
        }
    }


    fun purchaseProduct(
        activity: BaseActivity,
        product: ProductDetails,
        callback: OnPurchaseProgressListener
    ) {
        initialiseClient()
        startConnection { text ->
            if (text.equals("1")) {
                // Connection created. Process payment
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()
                )
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                if (billingResult.debugMessage.isNotBlank()) {
                    CommonMethod.makeLog(TAG, billingResult.debugMessage)
                }
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    callbackListener = callback
                    selectedProduct = product
                    CommonMethod.makeLog(TAG, "Starting payment flow.")
                } else {
                    PaymentFailureHandler.handleConnectionFailure(
                        billingResult.responseCode,
                        callbackListener
                    )
                }

            } else {
                CommonMethod.makeLog(TAG, "Failed to launch Google Payment")
            }
        }
    }

    private fun handlePurchase(purchaseList: MutableList<Purchase>?) {
        if (!purchaseList.isNullOrEmpty()) {
            for (purchase in purchaseList) {
                CommonMethod.makeLog(TAG, "Handling purchase with order ID - ${purchase.orderId}")
                if (purchase.purchaseState == PurchaseState.PENDING) {
                    CommonMethod.makeLog(
                        TAG,
                        "Purchase with order ID - ${purchase.orderId} is in pending state"
                    )
                    CommonMethod.makeToast("Please wait while we receive payment acknowledgement.")
                    callbackListener?.onProductPurchasePending()
                } else {
                    callbackListener?.onProductPaymentSuccess(purchase)
                }
            }
        }
    }

    fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            CommonMethod.makeLog(
                TAG,
                "Purchase ${purchase.orderId} is being acknowledged"
            )
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            billingClient.acknowledgePurchase(
                acknowledgePurchaseParams.build()
            ) {
                CommonMethod.makeLog(
                    TAG,
                    "Purchase ${purchase.orderId} is acknowledged"
                )
                consumeProducts(purchase)
            }
        } else {
            CommonMethod.makeLog(
                TAG,
                "Purchase ${purchase.orderId} is already acknowledged"
            )
            consumeProducts(purchase)
        }
    }

    private fun consumeProducts(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(
            consumeParams
        ) { billingResult, p1 ->
            if (billingResult.debugMessage.isNotBlank()) {
                CommonMethod.makeLog(TAG, billingResult.debugMessage)
            }
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                CommonMethod.makeLog(TAG, "Purchase ${purchase.orderId} consumed")
                callbackListener?.onPurchaseSuccess(purchase)
            } else
                PaymentFailureHandler.handleConnectionFailure(
                    billingResult.responseCode,
                    callbackListener
                )
        }
    }
}