package com.joinflatshare.ui.profile.details

import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.db.FlatshareDbManager
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.utils.amazonaws.AmazonFileChecker
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 27/05/24
 */
class ProfileDetailsDataBinder(
    private val activity: ProfileDetailsActivity,
    private val viewBinding: ActivityProfileDetailsBinding
) {
    internal fun setData() {
        val userData = activity.user

        // Profile Image
        if (!userData?.dp.isNullOrEmpty()) {
            AmazonFileChecker(activity).checkObject(
                userData?.dp
            ) { _, requestCode ->
                if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                    viewBinding.cardProfilePic.visibility = View.VISIBLE
                    viewBinding.cardProfilePic.viewTreeObserver.addOnGlobalLayoutListener(object :
                        OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewBinding.cardProfilePic.viewTreeObserver.removeOnGlobalLayoutListener(
                                this
                            )
                            viewBinding.cardProfilePic.layoutParams.height =
                                viewBinding.cardProfilePic.width
                            viewBinding.cardProfilePic.invalidate()
                            ImageHelper.loadImageWithCacheClear(
                                activity,
                                0,
                                viewBinding.imgProfilePic,
                                UrlConstants.IMAGE_URL + userData?.dp
                            )
                        }
                    })
                } else viewBinding.cardProfilePic.visibility = View.GONE
            }
        }

        // Verified
        if (userData!!.verification?.isVerified == true) {
            activity.baseViewBinder.img_topbar_text_header.visibility = View.VISIBLE
            activity.baseViewBinder.img_topbar_text_header.setImageResource(R.drawable.ic_tick_verified_blue)
        }

        // Age
        viewBinding.txtProfileAge.text = CommonMethod.getAge(userData.dob)

        // Interests
        if (userData.flatProperties.interests.isNullOrEmpty())
            viewBinding.llProfileInterests.visibility = View.GONE
        else
            viewBinding.txtProfileInterest.text =
                TextUtils.join(", ", userData.flatProperties.interests)

        // Languages
        if (userData.flatProperties.languages.isNullOrEmpty())
            viewBinding.llProfileLanguages.visibility = View.GONE
        else
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

        if (userData.college == null || userData.college!!.loc.coordinates.isEmpty() || userData.college?.name.isNullOrEmpty())
            viewBinding.llProfileEducation.visibility = View.GONE
        else {
            viewBinding.llProfileEducation.visibility = View.VISIBLE
            viewBinding.edtProfileEducation.text = userData.college?.name
        }

        if (userData.hometown == null || userData.hometown!!.loc.coordinates.isEmpty() || userData.hometown?.name.isNullOrEmpty())
            viewBinding.llProfileHometown.visibility = View.GONE
        else {
            viewBinding.llProfileHometown.visibility = View.VISIBLE
            viewBinding.edtProfileHometown.text = userData.hometown?.name
        }


        // IMAGE
        /*activity.image.clear()
        if (userData.images.isNotEmpty()) {
            activity.image.addAll(userData.images)
            activity.image.reverse()
            viewBinding.rvProfileInfoImage.visibility = View.VISIBLE
            adapter?.setItems(activity.image)
        } else viewBinding.rvProfileInfoImage.visibility = View.GONE*/


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