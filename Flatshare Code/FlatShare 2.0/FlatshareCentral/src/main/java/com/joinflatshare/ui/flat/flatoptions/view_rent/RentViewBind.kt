package com.joinflatshare.ui.flat.flatoptions.view_rent

import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant

class RentViewBind(private val constant: Int) {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null

    constructor(dialogFlatOptions: DialogFlatOptions, constant: Int) : this(constant) {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        setData()
    }

    private fun setData() {
        var rent: Int? = null
        var deposit: Int? = null
        viewBind?.llFlatRent?.visibility = View.VISIBLE
        if (dialogFlatOptions?.flat?.isMateSearch?.value == true) {
            rent = dialogFlatOptions?.flat?.flatProperties?.rentperPerson
            deposit = dialogFlatOptions?.flat?.flatProperties?.depositperPerson
        }

        if (constant == FlatOptionConstant.VIEW_CONSTANT_RENT) {
            viewBind?.edtFlatRent?.setText("" + rent)
            dialogFlatOptions?.setName("Rent Per Person")
        } else if (constant == FlatOptionConstant.VIEW_CONSTANT_DEPOSIT) {
            viewBind?.edtFlatRent?.setText("" + deposit)
            dialogFlatOptions?.setName("Deposit Per Person")
        } else {
            dialogFlatOptions?.setName("Rent Per Person")
        }

        if (viewBind?.edtFlatRent!!.toString().isNotEmpty())
            viewBind?.edtFlatRent?.setSelection(viewBind?.edtFlatRent!!.length())
    }
}