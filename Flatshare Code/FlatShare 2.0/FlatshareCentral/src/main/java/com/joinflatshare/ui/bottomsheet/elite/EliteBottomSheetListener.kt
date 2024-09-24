package com.joinflatshare.ui.bottomsheet.elite

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.billingclient.api.ProductDetails
import com.google.android.material.card.MaterialCardView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetEliteBinding
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 07/09/24
 */
class EliteBottomSheetListener(
    private val activity: BaseActivity,
    private val bottomSheet: EliteBottomSheet,
    private val viewBind: DialogBottomsheetEliteBinding,
    callback: OnProductDetailsFetched
) {
    private var selectedProduct: ProductDetails? = null

    init {
        viewBind.cardWeek1.setOnClickListener { click(1) }
        viewBind.cardWeek2.setOnClickListener { click(2) }
        viewBind.cardWeek3.setOnClickListener { click(3) }
        viewBind.cardWeek2.performClick()
        viewBind.btnElite.setOnClickListener {
            if (selectedProduct != null) {
                bottomSheet.dismissDialog(bottomSheet.dialog)
                MixpanelUtils.onButtonClicked("Elite ${selectedProduct?.name} selected")
                callback.onProductSelected(selectedProduct!!)
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
            if (isSelected) R.color.elite_blue else R.color.grey4
        )
        val grey5 = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.elite_blue else R.color.grey5
        )
        val greyCircular = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.grey6 else R.color.grey5
        )
        val grey6 = ContextCompat.getColor(
            activity,
            if (isSelected) R.color.elite_blue else R.color.grey6
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

    private fun click(position: Int) {
        colorCard(viewBind.cardWeek1, false)
        colorCard(viewBind.cardWeek2, false)
        colorCard(viewBind.cardWeek3, false)
        when (position) {
            1 -> {
                colorCard(viewBind.cardWeek1, true)
                selectedProduct = bottomSheet.products?.get(0)
                viewBind.txtFlatscore.text = "Add 250 to your Flatscore."
            }

            2 -> {
                colorCard(viewBind.cardWeek2, true)
                selectedProduct = bottomSheet.products?.get(1)
                viewBind.txtFlatscore.text = "Add 500 to your Flatscore."
            }

            3 -> {
                colorCard(viewBind.cardWeek3, true)
                selectedProduct = bottomSheet.products?.get(2)
                viewBind.txtFlatscore.text = "Add 1000 to your Flatscore."
            }
        }

    }
}