package com.joinflatshare.ui.profile.interest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileInterestBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.register.deal.RegisterDealActivity
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
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "3")
        init()
        click()
    }

    private fun init() {
        interestsView = InterestsView(
            this, viewBind.rvInterests,
            InterestsView.VIEW_TYPE_INTERESTS, false
        )
        checkIntent()
        interestsView.assignCallback { _, _ ->
            switchButtonBackground()
        }
        interestsView.show()
        switchButtonBackground()
    }

    private fun checkIntent() {
        val isEdit = intent.getBooleanExtra("edit", false)
        if (isEdit) {
            viewBind.btnSkip.visibility = View.INVISIBLE
            viewBind.btnInterests.text = "Save"
            interestsView.matchedContent.addAll(
                TextUtils.split(
                    intent.getStringExtra("content"),
                    ", "
                )
            )
        } else {
            val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
            if (response?.data?.allowedSkips?.isSkippingInterestsAllowed == false) {
                viewBind.btnSkip.visibility = View.INVISIBLE
            }
            if (!AppConstants.loggedInUser?.flatProperties?.interests.isNullOrEmpty())
                interestsView.matchedContent.addAll(AppConstants.loggedInUser?.flatProperties?.interests!!)
        }
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
            val user = AppConstants.loggedInUser
            user?.flatProperties?.interests?.clear()
            user?.flatProperties?.interests = ArrayList()
            baseApiController.updateUser(true, user, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    val intent = Intent(this@InterestActivity, RegisterDealActivity::class.java)
                    CommonMethod.switchActivity(this@InterestActivity, intent, false)
                }
            })
        }
        viewBind.btnInterests.setOnClickListener {
            when (viewBind.btnInterests.text) {
                "Save" -> {
                    if (interestsView.matchedContent.isNotEmpty()) {
                        val intent = Intent()
                        intent.putExtra("type", InterestsView.VIEW_TYPE_INTERESTS)
                        intent.putExtra(
                            "interest",
                            TextUtils.join(", ", interestsView.matchedContent)
                        )
                        setResult(Activity.RESULT_OK, intent)
                        CommonMethod.finishActivity(this)
                    }
                }

                "Next" -> {
                    if (interestsView.matchedContent.isNotEmpty()) {
                        val user = AppConstants.loggedInUser
                        user?.flatProperties?.interests?.clear()
                        user?.flatProperties?.interests = ArrayList()
                        user?.flatProperties?.interests?.addAll(interestsView.matchedContent)
                        baseApiController.updateUser(true, user, object : OnUserFetched {
                            override fun userFetched(resp: UserResponse?) {
                                val intent =
                                    Intent(this@InterestActivity, RegisterDealActivity::class.java)
                                CommonMethod.switchActivity(this@InterestActivity, intent, false)
                            }

                        })
                    }
                }
            }
        }
    }
}