package com.joinflatshare.ui.flat.flatoptions.view_moveindate

import android.app.DatePickerDialog
import android.view.View
import android.widget.DatePicker
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.system.ThemeUtils
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Calendar

class MoveInListener(
    private val activity: BaseActivity,
    private val moveInViewBind: MoveInViewBind
) : View.OnClickListener {

    init {
        moveInViewBind.viewBind?.llFlatDate?.setOnClickListener(this)
        moveInViewBind.viewBind?.imgDateCalendar?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            moveInViewBind.viewBind?.imgDateCalendar?.id,
            moveInViewBind.viewBind?.llFlatDate?.id -> {
                val calendar = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    activity,
                    ThemeUtils.getTheme(activity),
                    { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val format: NumberFormat = DecimalFormat("00")
                        val dd = format.format(dayOfMonth.toLong())
                        val mm = format.format((monthOfYear + 1).toLong())
                        moveInViewBind.viewBind?.txtDateDd?.text = dd
                        moveInViewBind.viewBind?.txtDateMm?.text = mm
                        moveInViewBind.viewBind?.txtDateYy?.text = "" + year
                        moveInViewBind.dateSelected = "$dd/$mm/$year"

                    }, calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                dpd.datePicker.minDate = calendar.timeInMillis
                dpd.show()
            }
        }
    }
}