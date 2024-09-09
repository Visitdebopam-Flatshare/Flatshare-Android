package com.joinflatshare.ui.bottomsheet.elite

import com.android.billingclient.api.ProductDetails
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetEliteBinding
import com.joinflatshare.payment.OnProductDetailsFetched
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 20/06/24
 */
class EliteBottomSheet(
    val activity: BaseActivity,
    val products: List<ProductDetails>?,
    val callback: OnProductDetailsFetched

) {
    private lateinit var viewBind: DialogBottomsheetEliteBinding
    lateinit var dialog: BottomSheetDialog

    init {
        create()
        populateProducts()
        EliteBottomSheetListener(activity, this, viewBind, callback)
        dialog.show()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetEliteBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
    }

    private fun populateProducts() {
        viewBind.txtEliteDesc1.text = products?.get(0)?.description
        viewBind.txtEliteDesc2.text = products?.get(1)?.description
        viewBind.txtEliteDesc3.text = products?.get(2)?.description
        viewBind.txtPrice1.text = products?.get(0)?.oneTimePurchaseOfferDetails?.formattedPrice
        viewBind.txtPrice2.text = products?.get(1)?.oneTimePurchaseOfferDetails?.formattedPrice
        viewBind.txtPrice3.text = products?.get(2)?.oneTimePurchaseOfferDetails?.formattedPrice
    }
}