package com.joinflatshare.ui.profile.interest

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileInterestBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.profile.location.LocationActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 08/03/24
 */
class InterestActivity : BaseActivity() {
    private lateinit var viewBind: ActivityProfileInterestBinding
    private lateinit var interestsView: InterestsView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileInterestBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
        click()
    }

    private fun init() {
        interestsView = InterestsView(
            this, viewBind.rvInterests,
            InterestsView.VIEW_TYPE_INTERESTS, false
        )
        if (!AppConstants.loggedInUser?.flatProperties?.interests.isNullOrEmpty())
            interestsView.matchedContent.addAll(AppConstants.loggedInUser?.flatProperties?.interests!!)
        interestsView.assignCallback { _, _ ->
            switchButtonBackground()
        }
        interestsView.show()
        switchButtonBackground()
    }

    private fun switchButtonBackground() {
        if (interestsView.matchedContent.isNotEmpty()) {
            viewBind.btnInterests.background = ContextCompat.getDrawable(
                this@InterestActivity,
                R.drawable.drawable_button_blue
            )
        } else viewBind.btnInterests.background = ContextCompat.getDrawable(
            this@InterestActivity,
            R.drawable.drawable_button_light_blue
        )
    }


    private fun click() {
        viewBind.btnBack.setOnClickListener {
            CommonMethod.finishActivity(this)
        }
        viewBind.btnSkip.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            CommonMethod.switchActivity(this, intent, false)
        }
        viewBind.btnInterests.setOnClickListener {
            if (interestsView.matchedContent.isNotEmpty()) {
                val user = AppConstants.loggedInUser
                user?.flatProperties?.interests?.clear()
                user?.flatProperties?.interests?.addAll(interestsView.matchedContent)
                baseApiController.updateUser(true, user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        val intent = Intent(this@InterestActivity, LocationActivity::class.java)
                        CommonMethod.switchActivity(this@InterestActivity, intent, false)
                    }

                })
            }
        }
    }
}