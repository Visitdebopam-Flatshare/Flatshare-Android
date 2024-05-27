package com.joinflatshare.ui.register.create

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.pojo.config.RentRange
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.utils.helper.CommonMethod
import java.util.Calendar

/**
 * Created by debopam on 03/02/24
 */
class ProfileCreateListener(
    private val activity: ProfileCreateActivity,
    private val viewBind: ActivityProfileCreateBinding
) : OnClickListener {
    private var buttonListener: TextWatcher? = null
    var dob = ""
    var isAllDataFilled = false

    init {
        viewBind.txtDob.setOnClickListener(this)
        viewBind.txtGender.setOnClickListener(this)
        viewBind.txtMyself.setOnClickListener(this)
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnCreateProfile.setOnClickListener(this)
        initTextWatcher()
        if (buttonListener != null) {
            viewBind.edtFname.addTextChangedListener(buttonListener)
            viewBind.edtFname.addTextChangedListener(buttonListener)
            viewBind.txtDob.addTextChangedListener(buttonListener)
            viewBind.txtGender.addTextChangedListener(buttonListener)
            viewBind.txtMyself.addTextChangedListener(buttonListener)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.txtGender.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Male"))
                list.add(ModelBottomSheet(0, "Female"))
                list.add(ModelBottomSheet(0, "Trans"))
                BottomSheetView(activity, list).show { _, position ->
                    viewBind.txtGender.text = list[position].name
                }
            }

            viewBind.txtDob.id -> {
                val calendar = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    activity,
                    OnDateSetListener { view12: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                        dob = String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth)
                        val day =
                            String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                        val dobCalendar = Calendar.getInstance()
                        dobCalendar[Calendar.YEAR] = year
                        dobCalendar[Calendar.MONTH] = month
                        dobCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        calendar.add(Calendar.YEAR, -15)
                        if (dobCalendar.time.after(calendar.time)) {
                            CommonMethod.makeToast(
                                "You need to be above the age of fifteen to join "
                                        + activity.getString(R.string.app_name)
                            )
                            return@OnDateSetListener
                        }
                        viewBind.txtDob.text = day
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                dpd.datePicker.maxDate = calendar.timeInMillis
                dpd.show()
            }

            viewBind.txtMyself.id -> {
                val list = ArrayList<ModelBottomSheet>()
                list.add(ModelBottomSheet(0, "Student"))
                list.add(ModelBottomSheet(0, "Working Professional"))
                BottomSheetView(activity, list).show { _, position ->
                    viewBind.txtMyself.text = list[position].name
                }
            }

            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.btnCreateProfile.id -> {
                if (isAllDataFilled) {
                    if (viewBind.edtFname.text.toString().length == 1)
                        CommonMethod.makeToast("First name must be minimum 2 characters long")
                    else if (viewBind.edtLname.text.toString().length == 1)
                        CommonMethod.makeToast("Last name must be minimum 2 characters long")
                    else {
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
                                modelUser?.flatProperties?.rentRange =
                                    RentRange()
                                modelUser?.work = viewBind.txtDob.text.toString()
                                activity.updateUser(modelUser)
                            }
                        }
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
                var allFilled = true
                if (viewBind.edtFname.text.toString().isEmpty())
                    allFilled = false
                else if (viewBind.edtLname.text.toString().isEmpty())
                    allFilled = false
                else if (viewBind.txtDob.text.toString().isEmpty())
                    allFilled = false
                else if (viewBind.txtGender.text.toString().isEmpty())
                    allFilled = false
                else if (viewBind.txtMyself.text.toString().isEmpty())
                    allFilled = false
                isAllDataFilled = allFilled
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
            }
        }
    }

}