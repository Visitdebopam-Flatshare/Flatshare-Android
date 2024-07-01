package com.joinflatshare.ui.bottomsheet

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joinflatshare.FlatshareCentral.R
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
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from(activity.findViewById(R.id.bottomSheet_profile_incomplete))

    init {
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        show()
    }

    private fun show() {
        activity.findViewById<View>(R.id.bottomSheet_profile_incomplete).visibility = View.VISIBLE
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        click()
    }

    private fun click() {
        activity.findViewById<ImageView>(R.id.img_cross_incomplete)?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        activity.findViewById<LinearLayout>(R.id.ll_complete_profile).setOnClickListener {
            val intent = Intent(activity, ProfileEditActivity::class.java)
            CommonMethod.switchActivity(
                activity,
                intent
            ) { result ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    callback.onFetched("1")
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }
}