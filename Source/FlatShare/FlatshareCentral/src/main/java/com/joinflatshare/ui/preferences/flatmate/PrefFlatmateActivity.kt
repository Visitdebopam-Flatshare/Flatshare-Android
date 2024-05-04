package com.joinflatshare.ui.preferences.flatmate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatmateBinding
import com.joinflatshare.customviews.deal_breakers.DealBreakerCallback
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator

class PrefFlatmateActivity : BaseActivity(), OnUiEventClick {
    lateinit var viewBind: ActivityPrefFlatmateBinding
    lateinit var listener: PrefFlatmateListener
    var updatingFlatData = FlatShareApplication.getDbInstance().userDao().getFlatData()
    var dealBreakerView: DealBreakerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityPrefFlatmateBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        listener = PrefFlatmateListener(this)
        bind()
    }

    private fun bind() {
        setCopyLink()
        setVerified()
        setGender()
        setInterests()
        setLanguages()
        setDealBreakers()
    }

    private fun setCopyLink() {
        if (!FlatShareMessageGenerator.isFlatDataAvailableToShare(updatingFlatData))
            viewBind.txtPrefFlatmateCopyLink.visibility = View.GONE
    }

    private fun setVerified() {
        viewBind.includePrefFlatmate.switchVerifiedMember.isChecked =
            updatingFlatData?.flatProperties?.isVerifiedOnly == true
    }

    private fun setGender() {
        listener.isMaleSelected = false
        listener.isFemaleSelected = false
        if (updatingFlatData?.flatProperties?.gender?.equals("Male") == true) {
            listener.isMaleSelected = true
            listener.isFemaleSelected = false
        } else if (updatingFlatData?.flatProperties?.gender?.equals("Female") == true) {
            listener.isMaleSelected = false
            listener.isFemaleSelected = true
        } else if (updatingFlatData?.flatProperties?.gender?.equals("Both") == true) {
            listener.isMaleSelected = true
            listener.isFemaleSelected = true
        }
        setGenderButton()
    }

    fun setGenderButton() {
        // Male
        viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
            ContextCompat.getColor(this, android.R.color.transparent)
        )
        viewBind.includePrefFlatmate.txtMale.setTextColor(
            ContextCompat.getColor(this, R.color.color_text_primary)
        )
        // Female
        viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
            ContextCompat.getColor(this, android.R.color.transparent)
        )
        viewBind.includePrefFlatmate.txtFemale.setTextColor(
            ContextCompat.getColor(this, R.color.color_text_primary)
        )
        updatingFlatData?.flatProperties?.gender = ""
        if (listener.isMaleSelected && listener.isFemaleSelected) {
            viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.button_bg_black)
            )
            viewBind.includePrefFlatmate.txtMale.setTextColor(
                ContextCompat.getColor(this, R.color.color_text_secondary)
            )
            viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.button_bg_black)
            )
            viewBind.includePrefFlatmate.txtFemale.setTextColor(
                ContextCompat.getColor(this, R.color.color_text_secondary)
            )
            updatingFlatData?.flatProperties?.gender = "Both"
        } else {
            if (listener.isMaleSelected) {
                viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.button_bg_black)
                )
                viewBind.includePrefFlatmate.txtMale.setTextColor(
                    ContextCompat.getColor(this, R.color.color_text_secondary)
                )
                updatingFlatData?.flatProperties?.gender = "Male"
            } else if (listener.isFemaleSelected) {
                viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.button_bg_black)
                )
                viewBind.includePrefFlatmate.txtFemale.setTextColor(
                    ContextCompat.getColor(this, R.color.color_text_secondary)
                )
                updatingFlatData?.flatProperties?.gender = "Female"
            }
        }
        viewBind.includePrefFlatmate.cardProfileMale.invalidate()
        viewBind.includePrefFlatmate.cardProfileFemale.invalidate()
    }

    private fun setInterests() {
        if (!updatingFlatData?.flatProperties?.interests.isNullOrEmpty()) {
            viewBind.includePrefFlatmate.txtProfileInterest.text =
                TextUtils.join(", ", updatingFlatData?.flatProperties?.interests!!)
        }
    }

    private fun setLanguages() {
        if (!updatingFlatData?.flatProperties?.languages.isNullOrEmpty()) {
            viewBind.includePrefFlatmate.txtProfileLanguages.text =
                TextUtils.join(", ", updatingFlatData?.flatProperties?.languages!!)
        }
    }

    private fun setDealBreakers() {
        dealBreakerView = DealBreakerView(this, viewBind.includePrefFlatmate.rvFlatDeals)
        val callback = object : DealBreakerCallback {
            override fun onSmokingSelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.smoking = constant
            }

            override fun onNonVegSelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.nonveg = constant
            }

            override fun onPartySelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.flatparty = constant
            }

            override fun onEggsSelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.eggs = constant
            }

            override fun onWorkoutSelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.workout = constant
            }

            override fun onPetsSelected(constant: Int) {
                if (updatingFlatData?.flatProperties?.dealBreakers == null)
                    updatingFlatData?.flatProperties?.dealBreakers = DealBreakers()
                updatingFlatData?.flatProperties?.dealBreakers?.pets = constant
            }
        }

        dealBreakerView?.setDealValues(updatingFlatData?.flatProperties, null)
        dealBreakerView?.assignCallback(callback)
        dealBreakerView?.show()
    }

    override fun onClick(intent: Intent?, requestCode: Int) {
        if (requestCode == 1) {
            // Interests or languages
            val type = intent?.getStringExtra("type")
            if (type == InterestsView.VIEW_TYPE_INTERESTS) {
                viewBind.includePrefFlatmate.txtProfileInterest.text =
                    intent?.getStringExtra("interest")
                if (viewBind.includePrefFlatmate.txtProfileInterest.text.isEmpty())
                    updatingFlatData?.flatProperties?.interests = ArrayList()
                else {
                    val items = ArrayList<String>()
                    val text = viewBind.includePrefFlatmate.txtProfileInterest.text.toString()
                    val split = TextUtils.split(text, ", ")
                    if (split.isNotEmpty()) {
                        for (txt in split) {
                            items.add(txt)
                        }
                    }
                    updatingFlatData?.flatProperties?.interests = items
                }
            } else if (type == InterestsView.VIEW_TYPE_LANGUAGES) {
                viewBind.includePrefFlatmate.txtProfileLanguages.text =
                    intent?.getStringExtra("interest")
                if (viewBind.includePrefFlatmate.txtProfileLanguages.text.isEmpty())
                    updatingFlatData?.flatProperties?.languages = ArrayList()
                else {
                    val items = ArrayList<String>()
                    val text = viewBind.includePrefFlatmate.txtProfileLanguages.text.toString()
                    val split = TextUtils.split(text, ", ")
                    if (split.isNotEmpty()) {
                        for (txt in split) {
                            items.add(txt)
                        }
                    }
                    updatingFlatData?.flatProperties?.languages = items
                }
            }
        }
    }

    override fun onBackPressed() {
        viewBind.btnBack.performClick()
    }
}