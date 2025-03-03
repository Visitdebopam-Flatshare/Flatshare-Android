package com.joinflatshare.ui.profile.language

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileLanguageBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.profile.interest.InterestActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 08/03/24
 */
class LanguageActivity : BaseActivity() {
    private lateinit var viewBind: ActivityProfileLanguageBinding
    private lateinit var interestsView: InterestsView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileLanguageBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "2")
        init()
        click()
    }

    private fun init() {
        interestsView = InterestsView(
            this, viewBind.rvLanguages,
            InterestsView.VIEW_TYPE_LANGUAGES, false
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
            MixpanelUtils.onScreenOpened("Edit Profile Language")
            viewBind.btnSkip.visibility = View.INVISIBLE
            viewBind.btnLanguages.text = "Save"
            interestsView.matchedContent.addAll(
                TextUtils.split(
                    intent.getStringExtra("content"),
                    ", "
                )
            )
        } else {
            MixpanelUtils.onScreenOpened("Onboarding Language")
            val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
            if (response?.data?.allowedSkips?.isSkippingLanguagesAllowed == false) {
                viewBind.btnSkip.visibility = View.INVISIBLE
            }
            if (!AppConstants.loggedInUser?.flatProperties?.languages.isNullOrEmpty())
                interestsView.matchedContent.addAll(AppConstants.loggedInUser?.flatProperties?.languages!!)
        }
    }

    private fun switchButtonBackground() {
        if (interestsView.matchedContent.isNotEmpty()) {
            viewBind.btnLanguages.background = ContextCompat.getDrawable(
                this,
                R.drawable.drawable_button_blue
            )
        } else viewBind.btnLanguages.background = ContextCompat.getDrawable(
            this,
            R.drawable.drawable_button_light_blue
        )
    }

    private fun click() {
        viewBind.btnBack.setOnClickListener {
            CommonMethod.finishActivity(this)
        }
        viewBind.btnSkip.setOnClickListener {
            MixpanelUtils.onButtonClicked("Language Skipped")
            val user = AppConstants.loggedInUser
            user?.flatProperties?.languages?.clear()
            user?.flatProperties?.languages = ArrayList()
            baseApiController.updateUser(true, user, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    MixpanelUtils.onButtonClicked("Onboarding Language Skipped")
                    val intent = Intent(this@LanguageActivity, InterestActivity::class.java)
                    CommonMethod.switchActivity(this@LanguageActivity, intent, false)
                }

            })
        }
        viewBind.btnLanguages.setOnClickListener {
            when (viewBind.btnLanguages.text) {
                "Save" -> {
                    MixpanelUtils.onButtonClicked("Language Saved")
                    if (interestsView.matchedContent.isNotEmpty()) {
                        val intent = Intent()
                        intent.putExtra("type", InterestsView.VIEW_TYPE_LANGUAGES)
                        intent.putExtra(
                            "language",
                            TextUtils.join(", ", interestsView.matchedContent)
                        )
                        setResult(Activity.RESULT_OK, intent)
                        CommonMethod.finishActivity(this)
                    }
                }

                "Next" -> {
                    MixpanelUtils.onButtonClicked("Language Saved")
                    if (interestsView.matchedContent.isNotEmpty()) {
                        val user = AppConstants.loggedInUser
                        user?.flatProperties?.languages?.clear()
                        user?.flatProperties?.languages = ArrayList()
                        user?.flatProperties?.languages?.addAll(interestsView.matchedContent)
                        baseApiController.updateUser(true, user, object : OnUserFetched {
                            override fun userFetched(resp: UserResponse?) {
                                MixpanelUtils.onButtonClicked("Onboarding Language Saved")
                                val intent =
                                    Intent(this@LanguageActivity, InterestActivity::class.java)
                                CommonMethod.switchActivity(this@LanguageActivity, intent, false)
                            }

                        })
                    }
                }
            }
        }
    }
}