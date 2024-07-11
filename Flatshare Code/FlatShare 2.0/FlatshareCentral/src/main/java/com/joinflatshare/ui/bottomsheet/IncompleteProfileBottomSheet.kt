package com.joinflatshare.ui.bottomsheet

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetIncompleteProfileBinding
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.profile.edit.ProfileEditActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 20/06/24
 */
class IncompleteProfileBottomSheet(
    private val activity: ApplicationBaseActivity,
    private val callback: OnStringFetched
) {
    private lateinit var viewBind: DialogBottomsheetIncompleteProfileBinding
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetIncompleteProfileBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        click()
        dialog.show()
    }

    private fun click() {
        viewBind.imgCrossIncomplete.setOnClickListener {
            dialog.dismiss()
        }
        viewBind.llCompleteProfile.setOnClickListener {
            val intent = Intent(activity, ProfileEditActivity::class.java)
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