package com.joinflatshare.ui.bottomsheet

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetElitePreferenceBinding
import com.joinflatshare.payment.OnProductPurchaseCompleteListener
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 20/06/24
 */
class ElitePreferenceBottomSheet(
    private val activity: BaseActivity,
    private val callback: OnProductPurchaseCompleteListener?
):BottomSheetBaseView(activity) {

    private lateinit var viewBind: DialogBottomsheetElitePreferenceBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetElitePreferenceBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        click()
        showDialog(dialog)
    }

    private fun click() {
        viewBind.imgCross.setOnClickListener {
            dismissDialog(dialog)
        }
        viewBind.llExploreElite.setOnClickListener {
            dismissDialog(dialog)
            PaymentHandler.showPaymentForElite(activity, callback)
        }
    }
}