package com.joinflatshare.ui.profile.myprofile

import android.os.Bundle
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 21/05/24
 */
class ProfileActivity : BaseActivity() {
    private lateinit var viewBind: ActivityProfileBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showBottomMenu(this)
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        ProfileListener(this, viewBind)
    }

    internal fun setUserData() {
        ImageHelper.loadProfileImage(this, viewBind.imgPhoto, viewBind.txtPhoto, user)
        viewBind.txtProfileName.text =
            "${user?.name?.firstName} ${user?.name?.lastName}, ${CommonMethod.getAge(user?.dob)}"

        viewBind.txtProfileComplete.text = "${user?.completed?.toInt()}% COMPLETE"

        if (user?.verification?.isVerified == true) {
            viewBind.imgProfileVerified.setImageResource(R.drawable.ic_tick_verified_blue)
            viewBind.cardProfileVerified.visibility = View.GONE
        } else {
            viewBind.imgProfileVerified.setImageResource(R.drawable.ic_tick_verified)
            viewBind.cardProfileVerified.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        AppConstants.menuSelected = 3
        baseViewBinder.applyMenuClick()
        setUserData()
    }
}