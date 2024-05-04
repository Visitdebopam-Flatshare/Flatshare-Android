package com.joinflatshare.ui.liked

import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityLikedBinding
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.ui.base.BaseActivity

class LikedListener(
    private val activity: LikedActivity,
    private val viewBind: ActivityLikedBinding
) : View.OnClickListener {
    init {
//        viewBind.cardDropdown.setOnClickListener(this)
        viewBind.llLikedReceived.setOnClickListener(this)
        viewBind.llLikedSent.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.cardDropdown.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Shared Flat Search"))
                list.add(ModelBottomSheet(0, "Flathunt Together"))
                list.add(ModelBottomSheet(0, "Flatmate Search"))
                list.add(ModelBottomSheet(0, "Casual Date"))
                list.add(ModelBottomSheet(0, "Long-Term Partner"))
                list.add(ModelBottomSheet(0, "Activity Partners"))
                BottomSheetView(activity, list).show { _, position ->
                    val oldType = activity.SEARCH_TYPE
                    when (position) {
                        0 -> activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT
                        1 -> activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
                        2 -> activity.SEARCH_TYPE = BaseActivity.TYPE_USER
                        3 -> activity.SEARCH_TYPE = BaseActivity.TYPE_DATE_CASUAL
                        4 -> activity.SEARCH_TYPE = BaseActivity.TYPE_DATE_LONG_TERM
                        5 -> activity.SEARCH_TYPE = BaseActivity.TYPE_DATE_ACTIVITY_PARTNERS
                    }
                    if (oldType != activity.SEARCH_TYPE) {
                        if (activity.viewTypeReceivedLikes)
                            activity.apiController.currentReceivedPage = 0
                        else
                            activity.apiController.currentSentPage = 0
                        activity.apiController.callApi()
                    }
                }
            }

            viewBind.llLikedReceived.id -> {
                if (!activity.viewTypeReceivedLikes) {
                    viewBind.llLikedReceived.background =
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_light_blue_elliptical
                        )
                    viewBind.llLikedSent.background =
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_black_stroke_elliptical
                        )
                    viewBind.txtLikedSent.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_text_primary
                        )
                    )
                    viewBind.txtLikedReceived.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.black
                        )
                    )
                    (viewBind.txtLikedReceivedCount.parent as LinearLayout).background =
                        ContextCompat.getDrawable(activity, R.drawable.drawable_blue_circle)
                    (viewBind.txtLikedSentCount.parent as LinearLayout).background =
                        ContextCompat.getDrawable(activity, R.drawable.drawable_grey_circle)
                    activity.viewTypeReceivedLikes = true
                    activity.apiController.currentReceivedPage = 0
                    activity.apiController.callApi()
                }
            }

            viewBind.llLikedSent.id -> {
                if (activity.viewTypeReceivedLikes) {
                    viewBind.llLikedSent.background =
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_light_blue_elliptical
                        )
                    viewBind.llLikedReceived.background =
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.drawable_button_black_stroke_elliptical
                        )
                    viewBind.txtLikedReceived.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_text_primary
                        )
                    )
                    viewBind.txtLikedSent.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.black
                        )
                    )
                    (viewBind.txtLikedReceivedCount.parent as LinearLayout).background =
                        ContextCompat.getDrawable(activity, R.drawable.drawable_grey_circle)
                    (viewBind.txtLikedSentCount.parent as LinearLayout).background =
                        ContextCompat.getDrawable(activity, R.drawable.drawable_blue_circle)
                    activity.viewTypeReceivedLikes = false
                    activity.apiController.currentSentPage = 0
                    activity.apiController.callApi()
                }
            }
        }
    }
}