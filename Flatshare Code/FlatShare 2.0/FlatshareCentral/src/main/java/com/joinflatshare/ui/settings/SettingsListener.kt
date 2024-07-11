package com.joinflatshare.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivitySettingsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.profile.verify.ProfileVerifyActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 01/07/24
 */
class SettingsListener(
    private val activity: SettingsActivity,
    private val viewBind: ActivitySettingsBinding
) :
    View.OnClickListener {
    init {
        viewBind.txtProfileNameVerified.setOnClickListener(this)
        viewBind.txtProfileDobVerified.setOnClickListener(this)
        viewBind.txtProfileGenderVerified.setOnClickListener(this)
        viewBind.txtProfileProfessionUpdate.setOnClickListener(this)
        viewBind.txtProfileBlocked.setOnClickListener(this)
        viewBind.txtProfilePrivacy.setOnClickListener(this)
        viewBind.txtProfileTerms.setOnClickListener(this)
        viewBind.cardProfileLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            viewBind.txtProfileNameVerified.id,
            viewBind.txtProfileDobVerified.id,
            viewBind.txtProfileGenderVerified.id -> {
                val intent = Intent(activity, ProfileVerifyActivity::class.java)
                CommonMethod.switchActivity(
                    activity,
                    intent
                ) { result ->
                    if (result?.resultCode == Activity.RESULT_OK) {
                        SettingsViewBind(viewBind)
                    }
                }
            }

            viewBind.txtProfileProfessionUpdate.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Student"))
                list.add(ModelBottomSheet(0, "Working Professional"))
                BottomSheetView(activity, list) { _, position ->
                    AppConstants.loggedInUser?.profession = list[position].name
                    activity.baseApiController.updateUser(
                        false,
                        AppConstants.loggedInUser,
                        object : OnUserFetched {
                            override fun userFetched(resp: UserResponse?) {
                                SettingsViewBind(viewBind)
                            }

                        })
                }
            }

            viewBind.txtProfileBlocked.id -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.joinflatshare.com")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.txtProfilePrivacy.id -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.joinflatshare.com/privacy.php")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.txtProfileTerms.id -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.joinflatshare.com/terms.php")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.cardProfileLogout.id -> {
                CommonMethod.logout(activity)
            }
        }
    }
}