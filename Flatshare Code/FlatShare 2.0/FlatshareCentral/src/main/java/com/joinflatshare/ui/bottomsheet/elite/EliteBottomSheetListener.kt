package com.joinflatshare.ui.bottomsheet.elite

import androidx.core.content.ContextCompat
import com.android.billingclient.api.ProductDetails
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetEliteBinding
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.ui.base.BaseActivity

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
                bottomSheet.dialog.dismiss()
                callback.onProductSelected(selectedProduct!!)
            }
        }
    }

    private fun click(position: Int) {
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
                selectedProduct = bottomSheet.products?.get(0)
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
                selectedProduct = bottomSheet.products?.get(1)
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
                selectedProduct = bottomSheet.products?.get(2)
            }
        }

    }
}