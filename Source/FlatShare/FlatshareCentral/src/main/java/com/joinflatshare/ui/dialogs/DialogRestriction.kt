package com.joinflatshare.ui.dialogs

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogRestrictionBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.GooglePaymentConstants
import com.joinflatshare.constants.RouteConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.payment.OnProductPurchaseCompleteListener
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.purchase.PurchaseRequest
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 30/05/23
 */
class DialogRestriction(
    private val activity: BaseActivity,
    private val products: List<ProductDetails>,
    private val restrictionType: String,
    private val uiCallback: OnStringFetched?,
    private val callback: OnProductDetailsFetched
) {
    val TAG = DialogRestriction::class.java.simpleName

    val viewBind: DialogRestrictionBinding
    private val arrayPlanName = ArrayList<TextView>()
    private val arrayPrice = ArrayList<TextView>()
    private val arrayPriceHolder = ArrayList<MaterialCardView>()
    private var selectedProduct: ProductDetails? = null
    private var fcoin1 = 0
    private var fcoin2 = 0
    private var fcoin3 = 0

    init {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(activity, R.style.DialogBottomPopUpTheme)
        viewBind = DialogRestrictionBinding.inflate(activity.layoutInflater)
        builder.setView(viewBind.root)
        builder.setCancelable(false)
        val dialog: AlertDialog = builder.create()
        loadView(restrictionType, viewBind)
        loadProfile()
        loadData()
        click(dialog)
        arrayPriceHolder[1].performClick()
        CommonMethods.showDialog(activity, dialog)
    }

    private fun loadData() {
        val children = viewBind.llRestrictionCheckHolder.children
        for (position in 0 until children.count()) {
            if (viewBind.llRestrictionCheckHolder.getChildAt(position) is MaterialCardView) {
                val child =
                    viewBind.llRestrictionCheckHolder.getChildAt(position) as MaterialCardView
                val planName = (child.getChildAt(0) as LinearLayout).getChildAt(0) as TextView
                val planPrice = (child.getChildAt(0) as LinearLayout).getChildAt(1) as TextView
                arrayPriceHolder.add(child)
                arrayPlanName.add(planName)
                arrayPrice.add(planPrice)
                assignData(position, planName, planPrice)
            }
        }
    }

    private fun assignData(position: Int, planName: TextView, planPrice: TextView) {
        when (position) {
            0 -> {
                if (products[0].description.contains(" - ")) {
                    val descSplit = products[0].description.split(" - ")
                    planName.text = descSplit[0]
                    viewBind.txtPaymentF1.text = descSplit[1]
                    fcoin1 = Integer.valueOf(descSplit[1])
                } else planName.text = products[0].description
                planPrice.text = products[0].oneTimePurchaseOfferDetails?.formattedPrice
            }

            2 -> {
                if (products[1].description.contains(" - ")) {
                    val descSplit = products[1].description.split(" - ")
                    planName.text = descSplit[0]
                    viewBind.txtPaymentF2.text = descSplit[1]
                    fcoin2 = Integer.valueOf(descSplit[1])
                } else planName.text = products[1].description
                planPrice.text = products[1].oneTimePurchaseOfferDetails?.formattedPrice
            }

            4 -> {
                if (products[2].description.contains(" - ")) {
                    val descSplit = products[2].description.split(" - ")
                    planName.text = descSplit[0]
                    viewBind.txtPaymentF3.text = descSplit[1]
                    fcoin3 = Integer.valueOf(descSplit[1])
                } else planName.text = products[2].description
                planPrice.text = products[2].oneTimePurchaseOfferDetails?.formattedPrice
            }
        }
    }

    private fun validateUserFcoin() {
        val userFcoin = AppConstants.loggedInUser!!.fsCoins
        viewBind.txtFcoin.text = "" + userFcoin
        if (fcoin1 == 0 || userFcoin < fcoin1) {
            viewBind.cardPaymentCoin1.alpha = 0.5f
            viewBind.cardPaymentCoin1.isEnabled = false
        }
        if (fcoin2 == 0 || userFcoin < fcoin2) {
            viewBind.cardPaymentCoin2.alpha = 0.5f
            viewBind.cardPaymentCoin2.isEnabled = false
        }
        if (fcoin3 == 0 || userFcoin < fcoin3) {
            viewBind.cardPaymentCoin3.alpha = 0.5f
            viewBind.cardPaymentCoin3.isEnabled = false
        }
    }

    private fun click(dialog: AlertDialog) {
        viewBind.imgRestrictionClose.setOnClickListener {
            PaymentHandler.isPopUpShowing = false
            CommonMethods.dismissDialog(activity, dialog)
        }
        for (position in 0 until arrayPriceHolder.size) {
            arrayPriceHolder[position].setOnClickListener {
                selectedProduct = products[position]
                for (item in 0 until arrayPriceHolder.size) {
                    if (position == item) {
                        // Clicked position
                        paintView(
                            arrayPriceHolder[item], ContextCompat.getColor(
                                activity,
                                R.color.color_text_primary
                            )
                        )
                        val linearLayout = arrayPriceHolder[item].getChildAt(0) as LinearLayout

                        for (text in linearLayout.children) {
                            paintView(
                                text, ContextCompat.getColor(
                                    activity,
                                    R.color.color_text_secondary
                                )
                            )
                        }
                    } else {
                        // Un selected
                        paintView(
                            arrayPriceHolder[item], ContextCompat.getColor(
                                activity,
                                R.color.color_bg
                            )
                        )
                        val linearLayout = arrayPriceHolder[item].getChildAt(0) as LinearLayout
                        for (text in linearLayout.children) {
                            paintView(
                                text, ContextCompat.getColor(
                                    activity,
                                    R.color.color_text_primary
                                )
                            )
                        }
                    }
                }
            }
        }
        viewBind.btnRestrictionContinue.setOnClickListener {
            if (selectedProduct != null) {
                val list = ArrayList<ProductDetails>()
                list.add(selectedProduct!!)
                var price =
                    selectedProduct?.oneTimePurchaseOfferDetails?.formattedPrice?.substring(1)
                price = price?.substring(0, price.indexOf("."))
                price = price?.replace(",", "")
                callback.onProductSelected(
                    list,
                    Integer.valueOf(price!!), object :
                        OnProductPurchaseCompleteListener {
                        override fun onProductPurchased(purchase: Purchase?) {
                            CommonMethod.makeLog(TAG, "Payment Complete")
                            activity.runOnUiThread {
                                uiCallback?.onFetched("1")
                                PaymentHandler.isPopUpShowing = false
                                CommonMethods.dismissDialog(activity, dialog)
                            }

                        }

                        override fun onProductPurchaseFailed() {
                            PaymentHandler.isPopUpShowing = false
                            CommonMethods.dismissDialog(activity, dialog)
                        }
                    })
            }
        }
        viewBind.btnRestrictionInvite.setOnClickListener {
            PaymentHandler.isPopUpShowing = false
            CommonMethods.dismissDialog(activity, dialog)
            val intent = Intent(activity, InviteActivity::class.java)
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, InviteActivity.INVITE_TYPE_APP)
            CommonMethod.switchActivity(activity, intent, false)
        }

        viewBind.cardPaymentCoin1.setOnClickListener {
            if (viewBind.cardPaymentCoin1.alpha == 1f)
                makePurchaseUsingCoin(dialog, restrictionType, fcoin1)
        }
        viewBind.cardPaymentCoin2.setOnClickListener {
            if (viewBind.cardPaymentCoin2.alpha == 1f)
                makePurchaseUsingCoin(dialog, restrictionType, fcoin2)
        }
        viewBind.cardPaymentCoin3.setOnClickListener {
            if (viewBind.cardPaymentCoin3.alpha == 1f)
                makePurchaseUsingCoin(dialog, restrictionType, fcoin3)
        }
    }

    private fun paintView(view: View, color: Int) {
        if (view is TextView)
            view.setTextColor(color)
        else if (view is MaterialCardView) {
            view.setCardBackgroundColor(
                color
            )
        }
    }

    private fun loadView(restrictionType: String, viewHolder: DialogRestrictionBinding) {
        when (restrictionType) {
            GooglePaymentConstants.PAYMENT_TYPE_CHECKS -> {
                viewHolder.txtRestrictionHeader.text = "Get Extra Checks"
                viewHolder.imgRestriction.setImageResource(R.drawable.ic_check)
                viewHolder.txtRestrictionDescription.text = "Check 2-4x more than\nother members."
            }

            GooglePaymentConstants.PAYMENT_TYPE_CHAT -> {
                viewHolder.txtRestrictionHeader.text = "Get Extra Chat Requests"
                viewHolder.imgRestriction.setImageResource(R.drawable.ic_chat_request)
                viewHolder.txtRestrictionDescription.text = "Connect with 2-4x\nmore members."
            }

            GooglePaymentConstants.PAYMENT_TYPE_FEED -> {
                viewHolder.txtRestrictionHeader.text = "See More Profiles"
                viewHolder.imgRestriction.setImageResource(R.drawable.ic_restriction_profile)
                viewHolder.txtRestrictionDescription.text = "Get 2-4x more\nrecommendations."
            }

            GooglePaymentConstants.PAYMENT_TYPE_GOD_MODE -> {
                viewHolder.txtRestrictionHeader.text = "Activate God Mode"
                viewHolder.imgRestriction.setImageResource(R.drawable.ic_godmode)
                viewHolder.txtRestrictionDescription.text =
                    "See who checks you.\nReveal 2 names everyday."
                viewHolder.llPaymentGodmode.visibility = View.VISIBLE
                viewHolder.llFcoinHolder.visibility = View.GONE
                viewHolder.btnRestrictionInvite.visibility = View.GONE
                viewHolder.frameRestrictionCoinTotal.visibility = View.INVISIBLE
            }

            GooglePaymentConstants.PAYMENT_TYPE_REVEAL -> {
                viewHolder.txtRestrictionHeader.text = "Get More Reveals"
                viewHolder.imgRestriction.setImageResource(R.drawable.ic_godmode)
                viewHolder.txtRestrictionDescription.text =
                    "See who checks you.\nConnect instantly."
                viewHolder.llFcoinHolder.visibility = View.GONE
                viewHolder.btnRestrictionInvite.visibility = View.GONE
                viewHolder.frameRestrictionCoinTotal.visibility = View.INVISIBLE

            }
        }
    }

    private fun makePurchaseUsingCoin(dialog: AlertDialog, purchaseType: String, amount: Int) {
        val request = PurchaseRequest()
        request.payment.isPurchasedThroughCoin = true
        request.payment.isPurchasedThroughGoogle = false
        request.payment.coinAmount = amount
        request.payment.purchaseType = purchaseType
        request.payment.purchaseAmount = 0
        activity.apiManager.purchaseProduct(
            request, null,
        ) {
            CommonMethod.makeLog(TAG, "Payment Complete")
            MixpanelUtils.onPurchaseSuccess("FS Coin Payment", Gson().toJson(request))
            DialogLottieViewer(activity, R.raw.lottie_coin_payment, null)
            uiCallback?.onFetched("1")
            CommonMethods.dismissDialog(activity, dialog)
        }
    }

    private fun loadProfile() {
        activity.baseApiController.getUser(
            false,
            AppConstants.loggedInUser!!.id,
            object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    validateUserFcoin()
                }

            })
    }
}