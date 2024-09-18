package com.joinflatshare.ui.preferences

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.payment.PaymentHandler
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.bottomsheet.ElitePreferenceBottomSheet
import com.joinflatshare.ui.bottomsheet.VerifiedBottomSheet
import com.joinflatshare.utils.google.AutoCompletePlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PreferenceListener(private val activity: PreferenceActivity) : View.OnClickListener {
    private val viewBind: ActivityPrefFlatBinding = activity.viewBind

    init {
        viewBind.includePrefFlat.txtPrefFlatLocation.setOnClickListener(this)
        viewBind.includePrefFlat.txtPrefFlatMovein.setOnClickListener(this)
        viewBind.includePrefFlat.txtPrefFlatPrivateRoom.setOnClickListener(this)
        viewBind.includePrefFlat.txtPrefFlatSharedRoom.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtMale.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtFemale.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtall.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtStudents.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtWorking.setOnClickListener(this)
        activity.baseViewBinder.btn_back.setOnClickListener(this)
        switchListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.includePrefFlat.txtPrefFlatLocation.id -> {
                AutoCompletePlaces.getPlaces(
                    activity
                ) { intent, requestCode ->
                    if (requestCode == RequestCodeConstants.REQUEST_CODE_LOCATION) {
                        if (intent != null) {
                            val location = CommonMethod.getSerializable(
                                intent, "location", ModelLocation::class.java
                            )
                            activity.user?.flatProperties?.preferredLocation = ArrayList()
                            activity.user?.flatProperties?.preferredLocation?.add(location)
                            viewBind.includePrefFlat.txtPrefFlatLocation.text = location.name
                        }
                    }
                }
            }

            viewBind.includePrefFlat.txtPrefFlatMovein.id -> {
                val calendar = Calendar.getInstance()
                val pickerNowTime = calendar.timeInMillis
                val constraints = CalendarConstraints.Builder().setStart(pickerNowTime)
                    .setValidator(DateValidatorPointForward.from(pickerNowTime))
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dpd =
                    MaterialDatePicker.Builder.datePicker().setTitleText("Select Move-in Date")
                        .setSelection(calendar.timeInMillis)
                        .setTextInputFormat(sdf)
                        .setTheme(R.style.ThemeDatePicker)
                        .setCalendarConstraints(constraints.build())
                        .build()
                dpd.show(activity.supportFragmentManager, "DOB")
                dpd.addOnPositiveButtonClickListener { it ->
                    calendar.timeInMillis = it
                    viewBind.includePrefFlat.txtPrefFlatMovein.text = sdf.format(calendar.time)
                    activity.user?.flatProperties?.moveinDate =
                        DateUtils.convertToServerFormat(sdf.format(calendar.time))
                }
            }

            viewBind.includePrefFlat.txtPrefFlatPrivateRoom.id -> {
                var roomType = activity.user?.flatProperties?.roomType
                roomType = if (roomType == "Private Room") "" else "Private Room"
                activity.user?.flatProperties?.roomType = roomType
                activity.setRoomType()
            }

            viewBind.includePrefFlat.txtPrefFlatSharedRoom.id -> {
                var roomType = activity.user?.flatProperties?.roomType
                roomType = if (roomType == "Shared Room") "" else "Shared Room"
                activity.user?.flatProperties?.roomType = roomType
                activity.setRoomType()
            }

            viewBind.includePrefFlatmate.txtMale.id -> {
                var gender = activity.user?.flatProperties?.gender
                gender = if (gender == "Male") "" else "Male"
                activity.user?.flatProperties?.gender = gender
                activity.setGender()
            }

            viewBind.includePrefFlatmate.txtFemale.id -> {
                var gender = activity.user?.flatProperties?.gender
                gender = if (gender == "Female") "" else "Female"
                activity.user?.flatProperties?.gender = gender
                activity.setGender()
            }

            viewBind.includePrefFlatmate.txtall.id -> {
                var gender = activity.user?.flatProperties?.gender
                gender = if (gender == "All") "" else "All"
                activity.user?.flatProperties?.gender = gender
                activity.setGender()
            }

            viewBind.includePrefFlatmate.txtStudents.id -> {
                var profession = activity.user?.flatProperties?.profession
                profession = if (profession == "Student") "" else "Student"
                activity.user?.flatProperties?.profession = profession
                activity.setProfession()
            }

            viewBind.includePrefFlatmate.txtWorking.id -> {
                var profession = activity.user?.flatProperties?.profession
                profession =
                    if (profession == "Working Professional") "" else "Working Professional"
                activity.user?.flatProperties?.profession = profession
                activity.setProfession()
            }

            activity.baseViewBinder.btn_back.id -> {
                activity.user?.flatProperties?.dealBreakers =
                    activity.dealBreakerView?.getDealBreakers()
                checkForUnsavedData()
            }
        }
    }

    private fun switchListener() {
        viewBind.includePrefFlatmate.switchVerifiedMember.setOnCheckedChangeListener { _, isChecked ->
            setVerified(isChecked)
            if (isChecked) {
                if (activity.user?.verification?.isVerified == false) {
                    setVerified(false)
                    VerifiedBottomSheet(
                        activity
                    ) { setVerified(true) }
                }
            }
        }
        viewBind.includePrefFlatmate.switchEliteMember.setOnCheckedChangeListener { _, isChecked ->
            setElite(isChecked)
            if (isChecked) {
                if (activity.user?.verification?.isVerified == false) {
                    setElite(false)
                    PaymentHandler.showPaymentForElite(
                        activity
                    ) { text ->
                        if (text == "1") {
                            activity.baseApiController.getUser(
                                true,
                                AppConstants.loggedInUser?.id, null
                            )
                        }
                    }


                    ElitePreferenceBottomSheet(
                        activity
                    ) { text ->
                        if (text == "1") {
                            activity.baseApiController.getUser(
                                true,
                                AppConstants.loggedInUser?.id, object : OnUserFetched {
                                    override fun userFetched(resp: UserResponse?) {
                                        setElite(true)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setVerified(checked: Boolean) {
        viewBind.includePrefFlatmate.switchVerifiedMember.isChecked = checked
        activity.user?.flatProperties?.isVerifiedOnly = checked
        if (checked) {
            viewBind.includePrefFlatmate.imgVerified.setColorFilter(
                ContextCompat.getColor(
                    activity, R.color.blue_dark
                )
            )
            viewBind.includePrefFlatmate.txtVerified.setTextColor(
                ContextCompat.getColor(
                    activity, R.color.blue_dark
                )
            )
            viewBind.includePrefFlatmate.rlVerified.background = ContextCompat.getDrawable(
                activity, R.drawable.drawable_button_blue_stroke_blue_bg
            )
        } else {
            viewBind.includePrefFlatmate.imgVerified.setColorFilter(
                ContextCompat.getColor(
                    activity, R.color.grey2
                )
            )
            viewBind.includePrefFlatmate.txtVerified.setTextColor(
                ContextCompat.getColor(
                    activity, R.color.grey2
                )
            )
            viewBind.includePrefFlatmate.rlVerified.background = ContextCompat.getDrawable(
                activity, R.drawable.drawable_button_grey_stroke
            )
        }
    }

    private fun setElite(checked: Boolean) {
        viewBind.includePrefFlatmate.switchVerifiedMember.isChecked = checked
        activity.user?.flatProperties?.isVerifiedOnly = checked
        if (checked) {
            viewBind.includePrefFlatmate.imgVerified.setColorFilter(
                ContextCompat.getColor(
                    activity, R.color.blue_dark
                )
            )
            viewBind.includePrefFlatmate.txtVerified.setTextColor(
                ContextCompat.getColor(
                    activity, R.color.blue_dark
                )
            )
            viewBind.includePrefFlatmate.rlVerified.background = ContextCompat.getDrawable(
                activity, R.drawable.drawable_button_blue_stroke_blue_bg
            )
        } else {
            viewBind.includePrefFlatmate.imgVerified.setColorFilter(
                ContextCompat.getColor(
                    activity, R.color.grey2
                )
            )
            viewBind.includePrefFlatmate.txtVerified.setTextColor(
                ContextCompat.getColor(
                    activity, R.color.grey2
                )
            )
            viewBind.includePrefFlatmate.rlVerified.background = ContextCompat.getDrawable(
                activity, R.drawable.drawable_button_grey_stroke
            )
        }
    }

    private fun checkForUnsavedData() {
        var hasChanged = false
        val actualUserData = FlatShareApplication.getDbInstance().userDao().getUser()
        if (actualUserData?.flatProperties?.preferredLocation?.equals(activity.user?.flatProperties?.preferredLocation) == false) {
            hasChanged = true
            MixpanelUtils.sendToMixPanel("Preferred Flat Location Filled")
        } else if (actualUserData?.flatProperties?.moveinDate != activity.user?.flatProperties?.moveinDate) hasChanged =
            true
        else if (actualUserData?.flatProperties?.roomType != activity.user?.flatProperties?.roomType) hasChanged =
            true
        else if (actualUserData?.flatProperties?.isVerifiedOnly != activity.user?.flatProperties?.isVerifiedOnly) hasChanged =
            true
        else if (actualUserData?.flatProperties?.gender != activity.user?.flatProperties?.gender) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.profession != activity.user?.flatProperties?.profession) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.interests != activity.user?.flatProperties?.interests) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.dealBreakers == null && activity.user?.flatProperties?.dealBreakers != null) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.dealBreakers?.smoking != activity.user?.flatProperties?.dealBreakers?.smoking) hasChanged =
            true
        else if (actualUserData?.flatProperties?.dealBreakers?.nonveg != activity.user?.flatProperties?.dealBreakers?.nonveg) hasChanged =
            true
        else if (actualUserData?.flatProperties?.dealBreakers?.flatparty != activity.user?.flatProperties?.dealBreakers?.flatparty) hasChanged =
            true
        else if (actualUserData?.flatProperties?.dealBreakers?.eggs != activity.user?.flatProperties?.dealBreakers?.eggs) hasChanged =
            true
        else if (actualUserData?.flatProperties?.dealBreakers?.pets != activity.user?.flatProperties?.dealBreakers?.pets) hasChanged =
            true
        else if (actualUserData?.flatProperties?.dealBreakers?.workout != activity.user?.flatProperties?.dealBreakers?.workout) hasChanged =
            true

        if (hasChanged) activity.baseApiController.updateUser(
            true,
            activity.user,
            object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    AppConstants.isFeedReloadRequired = true
                    CommonMethod.finishActivity(activity)
                }
            })
        else CommonMethod.finishActivity(activity)
    }
}