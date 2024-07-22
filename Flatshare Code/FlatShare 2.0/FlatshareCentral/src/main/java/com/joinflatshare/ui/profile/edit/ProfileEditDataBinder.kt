package com.joinflatshare.ui.profile.edit

import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.utils.helper.ImageHelper

class ProfileEditDataBinder(
    private val activity: ProfileEditActivity,
    private val viewBind: ActivityProfileEditBinding
) {

    var adapter: ProfileEditImageAdapter? = null
    var edt_profile: Array<TextView> = arrayOf(
        viewBind.edtProfileHometown,
        viewBind.edtProfileCollege
    )
    var imgSearch: Array<ImageView> = arrayOf(
        viewBind.imgSearch1,
        viewBind.imgSearch2
    )

    // Image Handlers
    var apiUserImages = ArrayList<String>()
    var adapterUserImages = ArrayList<String>()
    var deletedUserImages = ArrayList<String>()
    var addedUserImages = ArrayList<String>()

    init {
        viewBind.rvProfileInfoImage.layoutManager = GridLayoutManager(activity, 3)
        viewBind.rvProfileInfoImage.addItemDecoration(
            GridSpacingItemDecoration(
                3, 30, false
            )
        )
        adapter =
            ProfileEditImageAdapter(activity)
        setData()
    }

    private fun setData() {
        viewBind.txtProfileInterest.text =
            TextUtils.join(", ", AppConstants.loggedInUser?.flatProperties?.interests!!)
        viewBind.txtProfileLanguages.text =
            TextUtils.join(", ", AppConstants.loggedInUser?.flatProperties?.languages!!)
        viewBind.edtProfileStatus.setText(AppConstants.loggedInUser?.status)
        viewBind.edtProfileWork.setText(AppConstants.loggedInUser?.work)
        bioLimit()
        workLimit()
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


        // Image
        loadProfileImage()
//        loadImages()
//        viewBind.rvProfileInfoImage.setAdapter(adapter)
    }

    private fun loadProfileImage() {
        ImageHelper.loadProfileImage(
            activity,
            viewBind.imgPhoto,
            viewBind.txtPhoto,
            AppConstants.loggedInUser
        )
    }

    private fun loadImages() {
        adapterUserImages.clear()
        val images = AppConstants.loggedInUser?.images
        if (!images.isNullOrEmpty()) {
            if (images.size > 5) {
                for (i in 0..4) {
                    adapterUserImages.add(images[i])
                }
            } else {
                adapterUserImages.addAll(images)
            }
        }

        var dp = AppConstants.loggedInUser?.dp
        if (dp.isNullOrEmpty())
            dp = ""
        adapterUserImages.add(0, dp)
        if (adapterUserImages.size < 6) {
            val due = 6 - adapterUserImages.size
            for (i in 0 until due) {
                adapterUserImages.add("")
            }
        }
        adapter?.setItems(adapterUserImages)
    }

    internal fun bioLimit() {
        val due: Int = 150 - viewBind.edtProfileStatus.getText().toString().length
        viewBind.txtProfileStatusLimit.text = "" + due
    }

    internal fun workLimit() {
        var due: Int = 150 - viewBind.edtProfileWork.getText().toString().length
        viewBind.txtProfileWorkLimit.text = "" + due
    }
}