package com.joinflatshare.ui.preferences.flatmate

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatmateBinding
import com.joinflatshare.ui.base.BaseActivity

class PrefFlatmateActivity : BaseActivity() {
    lateinit var viewBind: ActivityPrefFlatmateBinding
    lateinit var listener: PrefFlatmateListener
    var updatingFlatData = FlatShareApplication.getDbInstance().userDao().getFlatData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityPrefFlatmateBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        listener = PrefFlatmateListener(this)
        bind()
    }

    private fun bind() {
        setVerified()
        setGender()
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
        } else if (updatingFlatData?.flatProperties?.gender?.equals("All") == true) {
            listener.isMaleSelected = true
            listener.isFemaleSelected = true
        }
        setGenderButton()
    }

    fun setGenderButton() {
        viewBind.includePrefFlatmate.txtMale.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlatmate.txtFemale.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlatmate.txtall.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlatmate.txtMale.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        viewBind.includePrefFlatmate.txtFemale.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        viewBind.includePrefFlatmate.txtall.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )

        updatingFlatData?.flatProperties?.gender = ""
        if (listener.isMaleSelected) {
            viewBind.includePrefFlatmate.txtMale.background =
                ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
            viewBind.includePrefFlatmate.txtFemale.setTextColor(
                ContextCompat.getColor(this, R.color.white)
            )
            updatingFlatData?.flatProperties?.gender = "Male"
        } else if (listener.isFemaleSelected) {
            viewBind.includePrefFlatmate.txtFemale.background =
                ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
            viewBind.includePrefFlatmate.txtFemale.setTextColor(
                ContextCompat.getColor(this, R.color.white)
            )
            updatingFlatData?.flatProperties?.gender = "Female"
        } else if (listener.isAllGenderSelected) {
            viewBind.includePrefFlatmate.txtall.background =
                ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
            viewBind.includePrefFlatmate.txtall.setTextColor(
                ContextCompat.getColor(this, R.color.white)
            )
            updatingFlatData?.flatProperties?.gender = "All"
        }
    }
}