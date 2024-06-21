package com.joinflatshare.ui.bottomsheet

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joinflatshare.FlatshareCentral.R
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
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from(activity.findViewById(R.id.bottomSheet_verified))

    init {
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        show()
    }

    private fun show() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        activity.findViewById<View>(R.id.bottomSheet_verified).visibility = View.VISIBLE
        click()
    }

    private fun click() {
        activity.findViewById<ImageView>(R.id.img_cross)?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        activity.findViewById<LinearLayout>(R.id.ll_explore_super_check).setOnClickListener {
            val intent = Intent(activity, ProfileVerifyActivity::class.java)
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