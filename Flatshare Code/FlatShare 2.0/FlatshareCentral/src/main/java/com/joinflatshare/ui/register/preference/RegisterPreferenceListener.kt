package com.joinflatshare.ui.register.preference

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterPreferenceBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.utils.google.AutoCompletePlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by debopam on 04/02/24
 */
class RegisterPreferenceListener(
    private val activity: RegisterPreferenceActivity,
    private val viewBind: ActivityRegisterPreferenceBinding
) : OnClickListener {
    private var moveIn: String? = null
    private var location: ModelLocation? = null
    var roomType = ""

    init {
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnSearch.setOnClickListener(this)
        viewBind.txtPrefFlatPrivateRoom.setOnClickListener(this)
        viewBind.txtPrefFlatSharedRoom.setOnClickListener(this)
        viewBind.txtPrefFlatLocation.setOnClickListener(this)
        viewBind.txtPrefFlatMovein.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.txtPrefFlatPrivateRoom.id -> {
                roomType = if (roomType == "Private Room") "" else "Private Room"
                activity.setRoomType()
            }

            viewBind.txtPrefFlatSharedRoom.id -> {
                roomType = if (roomType == "Shared Room") "" else "Shared Room"
                activity.setRoomType()
            }

            viewBind.txtPrefFlatLocation.id -> {
                AutoCompletePlaces.getPlaces(
                    activity
                ) { intent, requestCode ->
                    if (requestCode == RequestCodeConstants.REQUEST_CODE_LOCATION) {
                        if (intent != null) {
                            location = CommonMethod.getSerializable(
                                intent, "location", ModelLocation::class.java
                            )
                            viewBind.txtPrefFlatLocation.text = location?.name
                            isButtonEnabled()
                        }
                    }
                }
            }

            viewBind.txtPrefFlatMovein.id -> {
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
                    viewBind.txtPrefFlatMovein.text = sdf.format(calendar.time)
                    moveIn = DateUtils.convertToServerFormat(sdf.format(calendar.time))
                    isButtonEnabled()
                }
            }

            viewBind.btnSearch.id -> {
                if (location == null || moveIn.isNullOrEmpty())
                    return
                else {
                    val user = AppConstants.loggedInUser
                    user?.flatProperties?.preferredLocation = ArrayList()
                    user?.flatProperties?.preferredLocation?.add(location!!)
                    user?.flatProperties?.moveinDate = moveIn
                    user?.flatProperties?.roomType = roomType
                    activity.updateUser(user, object : OnUserFetched {
                        override fun userFetched(resp: UserResponse?) {
                            FlatShareApplication.getDbInstance().appDao()
                                .delete(AppDao.ONBOARDING_SCREEN_PROGRESS)
                            MixpanelUtils.onButtonClicked("Onboarding Preference Saved")
                            val intent = Intent(activity, ExploreActivity::class.java)
                            CommonMethod.switchActivity(activity, intent, false)
                            activity.finishAffinity()
                        }
                    })
                }
            }
        }
    }

    private fun isButtonEnabled() {
        if (location == null || moveIn.isNullOrEmpty()) {
            viewBind.btnSearch.background = ContextCompat.getDrawable(
                activity,
                R.drawable.drawable_button_light_blue
            )
        } else {
            viewBind.btnSearch.background = ContextCompat.getDrawable(
                activity,
                R.drawable.drawable_button_blue
            )
        }
    }
}