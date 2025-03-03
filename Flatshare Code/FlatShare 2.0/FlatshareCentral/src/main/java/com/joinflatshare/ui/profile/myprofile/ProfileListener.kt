package com.joinflatshare.ui.profile.myprofile

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.View.OnClickListener
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.ui.faq.FaqActivity
import com.joinflatshare.ui.invite.InviteActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.ui.profile.edit.ProfileEditActivity
import com.joinflatshare.ui.profile.verify.ProfileVerifyActivity
import com.joinflatshare.ui.settings.SettingsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 21/05/24
 */
class ProfileListener(
    private val activity: ProfileActivity,
    private val viewBind: ActivityProfileBinding
) : OnClickListener {

    init {
        viewBind.cardProfileVerified.setOnClickListener(this)
        viewBind.cardProfileInvite.setOnClickListener(this)
        viewBind.cardProfileFaq.setOnClickListener(this)
        viewBind.imgProfileSettings.setOnClickListener(this)
        viewBind.imgProfileEdit.setOnClickListener(this)
        viewBind.framePhoto.setOnClickListener(this)
        viewBind.cardProfileRentedFlats.setOnClickListener(this)
        viewBind.cardProfileListFlat.setOnClickListener(this)
        viewBind.cardProfileEarnMoney.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            viewBind.cardProfileVerified.id -> {
                val intent = Intent(activity, ProfileVerifyActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.imgProfileSettings.id -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.cardProfileInvite.id -> {
                val intent = Intent(activity, InviteActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.cardProfileFaq.id -> {
                val intent = Intent(activity, FaqActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.imgProfileEdit.id -> {
                val intent = Intent(activity, ProfileEditActivity::class.java)
                CommonMethod.switchActivity(activity, intent, false)
            }

            viewBind.cardProfileListFlat.id -> {
                MixpanelUtils.onButtonClicked("List my flat")
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://joinflatshare.com/list-my-flat")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.cardProfileRentedFlats.id -> {
                MixpanelUtils.onButtonClicked("Verified Rental Flats")
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://joinflatshare.com")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.cardProfileEarnMoney.id -> {
                MixpanelUtils.onButtonClicked("Earn Money")
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://joinflatshare.com/earn-money")
                )
                CommonMethod.switchActivity(activity, browserIntent, false)
            }

            viewBind.framePhoto.id -> {
                val intent = Intent(activity, ProfileDetailsActivity::class.java)
                intent.putExtra("phone", AppConstants.loggedInUser?.id)
                CommonMethod.switchActivity(activity, intent, false)
            }
        }
    }
}