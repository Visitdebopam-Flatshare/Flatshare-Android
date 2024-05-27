package com.joinflatshare.ui.profile.details

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.db.FlatshareDbManager
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.ui.profile.edit.ProfileEditImageAdapter
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 27/05/24
 */
class ProfileDetailsDataBinder(
    private val activity: ProfileDetailsActivity,
    private val viewBinding: ActivityProfileDetailsBinding
) {
    private var adapter: ProfileEditImageAdapter? = null

    init {
        viewBinding.rvProfileInfoImage.layoutManager = GridLayoutManager(activity, 3)
        viewBinding.rvProfileInfoImage.addItemDecoration(
            GridSpacingItemDecoration(
                3, 30, false
            )
        )
        adapter = ProfileEditImageAdapter(activity)
        viewBinding.rvProfileInfoImage.adapter = adapter
    }

    internal fun setData() {
        val userData = activity.user

        // Verified
        activity.baseViewBinder.img_topbar_text_header.visibility = View.VISIBLE
        if (userData!!.verification?.isVerified == true) {
            activity.baseViewBinder.img_topbar_text_header.setImageResource(R.drawable.ic_tick_verified_blue)
        } else activity.baseViewBinder.img_topbar_text_header.setImageResource(R.drawable.ic_tick_verified)

        // Age
        viewBinding.txtProfileAge.text = CommonMethod.getAge(userData.dob)

        // Interests
        viewBinding.txtProfileInterest.text =
            TextUtils.join(", ", userData.flatProperties.interests)

        // Languages
        viewBinding.txtProfileLanguages.text =
            TextUtils.join(", ", userData.flatProperties.languages)

        if (userData.status.isNullOrEmpty())
            viewBinding.llProfileStatus.visibility = View.GONE
        else {
            viewBinding.llProfileStatus.visibility = View.VISIBLE
            viewBinding.edtProfileStatus.text = userData.status
        }

        if (userData.work.isNullOrEmpty())
            viewBinding.llProfileWork.visibility = View.GONE
        else {
            viewBinding.llProfileWork.visibility = View.VISIBLE
            viewBinding.edtProfileWork.text = userData.work
        }

        if (userData.college == null || userData.college!!.loc.coordinates.isEmpty())
            viewBinding.llProfileEducation.visibility = View.GONE
        else {
            viewBinding.llProfileEducation.visibility = View.VISIBLE
            viewBinding.edtProfileEducation.text = userData.college?.name
        }

        if (userData.hometown == null || userData.hometown!!.loc.coordinates.isEmpty())
            viewBinding.llProfileHometown.visibility = View.GONE
        else {
            viewBinding.llProfileHometown.visibility = View.VISIBLE
            viewBinding.edtProfileHometown.text = userData.hometown?.name
        }


        // IMAGE
        activity.image.clear()
        if (userData.images.isNotEmpty()) {
            activity.image.addAll(userData.images)
            activity.image.reverse()
            viewBinding.rvProfileInfoImage.visibility = View.VISIBLE
            adapter?.setItems(activity.image)
        } else viewBinding.rvProfileInfoImage.visibility = View.GONE


        // Flatscore
        if (userData.score == 0)
            viewBinding.llProfileFlatscore.visibility = View.GONE
        else {
            viewBinding.llProfileFlatscore.visibility = View.VISIBLE
            viewBinding.edtProfileFlatscore.text = "${userData.score}"
        }

        // Report
        if (userData.id == FlatshareDbManager.getInstance(activity).userDao()
                .get(UserDao.USER_CONSTANT_USERID)
        ) {
            viewBinding.llProfileBottom.visibility = View.GONE
        }
    }
}