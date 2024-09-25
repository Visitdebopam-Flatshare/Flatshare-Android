package com.joinflatshare.ui.flat.flatoptions.view_rent_range

import android.view.View
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.ui.dialogs.DialogFlatOptions

class RentRangeViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init()
    }

    private fun init() {
        viewBind?.llRentRange?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Rent Range Per Person")
        bind()
    }

    private fun bind() {
        /*viewBind?.seekbarRent?.setMinValue(AppData.flatData?.rentRange?.startRange?.toFloat()!!)
        viewBind?.seekbarRent?.setMaxValue(AppData.flatData?.rentRange?.endRange?.toFloat()!!)
        viewBind?.txtRentRangeMin?.text = try {
            dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + viewBind?.seekbarRent?.selectedMinValue
        } catch (ex: Exception) {
            dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + AppData.flatData?.rentRange?.startRange
        }
        viewBind?.txtRentRangeMax?.setText(
            try {
                dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + viewBind?.seekbarRent?.selectedMaxValue
            } catch (ex: Exception) {
                dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + AppData.flatData?.rentRange?.endRange
            }
        )
        viewBind?.seekbarRent?.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
            viewBind?.txtRentRangeMin?.text = dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + minValue
            viewBind?.txtRentRangeMax?.text = dialogFlatOptions?.activity?.resources?.getString(R.string.currency) + maxValue
        }
        val data = dialogFlatOptions?.loggedInUser?.flatProperties?.rentRange
        if (data != null && data.startRange > 0 && data.endRange > 0) {
            val start = data.startRange
            val end = data.endRange
            viewBind?.txtRentRangeMin?.text = "" + start
            viewBind?.seekbarRent?.setMinStartValue(start.toFloat())
            viewBind?.txtRentRangeMax?.text = "" + end
            viewBind?.seekbarRent?.setMaxStartValue(end.toFloat())
            viewBind?.seekbarRent?.apply()
        }*/
    }
}