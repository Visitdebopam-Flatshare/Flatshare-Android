package com.joinflatshare.ui.preferences

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.IntentConstants
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.DateUtils

class PreferenceActivity : BaseActivity() {
    lateinit var viewBind: ActivityPrefFlatBinding
    lateinit var listener: PreferenceListener
    val user = FlatShareApplication.getDbInstance().userDao().getUser()
    var dealBreakerView: DealBreakerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityPrefFlatBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Preferences", 0, 0)
        listener = PreferenceListener(this)
        bind()
        if (intent.getBooleanExtra(IntentConstants.INTENT_MOVE_TO_PREFERENCE, false))
            AppConstants.isFeedReloadRequired = true
    }

    private fun bind() {
        setLocation()
        setMoveInDate()
        setRoomType()
        setVerified()
        setGender()
        setProfession()
        setDealBreakers()
    }

    private fun setLocation() {
        if (!user?.flatProperties?.preferredLocation.isNullOrEmpty() &&
            user?.flatProperties?.preferredLocation!!.size > 0
        )
            viewBind.includePrefFlat.txtPrefFlatLocation.text =
                user.flatProperties.preferredLocation[0].name
    }

    private fun setMoveInDate() {
        if (!user?.flatProperties?.moveinDate.isNullOrEmpty())
            viewBind.includePrefFlat.txtPrefFlatMovein.text =
                DateUtils.convertToAppFormat(user?.flatProperties?.moveinDate)
    }

    fun setRoomType() {
        viewBind.includePrefFlat.txtPrefFlatPrivateRoom.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlat.txtPrefFlatSharedRoom.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlat.txtPrefFlatPrivateRoom.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        viewBind.includePrefFlat.txtPrefFlatSharedRoom.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        if (!user?.flatProperties?.roomType.isNullOrEmpty()) {
            if (user?.flatProperties?.roomType.equals("Private Room")
            ) {
                viewBind.includePrefFlat.txtPrefFlatPrivateRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlat.txtPrefFlatPrivateRoom.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }
            if (user?.flatProperties?.roomType.equals("Shared Room")
            ) {
                viewBind.includePrefFlat.txtPrefFlatSharedRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlat.txtPrefFlatSharedRoom.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }
        }
    }

    private fun setVerified() {
        viewBind.includePrefFlatmate.switchVerifiedMember.isChecked =
            user?.flatProperties?.isVerifiedOnly == true
    }

    fun setGender() {
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

        when (user?.flatProperties?.gender) {
            "Male" -> {
                viewBind.includePrefFlatmate.txtMale.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlatmate.txtMale.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            "Female" -> {
                viewBind.includePrefFlatmate.txtFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlatmate.txtFemale.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            "All" -> {
                viewBind.includePrefFlatmate.txtall.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlatmate.txtall.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }
        }
    }

    fun setProfession() {
        viewBind.includePrefFlatmate.txtStudents.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlatmate.txtWorking.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.includePrefFlatmate.txtStudents.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        viewBind.includePrefFlatmate.txtWorking.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )

        when (user?.flatProperties?.profession) {
            "Student" -> {
                viewBind.includePrefFlatmate.txtStudents.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlatmate.txtStudents.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            "Working Professional" -> {
                viewBind.includePrefFlatmate.txtWorking.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.includePrefFlatmate.txtWorking.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }
        }
    }

    private fun setDealBreakers() {
        if (dealBreakerView == null)
            dealBreakerView = DealBreakerView(this, viewBind.includePrefFlatmate.rvFlatDeals)
        dealBreakerView?.setDealValues(user?.flatProperties?.dealBreakers, null)
        dealBreakerView?.isEditable(true)
        dealBreakerView?.show()
    }

    override fun onBackPressed() {
        baseViewBinder.btn_back.performClick()
    }
}