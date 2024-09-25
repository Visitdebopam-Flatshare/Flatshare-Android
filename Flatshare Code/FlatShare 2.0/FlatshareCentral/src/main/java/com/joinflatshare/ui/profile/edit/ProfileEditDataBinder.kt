package com.joinflatshare.ui.profile.edit

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.pojo.user.User
import com.joinflatshare.utils.helper.ImageHelper

class ProfileEditDataBinder(
    private val activity: ProfileEditActivity,
    private val viewBind: ActivityProfileEditBinding
) {

    var adapter: ProfileEditImageAdapter? = null
    var edt_profile: Array<TextView> = arrayOf(
        viewBind.edtProfileCollege,
        viewBind.edtProfileHometown
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
        val user = AppConstants.loggedInUser
        if (!user?.flatProperties?.interests.isNullOrEmpty())
            viewBind.txtProfileInterest.text =
                TextUtils.join(", ", user?.flatProperties?.interests!!)
        if (!user?.flatProperties?.languages.isNullOrEmpty())
            viewBind.txtProfileLanguages.text =
                TextUtils.join(", ", user?.flatProperties?.languages!!)
        viewBind.edtProfileStatus.setText(user?.status)
        if (user?.profession=="Student") {
            viewBind.llProfileWork.visibility = View.GONE
        }
        else {
            viewBind.llProfileWork.visibility= View.VISIBLE
            viewBind.edtProfileWork.setText(user?.work)
            workLimit()
        }
        bioLimit()
        if (user?.hometown != null) {
            val hometown = user?.hometown
            if (hometown!!.name.isEmpty()) {
                edt_profile[0].text = ""
                activity.latProfile[0] = null
            } else {
                edt_profile[0].text =
                    HtmlCompat.fromHtml(hometown.name, HtmlCompat.FROM_HTML_MODE_LEGACY)
                activity.latProfile[0] = hometown.loc
            }
        }
        if (user!!.college != null) {
            val hangout = user!!.college
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
        loadProfileImage(user)
//        loadImages()
//        viewBind.rvProfileInfoImage.setAdapter(adapter)
    }

    private fun loadProfileImage(user: User) {
        ImageHelper.loadProfileImage(
            activity,
            viewBind.imgPhoto,
            viewBind.txtPhoto,
            user
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