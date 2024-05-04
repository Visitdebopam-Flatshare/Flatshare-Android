package com.joinflatshare.payment

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.GooglePaymentConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.pojo.purchase.PurchaseOrder
import com.joinflatshare.pojo.purchase.PurchaseRequest
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.DialogRestriction
import com.joinflatshare.ui.explore.adapter.ExploreAdapter
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 26/05/23
 */
object PaymentHandler : OnPurchaseProgressListener {
    private val TAG = PaymentFailureHandler::class.java.simpleName
    private lateinit var activity: BaseActivity
    private var playClient: GooglePlayBillingClient? = null
    private var uiCallback: OnProductPurchaseCompleteListener? = null
    private var purchaseAmount = 0
    private var purchaseType = ""
    var isPopUpShowing = false


    fun showPaymentForChecks(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_CHECK_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHECK_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHECK_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_CHECKS
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    fun showPaymentForChats(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_3)
        activity.apiManager.showProgress()
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                activity.apiManager.hideProgress()
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_CHAT
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    fun showPaymentForProfiles(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        playClient = GooglePlayBillingClient(activity)
        ExploreAdapter.shouldShowPaymentGateway = false
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_PROFILE_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_PROFILE_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_PROFILE_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_FEED
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    fun showPaymentForGodMode(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_GOD_MODE
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    fun showPaymentForReveals(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_REVEAL_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_REVEAL_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_REVEAL_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_REVEAL
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }


    private fun showPopUp(
        products: List<ProductDetails>,
        restrictionType: String,
        uiCallback: OnStringFetched?
    ) {

        activity.runOnUiThread {
            isPopUpShowing = true
            DialogRestriction(
                activity,
                products,
                restrictionType,
                uiCallback,
                object : OnProductDetailsFetched {
                    override fun onProductSelected(
                        products: List<ProductDetails>?,
                        amount: Int,
                        callback: OnProductPurchaseCompleteListener
                    ) {
                        this@PaymentHandler.uiCallback = callback
                        this@PaymentHandler.purchaseAmount = amount
                        purchaseProduct(products!![0])
                    }
                })
        }
    }

    private fun purchaseProduct(
        product: ProductDetails
    ) {
        playClient?.purchaseProduct(activity, product, this)
    }

    override fun onPurchaseFailed() {
        uiCallback?.onProductPurchaseFailed()
    }

    override fun onProductPurchasePending() {
        uiCallback?.onProductPurchased(null)
    }

    override fun onPurchaseSuccess(purchase: Purchase) {
        CommonMethod.makeLog(TAG, "Purchase successful")
        uiCallback?.onProductPurchased(purchase)
    }

    override fun onProductPaymentSuccess(purchase: Purchase) {
        // Verify the purchase.
        if (FlatShareApplication.getDbInstance().userDao()
                .isPurchaseOrderGenuine(purchase.orderId)
        ) {
            CommonMethod.makeLog("Purchase", Gson().toJson(purchase))
            logPaymentSuccess(purchase)
            // Grant entitlement to the user. Call API
            val request = PurchaseRequest()
            request.payment.isPurchasedThroughCoin = false
            request.payment.isPurchasedThroughGoogle = true
            request.payment.coinAmount = 0
            request.payment.purchaseType = purchaseType
            request.payment.purchaseAmount = purchaseAmount
            request.payment.purchaseOrder =
                Gson().fromJson(purchase.originalJson, PurchaseOrder::class.java)
            CommonMethod.makeLog("Original Json", purchase.originalJson)
            activity.apiManager.purchaseProduct(
                request, purchase,
            ) {
                MixpanelUtils.onPurchaseSuccess(purchase.originalJson, Gson().toJson(request))
                playClient?.acknowledgePurchase(purchase)
            }
        }
    }

    private fun logPaymentPopup(paymentType: String) {
        MixpanelUtils.onButtonClicked("Payment Popup for $paymentType")
        Logger.logPayment("Payment Popup for $paymentType", Logger.LOG_TYPE_PAYMENT)
    }

    private fun logPaymentSuccess(purchase: Purchase) {
        MixpanelUtils.onPaymentSuccess(purchase.originalJson, purchaseType)
        Logger.logPayment(
            "Payment Success for $purchaseType with details - ${purchase.originalJson}",
            Logger.LOG_TYPE_PAYMENT
        )
    }
}