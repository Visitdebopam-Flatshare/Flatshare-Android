package com.joinflatshare.ui.date

import android.app.Activity
import android.content.Intent
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityDatePreferencesBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.profile.myprofile.ProfileActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 14/07/23
 */
class DatePreferenceListener(
    private val activity: DatePreferenceActivity,
    private val viewBind: ActivityDatePreferencesBinding
) : View.OnClickListener {
    var isMaleSelected = false
    var isFemaleSelected = false
    var dateType: Int = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL

    init {
        switchListener()
        viewBind.cardProfileMale.setOnClickListener(this)
        viewBind.cardProfileFemale.setOnClickListener(this)
        viewBind.cardDateCasual.setOnClickListener(this)
        viewBind.cardDateLongTerm.setOnClickListener(this)
        viewBind.cardDatePartners.setOnClickListener(this)
        viewBind.txtDateClose.setOnClickListener(this)
        viewBind.btnDateSearch.setOnClickListener(this)
        activity.baseViewBinder.btn_back.setOnClickListener(this)
    }


    private fun switchListener() {
        viewBind.switchVerifiedMember.setOnCheckedChangeListener { _, isChecked ->
            var shouldCheck = false
            if (isChecked) {
                val user = FlatShareApplication.getDbInstance().userDao().getUser()
                if (user != null) {
                    if (user.verification?.isVerified == true) {
                        MixpanelUtils.onButtonClicked("GV on Date preferences screen")
                        shouldCheck = true
                    } else {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        intent.putExtra("verify", true)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }
            activity.user?.dateProperties?.isVerifiedOnly = shouldCheck
            viewBind.switchVerifiedMember.isChecked = shouldCheck
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.cardProfileMale.id -> {
                isMaleSelected = !isMaleSelected
                activity.dataBind.setGenderButton()
            }

            viewBind.cardProfileFemale.id -> {
                isFemaleSelected = !isFemaleSelected
                activity.dataBind.setGenderButton()
            }

            viewBind.cardDateCasual.id -> {
                dateType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL
                activity.dataBind.setDateButton()
            }

            viewBind.cardDateLongTerm.id -> {
                dateType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM
                activity.dataBind.setDateButton()
            }

            viewBind.cardDatePartners.id -> {
                dateType = ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS
                activity.dataBind.setDateButton()
            }

            viewBind.btnDateSearch.id,
            activity.baseViewBinder.btn_back.id -> {
                validate()
            }

            viewBind.txtDateClose.id -> {
                val user = FlatShareApplication.getDbInstance().userDao().getUser()
                user?.isDateSearch?.value = false
                activity.baseApiController.updateUser(true,
                    user, object : OnUserFetched {
                        override fun userFetched(resp: UserResponse?) {
                            CommonMethod.makeToast("Vibe Check turned off")
                            AppConstants.isFeedReloadRequired = true
                            activity.setResult(Activity.RESULT_OK)
                            MixpanelUtils.onButtonClicked("Close Date Search")
                            CommonMethod.finishActivity(activity)
                        }

                    })
            }
        }
    }

    private fun validate() {
        val actualUserData = FlatShareApplication.getDbInstance().userDao().getUser()
        var hasChanged = false
        when (dateType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM ->
                activity.user?.dateProperties?.plans = activity.dataBind.adapter.getMatchedItems()

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                activity.user?.dateProperties?.activities =
                    activity.dataBind.adapter.getMatchedItems()
            }
        }

        if (actualUserData?.dateProperties?.isVerifiedOnly != activity.user?.dateProperties?.isVerifiedOnly)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dateType != activity.user?.dateProperties?.dateType) {
            hasChanged = true
        } else if (!actualUserData?.dateProperties?.gender.equals(activity.user?.dateProperties?.gender)) {
            hasChanged = true
        } else if (actualUserData?.dateProperties?.plans != activity.user?.dateProperties?.plans) {
            hasChanged = true
        } else if (actualUserData?.dateProperties?.activities != activity.user?.dateProperties?.activities)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers == null && activity.user?.dateProperties?.dealBreakers != null) {
            hasChanged = true
        } else if (actualUserData?.dateProperties?.dealBreakers?.smoking != activity.user?.dateProperties?.dealBreakers?.smoking)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers?.nonveg != activity.user?.dateProperties?.dealBreakers?.nonveg)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers?.flatparty != activity.user?.dateProperties?.dealBreakers?.flatparty)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers?.eggs != activity.user?.dateProperties?.dealBreakers?.eggs)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers?.pets != activity.user?.dateProperties?.dealBreakers?.pets)
            hasChanged = true
        else if (actualUserData?.dateProperties?.dealBreakers?.workout != activity.user?.dateProperties?.dealBreakers?.workout)
            hasChanged = true

        if (hasChanged)
            activity.baseApiController.updateUser(true,
                activity.user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        AppConstants.isFeedReloadRequired = true
                        val intent = Intent()
                        activity.setResult(Activity.RESULT_OK, intent)
                        CommonMethod.finishActivity(activity)
                    }

                })
        else CommonMethod.finishActivity(activity)
    }
}