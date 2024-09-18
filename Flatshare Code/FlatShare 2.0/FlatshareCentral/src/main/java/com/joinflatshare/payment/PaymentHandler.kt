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
import com.joinflatshare.ui.bottomsheet.elite.EliteBottomSheet
import com.joinflatshare.ui.bottomsheet.restriction.RestrictionBottomSheet
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.webservice.api.ApiManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

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
        if (playClient == null)
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
        if (playClient == null)
            playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
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
        if (playClient == null)
            playClient = GooglePlayBillingClient(activity)
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

    fun showPaymentForElite(activity: BaseActivity, callBack: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        if (playClient == null)
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
                    showPopUp(products, purchaseType, callBack)
                }
            }
        })
    }

    fun showPaymentForReveals(activity: BaseActivity, uiCallback: OnStringFetched?) {
        if (isPopUpShowing)
            return
        this.activity = activity
        if (playClient == null)
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
            if (restrictionType == GooglePaymentConstants.PAYMENT_TYPE_GOD_MODE) {
                EliteBottomSheet(activity, products,uiCallback, object : OnProductDetailsFetched {
                    override fun onProductSelected(
                        product: ProductDetails,
                        callback: OnProductPurchaseCompleteListener
                    ) {
                        var price =
                            product.oneTimePurchaseOfferDetails?.formattedPrice!!.substring(1)
                        price = price.substring(0, price.indexOf("."))
                        price = price.replace(",", "")
                        this@PaymentHandler.uiCallback = callback
                        this@PaymentHandler.purchaseAmount = Integer.valueOf(price)
                        purchaseProduct(product)
                    }
                })
            } else {
                RestrictionBottomSheet(
                    activity,
                    products,
                    restrictionType,
                    object : OnProductDetailsFetched {
                        override fun onProductSelected(
                            product: ProductDetails,
                            callback: OnProductPurchaseCompleteListener
                        ) {
                            var price =
                                product.oneTimePurchaseOfferDetails?.formattedPrice!!.substring(1)
                            price = price.substring(0, price.indexOf("."))
                            price = price.replace(",", "")
                            this@PaymentHandler.uiCallback = callback
                            this@PaymentHandler.purchaseAmount = Integer.valueOf(price)
                            purchaseProduct(product)
                        }
                    })
            }
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

    override fun onProductPaymentSuccess(purchase: Purchase, selectedProduct: ProductDetails?) {
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
            request.payment.purchaseOrder.productName = selectedProduct?.name
            CommonMethod.makeLog("Original Json", purchase.originalJson)
            val observable = ApiManager.getApiInterface().purchaseOrder(request)
            ApiManager().callApi(
                activity,
                observable,
                object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                    override fun onResponseCallBack(response: String) {
                        MixpanelUtils.onPurchaseSuccess(
                            purchase.originalJson,
                            Gson().toJson(request)
                        )
                        playClient?.acknowledgePurchase(purchase)
                    }
                })
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