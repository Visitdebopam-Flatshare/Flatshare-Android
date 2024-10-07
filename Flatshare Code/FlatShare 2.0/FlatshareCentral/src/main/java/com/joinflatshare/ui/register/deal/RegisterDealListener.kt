package com.joinflatshare.ui.register.deal

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterDealBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.register.about.RegisterAboutActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 04/02/24
 */
class RegisterDealListener(
    private val activity: RegisterDealActivity,
    private val viewBind: ActivityRegisterDealBinding
) : OnClickListener {

    init {
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnDealNext.setOnClickListener(this)
        viewBind.btnSkip.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.btnDealNext.id -> {
                val selectedDeals = activity.dealBreakerView.getDealBreakers()
                if (selectedDeals.smoking == 0 && selectedDeals.pets == 0 && selectedDeals.eggs == 0
                    && selectedDeals.flatparty == 0 && selectedDeals.nonveg == 0
                    && selectedDeals.workout == 0
                ) {
                    return
                }

                val user = AppConstants.loggedInUser
                user?.flatProperties?.dealBreakers = activity.dealBreakerView.getDealBreakers()
                activity.updateUser(user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        MixpanelUtils.onButtonClicked("Onboarding Deal Breaker Saved")
                        val intent = Intent(activity, RegisterAboutActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                })
            }

            viewBind.btnSkip.id -> {
                val user = AppConstants.loggedInUser
                user?.flatProperties?.dealBreakers = DealBreakers()
                activity.updateUser(user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        MixpanelUtils.onButtonClicked("Onboarding Deal Breaker Skipped")
                        val intent = Intent(activity, RegisterAboutActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                })
            }
        }
    }
}