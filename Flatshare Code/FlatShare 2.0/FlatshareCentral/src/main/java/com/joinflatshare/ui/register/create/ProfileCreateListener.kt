package com.joinflatshare.ui.register.create

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.pojo.config.RentRange
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by debopam on 03/02/24
 */
class ProfileCreateListener(
    private val activity: ProfileCreateActivity,
    private val viewBind: ActivityProfileCreateBinding
) : OnClickListener {
    private var buttonListener: TextWatcher? = null
    var dob = ""

    init {
        viewBind.txtDob.setOnClickListener(this)
        viewBind.txtGender.setOnClickListener(this)
        viewBind.txtMyself.setOnClickListener(this)
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnCreateProfile.setOnClickListener(this)
        initTextWatcher()
        if (buttonListener != null) {
            viewBind.edtFname.addTextChangedListener(buttonListener)
            viewBind.edtLname.addTextChangedListener(buttonListener)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.txtGender.id -> {
                CommonMethods.hideSoftKeyboard(activity)
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Male"))
                list.add(ModelBottomSheet(0, "Female"))
                list.add(ModelBottomSheet(0, "Trans"))
                BottomSheetView(activity, list) { _, position ->
                    viewBind.txtGender.text = list[position].name
                    checkValidation()
                }
            }

            viewBind.txtDob.id -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -15)
                val pickerNowTime = calendar.timeInMillis
                val constraints = CalendarConstraints.Builder().setEnd(pickerNowTime)
                    .setValidator(DateValidatorPointBackward.before(pickerNowTime))
                var sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dpd = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
                    .setSelection(calendar.timeInMillis)
                    .setTextInputFormat(sdf)
                    .setTheme(R.style.ThemeDatePicker)
                    .setCalendarConstraints(constraints.build())
                    .build()
                dpd.show(activity.supportFragmentManager, "DOB")
                dpd.addOnPositiveButtonClickListener { it ->
                    calendar.timeInMillis = it
                    viewBind.txtDob.text = sdf.format(calendar.time)
                    sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    dob = sdf.format(calendar.time)
                    checkValidation()
                }
            }

            viewBind.txtMyself.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Student"))
                list.add(ModelBottomSheet(0, "Working Professional"))
                BottomSheetView(activity, list) { _, position ->
                    viewBind.txtMyself.text = list[position].name
                    checkValidation()
                }
            }

            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.btnCreateProfile.id -> {
                if (checkValidation())
                    AlertDialog.showAlert(
                        activity,
                        "Alert",
                        "You won't be able to edit your name, birthday, & gender. Is it confirmed?",
                        "Yes, I know",
                        "No"
                    ) { _: Intent?, requestCode: Int ->
                        if (requestCode == 1) {
                            val name = Name()
                            name.firstName = viewBind.edtFname.text.toString()
                            name.lastName = viewBind.edtLname.text.toString()
                            val modelUser = activity.user
                            modelUser?.name = name
                            modelUser?.dob = dob
                            modelUser?.gender = viewBind.txtGender.text.toString()
                            modelUser?.flatProperties?.rentRange = RentRange()
                            modelUser?.profession = viewBind.txtMyself.text.toString()
                            activity.updateUser(modelUser)
                        }
                    }
            }
        }
    }

    private fun initTextWatcher() {
        buttonListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                checkValidation()

            }
        }
    }

    private fun checkValidation(): Boolean {
        var allFilled = true
        if (viewBind.edtFname.text.toString().trim().length < 2)
            allFilled = false
        else if (viewBind.edtLname.text.toString().trim().isEmpty())
            allFilled = false
        else if (viewBind.txtDob.text.toString().isEmpty())
            allFilled = false
        else if (viewBind.txtGender.text.toString().isEmpty())
            allFilled = false
        else if (viewBind.txtMyself.text.toString().isEmpty())
            allFilled = false
        if (allFilled)
            viewBind.btnCreateProfile.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.drawable_button_blue
                )
            )
        else viewBind.btnCreateProfile.setBackgroundDrawable(
            ContextCompat.getDrawable(
                activity,
                R.drawable.drawable_button_light_blue
            )
        )
        return allFilled
    }

}