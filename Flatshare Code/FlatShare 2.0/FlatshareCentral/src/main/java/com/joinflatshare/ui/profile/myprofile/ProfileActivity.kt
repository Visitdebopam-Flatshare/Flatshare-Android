package com.joinflatshare.ui.profile.myprofile

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding
import com.joinflatshare.constants.AppConstants
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
        ProfileListener(this, viewBind)
    }

    private fun setUserData() {
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        ImageHelper.loadProfileImage(this, viewBind.imgPhoto, viewBind.txtPhoto, user)
        // Name & DOB
        val name = "${user?.name?.firstName} ${user?.name?.lastName}"
        val dob = CommonMethod.getAge(user?.dob)
        viewBind.txtProfileName.text = if (dob.isEmpty()) name else "$name, $dob"

        setCompletionPercentage()

        if (user?.verification?.isVerified == true) {
            viewBind.imgProfileVerified.visibility = View.VISIBLE
            viewBind.cardProfileVerified.visibility = View.GONE
        } else {
            viewBind.imgProfileVerified.visibility = View.GONE
            viewBind.cardProfileVerified.visibility = View.VISIBLE
        }
    }

    private fun setCompletionPercentage() {
        val userPerc = user?.completedPercentage
        var percentage = 0
        if (userPerc != null)
            percentage = userPerc
        if (percentage == 100) {
            viewBind.cardProfileComplete.visibility = View.GONE
        } else {
            viewBind.cardProfileComplete.visibility = View.VISIBLE
            viewBind.txtProfileComplete.text = "$percentage% COMPLETE"
            viewBind.llProfileComplete.removeAllViews()
            for (i in 1 until percentage) {
                val view = View(this)
                view.layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_dark))
                viewBind.llProfileComplete?.addView(view)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        AppConstants.menuSelected = 3
        baseViewBinder.applyMenuClick()
        setUserData()
    }
}