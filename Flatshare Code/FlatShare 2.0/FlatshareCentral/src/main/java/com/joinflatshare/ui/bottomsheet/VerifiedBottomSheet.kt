package com.joinflatshare.ui.bottomsheet

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetVerifiedBinding
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.profile.verify.ProfileVerifyActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 20/06/24
 */
class VerifiedBottomSheet(
    private val activity: ApplicationBaseActivity,
    private val callback: OnStringFetched
) {

    private lateinit var viewBind: DialogBottomsheetVerifiedBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetVerifiedBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        click()
        dialog.show()
    }

    private fun click() {
        viewBind.imgCross.setOnClickListener {
            dialog.dismiss()
        }
        viewBind.llExploreSuperCheck.setOnClickListener {
            val intent = Intent(activity, ProfileVerifyActivity::class.java)
            CommonMethod.switchActivity(
                activity,
                intent
            ) { result ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    callback.onFetched("1")
                    dialog.dismiss()
                }
            }
        }
    }
}