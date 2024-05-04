package com.joinflatshare.ui.flat.flatoptions.view_moveindate

import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.utils.helper.DateUtils.convertToAppFormat

class MoveInViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null
    var dateSelected = ""

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init()
    }

    private fun init() {
        viewBind?.llFlatDate?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Available By")
        setMoveinDate()
    }

    private fun setMoveinDate() {
        var date:String? = ""
        if (dialogFlatOptions?.flat != null && dialogFlatOptions?.flat?.isMateSearch?.value == true)
            date =dialogFlatOptions?.flat?.flatProperties?.moveinDate
        else if (dialogFlatOptions?.loggedInUser?.isFlatSearch?.value == true)
            date = dialogFlatOptions?.loggedInUser?.flatProperties?.moveinDate
        if (!date.isNullOrEmpty()) {
            try {
                dateSelected = convertToAppFormat(date)
                val split = dateSelected.split("/").toTypedArray()
                viewBind?.txtDateDd?.setText(split[0])
                viewBind?.txtDateMm?.setText(split[1])
                viewBind?.txtDateYy?.setText(split[2])
            } catch (ex: Exception) {
                viewBind?.txtDateDd?.text = ""
                viewBind?.txtDateMm?.text = ""
                viewBind?.txtDateYy?.text = ""
            }
        } else {
            viewBind?.txtDateDd?.text = ""
            viewBind?.txtDateMm?.text = ""
            viewBind?.txtDateYy?.text = ""
        }
    }
}