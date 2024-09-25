package com.joinflatshare.payment

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.debopam.progressdialog.DialogCustomProgress
import com.google.gson.Gson
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.GooglePaymentConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.purchase.PurchaseOrder
import com.joinflatshare.pojo.purchase.PurchaseRequest
import com.joinflatshare.pojo.user.UserResponse
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
    private val TAG = PaymentHandler::class.java.simpleName
    private lateinit var activity: BaseActivity
    private var playClient: GooglePlayBillingClient? = null
    private var uiCallback: OnProductPurchaseCompleteListener? = null
    private var purchaseAmount = 0
    private var purchaseType = ""
    var isPopUpShowing = false


    /*fun showPaymentForChecks(activity: BaseActivity, uiCallback: OnStringFetched?) {
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
    }*/

    fun showPaymentForChats(
        activity: BaseActivity,
        uiCallback: OnProductPurchaseCompleteListener?
    ) {
        if (isPopUpShowing)
            return
        this.activity = activity
        DialogCustomProgress.showProgress(activity)
        if (playClient == null)
            playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_CHAT_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                DialogCustomProgress.hideProgress(activity)
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_CHAT
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    /*fun showPaymentForProfiles(activity: BaseActivity, uiCallback: OnStringFetched?) {
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
    }*/

    fun showPaymentForElite(
        activity: BaseActivity,
        uiCallback: OnProductPurchaseCompleteListener?
    ) {
        if (isPopUpShowing)
            return
        this.activity = activity
        DialogCustomProgress.showProgress(activity)
        if (playClient == null)
            playClient = GooglePlayBillingClient(activity)
        val productsIds = ArrayList<String>()
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_1)
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_2)
        productsIds.add(GooglePaymentConstants.PRODUCT_GOD_3)
        playClient?.fetchPrice(productsIds, object : OnProductPricesFetched {
            override fun onPriceFetched(products: List<ProductDetails>?) {
                DialogCustomProgress.hideProgress(activity)
                if (!products.isNullOrEmpty()) {
                    purchaseType = GooglePaymentConstants.PAYMENT_TYPE_GOD_MODE
                    logPaymentPopup(purchaseType)
                    showPopUp(products, purchaseType, uiCallback)
                }
            }
        })
    }

    /*fun showPaymentForReveals(activity: BaseActivity, uiCallback: OnStringFetched?) {
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
    }*/


    private fun showPopUp(
        products: List<ProductDetails>,
        restrictionType: String,
        uiCallback: OnProductPurchaseCompleteListener?
    ) {
        activity.runOnUiThread {
            isPopUpShowing = true
            if (restrictionType == GooglePaymentConstants.PAYMENT_TYPE_GOD_MODE) {
                EliteBottomSheet(activity, products, object : OnProductDetailsFetched {
                    override fun onProductSelected(product: ProductDetails) {
                        val price =
                            (product.oneTimePurchaseOfferDetails?.priceAmountMicros!! / 1000000)
                        this@PaymentHandler.uiCallback = uiCallback
                        this@PaymentHandler.purchaseAmount = price.toInt()
                        purchaseProduct(product)
                    }
                })
            } else {
                RestrictionBottomSheet(
                    activity,
                    products,
                    restrictionType,
                    object : OnProductDetailsFetched {
                        override fun onProductSelected(product: ProductDetails) {
                            val price =
                                (product.oneTimePurchaseOfferDetails?.priceAmountMicros!! / 1000000)
                            this@PaymentHandler.uiCallback = uiCallback
                            this@PaymentHandler.purchaseAmount = price.toInt()
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
        isPopUpShowing = false
        uiCallback?.onProductPurchaseFailed()
    }

    override fun onProductPurchasePending() {
//        uiCallback?.onProductPurchased(null)
    }

    override fun onPurchaseSuccess(purchase: Purchase) {
        CommonMethod.makeLog(TAG, "Purchase successful")
        isPopUpShowing = false
        activity.baseApiController.getUser(
            true,
            AppConstants.loggedInUser?.id,
            object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    uiCallback?.onProductPurchased(purchase)
                }
            })
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