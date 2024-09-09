package com.joinflatshare.ui.bottomsheet.restriction

import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogRestrictionBinding
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.payment.OnProductPurchaseCompleteListener
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 30/05/23
 */
class RestrictionBottomSheet(
    private val activity: BaseActivity,
    private val products: List<ProductDetails>?,
    private val restrictionType: String,
    private val callback: OnProductDetailsFetched
) {
    val TAG = RestrictionBottomSheet::class.java.simpleName
    private val dialog: BottomSheetDialog

    val viewBind: DialogRestrictionBinding
    private var selectedProduct: ProductDetails? = null

    init {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogRestrictionBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        populateProducts()
        viewBind.cardWeek1.setOnClickListener { clickCard(1) }
        viewBind.cardWeek2.setOnClickListener { clickCard(2) }
        viewBind.cardWeek3.setOnClickListener { clickCard(3) }
        viewBind.cardWeek2.performClick()
        viewBind.imgCross.setOnClickListener {
            PaymentHandler.isPopUpShowing = false
            dialog.dismiss()
        }
        viewBind.cardElite.setOnClickListener {
            PaymentHandler.isPopUpShowing = false
            dialog.dismiss()
            Handler(Looper.getMainLooper()).postDelayed(
                { PaymentHandler.showPaymentForElite(activity, null) }, 500
            )
        }
        viewBind.btnElite.setOnClickListener {
            if (selectedProduct != null) {
                callback.onProductSelected(
                    selectedProduct!!,
                    object : OnProductPurchaseCompleteListener {
                        override fun onProductPurchased(purchase: Purchase?) {
                            PaymentHandler.isPopUpShowing = false
                            dialog.dismiss()
                        }

                        override fun onProductPurchaseFailed() {
                            PaymentHandler.isPopUpShowing = false
                            dialog.dismiss()
                        }
                    })
            }
        }
        dialog.show()
    }

    private fun populateProducts() {
        viewBind.txtEliteDesc1.text = products?.get(0)?.description
        viewBind.txtEliteDesc2.text = products?.get(1)?.description
        viewBind.txtEliteDesc3.text = products?.get(2)?.description
        viewBind.txtPrice1.text = products?.get(0)?.oneTimePurchaseOfferDetails?.formattedPrice
        viewBind.txtPrice2.text = products?.get(1)?.oneTimePurchaseOfferDetails?.formattedPrice
        viewBind.txtPrice3.text = products?.get(2)?.oneTimePurchaseOfferDetails?.formattedPrice
    }

    private fun clickCard(position: Int) {
        viewBind.cardWeek1.alpha = 0.5f
        viewBind.cardWeek2.alpha = 0.5f
        viewBind.cardWeek3.alpha = 0.5f
        viewBind.txtPrice1.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.txtPrice2.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.txtPrice3.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.txtEliteDesc1.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.txtEliteDesc2.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        viewBind.txtEliteDesc3.setTextColor(
            ContextCompat.getColor(
                activity,
                R.color.color_text_primary
            )
        )
        when (position) {
            1 -> {
                viewBind.cardWeek1.alpha = 1f
                viewBind.txtPrice1.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                viewBind.txtEliteDesc1.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                selectedProduct = products?.get(0)
            }

            2 -> {
                viewBind.cardWeek2.alpha = 1f
                viewBind.txtPrice2.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                viewBind.txtEliteDesc2.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                selectedProduct = products?.get(1)
            }

            3 -> {
                viewBind.cardWeek3.alpha = 1f
                viewBind.txtPrice3.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                viewBind.txtEliteDesc3.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.elite_blue
                    )
                )
                selectedProduct = products?.get(2)
            }
        }

    }
}