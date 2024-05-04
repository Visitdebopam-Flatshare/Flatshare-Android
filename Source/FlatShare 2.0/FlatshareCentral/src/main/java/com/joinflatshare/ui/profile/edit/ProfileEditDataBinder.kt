package com.joinflatshare.ui.profile.edit

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.AppConstants
import java.util.Collections

class ProfileEditDataBinder(
    private val activity: ProfileEditActivity,
    private val viewBind: ActivityProfileEditBinding
) {

    var adapter: ProfileEditImageAdapter? = null
    var edt_profile: Array<TextView> = arrayOf(
        viewBind.edtProfileHometown,
        viewBind.edtProfileCollege,
        viewBind.edtProfileHangout
    )
    var imgSearch: Array<ImageView> = arrayOf(
        viewBind.imgSearch1,
        viewBind.imgSearch2,
        viewBind.imgSearch3
    )
    var view_profile: Array<View> = arrayOf(
        viewBind.view1,
        viewBind.view2,
        viewBind.view3
    )

    // Image Handlers
    @kotlin.jvm.JvmField
    var apiUserImages = ArrayList<String>()

    @kotlin.jvm.JvmField
    var adapterUserImages = ArrayList<String>()

    @kotlin.jvm.JvmField
    var deletedUserImages = ArrayList<String>()

    @kotlin.jvm.JvmField
    var addedUserImages = ArrayList<String>()

    init {
        activity.baseViewBinder.view_topbar_divider.visibility = View.GONE
        viewBind.rvProfileInfoImage.setLayoutManager(
            LinearLayoutManager(
                activity,
                RecyclerView.HORIZONTAL,
                false
            )
        )
        adapter =
            ProfileEditImageAdapter(
                activity
            )
        createProgressBar()
        setData()
    }

    private fun createProgressBar() {
        viewBind.llProgress.removeAllViews()
        val totalProgress = 9
        for (i in 0 until totalProgress) {
            val tv = TextView(activity)
            tv.layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f)
            tv.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_hint))
            viewBind.llProgress.addView(tv)
        }
    }

    private fun setData() {
        viewBind.txtProfileInterest.text =
            TextUtils.join(", ", AppConstants.loggedInUser?.flatProperties?.interests!!)
        viewBind.txtProfileLanguages.text =
            TextUtils.join(", ", AppConstants.loggedInUser?.flatProperties?.languages!!)
        viewBind.txtInterestCount.text =
            "(${AppConstants.loggedInUser?.flatProperties?.interests?.size}/5)"
        viewBind.txtLanguageCount.text =
            "(${AppConstants.loggedInUser?.flatProperties?.languages?.size}/3)"
        viewBind.edtProfileWebsite.setText(AppConstants.loggedInUser?.website)
        viewBind.edtProfileStatus.setText(AppConstants.loggedInUser?.status)
        viewBind.txtProfileStatus.setText("About ${AppConstants.loggedInUser?.name?.firstName} and Looking for")
        bioLimit()
        viewBind.edtProfileWork.setText(AppConstants.loggedInUser?.work)
        if (AppConstants.loggedInUser?.hometown != null) {
            val hometown = AppConstants.loggedInUser?.hometown
            if (hometown!!.name.isEmpty()) {
                edt_profile[0].text = ""
                activity.latProfile[0] = null
            } else {
                edt_profile[0].text =
                    HtmlCompat.fromHtml(hometown.name, HtmlCompat.FROM_HTML_MODE_LEGACY)
                activity.latProfile[0] = hometown.loc
            }
        }
        if (AppConstants.loggedInUser!!.college != null) {
            val hangout = AppConstants.loggedInUser!!.college
            if (hangout!!.name.isEmpty()) {
                edt_profile[1].text = ""
                activity.latProfile[1] = null
            } else {
                edt_profile[1].text =
                    HtmlCompat.fromHtml(hangout.name, HtmlCompat.FROM_HTML_MODE_LEGACY)
                activity.latProfile[1] = hangout.loc
            }
        }
        if (AppConstants.loggedInUser!!.hangout != null) {
            val hangout = AppConstants.loggedInUser!!.hangout
            if (hangout!!.name.isEmpty()) {
                edt_profile[2].text = ""
                activity.latProfile[2] = null
            } else {
                edt_profile[2].text =
                    HtmlCompat.fromHtml(hangout.name, HtmlCompat.FROM_HTML_MODE_LEGACY)
                activity.latProfile[2] = hangout.loc
            }
        }


        // Image
        loadImages()
        viewBind.rvProfileInfoImage.setAdapter(adapter)
    }

    private fun loadImages() {
        adapterUserImages.clear()
        apiUserImages.clear()
        apiUserImages.addAll(AppConstants.loggedInUser!!.images)
        adapterUserImages.addAll(AppConstants.loggedInUser!!.images)
        Collections.reverse(adapterUserImages)
        if (adapterUserImages.size < 10) adapterUserImages.add(0, "")
        adapter?.items = adapterUserImages
        setCompleteCount()
    }

    fun bioLimit() {
        val due: Int = 150 - viewBind.edtProfileStatus.getText().toString().length
        viewBind.txtProfileStatusLimit.text = "" + due
    }

    fun setCompleteCount() {
        var completeCount = 0
        val items = adapter?.items!!
        if (items.size > 1) completeCount++
        if (!viewBind.txtProfileInterest.getText().toString().isEmpty()) completeCount++
        if (!viewBind.txtProfileLanguages.getText().toString().isEmpty()) completeCount++
        if (!viewBind.edtProfileWebsite.getText().toString().isEmpty()) completeCount++
        if (!viewBind.edtProfileStatus.getText().toString().isEmpty()) completeCount++
        if (!viewBind.edtProfileWork.getText().toString().isEmpty()) completeCount++
        for (customTextViewRegular in edt_profile) {
            if (!customTextViewRegular.text.toString().isEmpty()) completeCount++
        }
        for (i in 0 until viewBind.llProgress.childCount) {
            if (viewBind.llProgress.getChildAt(i) is TextView) {
                val txt = viewBind.llProgress.getChildAt(i) as TextView
                if (i < completeCount) {
                    txt.setBackgroundColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.blue
                        )
                    )
                } else {
                    txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_hint))
                }
            }
        }
    }
}