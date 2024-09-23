package com.joinflatshare.ui.bottomsheet.restriction

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogRestrictionBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.payment.OnProductPurchaseCompleteListener
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.bottomsheet.BottomSheetBaseView
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by debopam on 30/05/23
 */
class RestrictionBottomSheet(
    private val activity: BaseActivity,
    private val products: List<ProductDetails>?,
    private val restrictionType: String,
    private val callback: OnProductDetailsFetched
):BottomSheetBaseView(activity) {
    val TAG = RestrictionBottomSheet::class.java.simpleName
    private val dialog: BottomSheetDialog = BottomSheetDialog(activity)

    val viewBind: DialogRestrictionBinding =
        DialogRestrictionBinding.inflate(activity.layoutInflater)
    private var selectedProduct: ProductDetails? = null

    init {
        dialog.setContentView(viewBind.root)
        populateProducts()
        viewBind.cardWeek1.setOnClickListener { clickCard(1) }
        viewBind.cardWeek2.setOnClickListener { clickCard(2) }
        viewBind.cardWeek3.setOnClickListener { clickCard(3) }
        viewBind.cardWeek2.performClick()
        viewBind.imgCross.setOnClickListener {
            PaymentHandler.isPopUpShowing = false
            dismissDialog(dialog)
        }
        viewBind.cardElite.visibility = if (isEliteMember()) View.GONE else View.VISIBLE
        viewBind.cardElite.setOnClickListener {
            dismissDialog(dialog)
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    PaymentHandler.showPaymentForElite(activity, null)
                }, 500
            )
        }
        viewBind.btnContinue.setOnClickListener {
            if (selectedProduct != null) {
                dismissDialog(dialog)
                callback.onProductSelected(selectedProduct!!)
            }
        }
        showDialog(dialog)
        dialog.setOnDismissListener { PaymentHandler.isPopUpShowing=false }
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
        colorCard(viewBind.cardWeek1, false)
        colorCard(viewBind.cardWeek2, false)
        colorCard(viewBind.cardWeek3, false)
        when (position) {
            1 -> {
                colorCard(viewBind.cardWeek1, true)
                selectedProduct = products?.get(0)
            }

            2 -> {
                colorCard(viewBind.cardWeek2, true)
                selectedProduct = products?.get(1)
            }

            3 -> {
                colorCard(viewBind.cardWeek3, true)
                selectedProduct = products?.get(2)
            }
        }
    }

    private fun colorCard(card: MaterialCardView, isSelected: Boolean) {
        val textColor = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.white else R.color.grey4
        )
        val grey4 = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.blue_dark else R.color.grey4
        )
        val grey5 = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.blue_dark else R.color.grey5
        )
        val greyCircular = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.grey6 else R.color.grey5
        )
        val grey6 = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.blue_dark else R.color.grey6
        )

        card.strokeColor = grey6
        val llHolder: LinearLayout
        if (card.id == viewBind.cardWeek2.id) {
            val txt = (card.getChildAt(0) as LinearLayout).getChildAt(0) as TextView
            txt.setBackgroundColor(grey5)
            txt.setTextColor(textColor)
            llHolder = (card.getChildAt(0) as LinearLayout).getChildAt(1) as LinearLayout
        } else {
            llHolder = card.getChildAt(0) as LinearLayout
        }
        (llHolder.getChildAt(0) as TextView).setTextColor(grey4)
        (llHolder.getChildAt(2) as TextView).setTextColor(grey4)
        val circularCard = llHolder.getChildAt(1) as MaterialCardView
        circularCard.setCardBackgroundColor(greyCircular)
        (circularCard.getChildAt(0) as ImageView).setColorFilter(grey4)
    }

    private fun isEliteMember(): Boolean {
        var godMode = AppConstants.loggedInUser?.godMode
        try {
            if (!godMode.isNullOrEmpty() && godMode.contains(".")) {
                godMode = godMode.substring(0, godMode.lastIndexOf("."))
                val sdf = SimpleDateFormat("yyyy-MM-ddTHH:mm:ss", Locale.getDefault())
                val currentTime = System.currentTimeMillis()
                val godModeExpireTime = sdf.parse(godMode)!!
                if (godModeExpireTime.time < currentTime) {
                    return true
                }
            }
            return false
        } catch (ex: Exception) {
            return false
        }
    }
}