package com.joinflatshare.ui.settings

import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivitySettingsBinding
import com.joinflatshare.utils.helper.DateUtils

/**
 * Created by debopam on 01/07/24
 */
class SettingsViewBind(
    viewBind: ActivitySettingsBinding
) {
    init {
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        viewBind.txtProfileName.text = "Name: " + user?.name?.firstName + " " + user?.name?.lastName
        viewBind.txtProfileDob.text = "DOB: " + DateUtils.getDOB(user?.dob)
        viewBind.txtProfileGender.text = "Gender: " + user?.gender
        viewBind.txtProfileProfession.text = "I'm a: " + user?.profession
        viewBind.txtProfileMobile.text = "Phone no: " + user?.id

        if (user?.verification?.isVerified == false) {
            viewBind.txtProfileNameVerified.visibility = View.VISIBLE
            viewBind.txtProfileDobVerified.visibility = View.VISIBLE
            viewBind.txtProfileGenderVerified.visibility = View.VISIBLE
        } else {
            viewBind.txtProfileNameVerified.visibility = View.GONE
            viewBind.txtProfileDobVerified.visibility = View.GONE
            viewBind.txtProfileGenderVerified.visibility = View.GONE
        }
    }
}