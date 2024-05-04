package com.joinflatshare.ui.flat.flatoptions.view_gender

import android.view.View
import android.widget.CompoundButton
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.ui.dialogs.DialogFlatOptions

class GenderViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null
    var genderList = ArrayList<String>()

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init()
    }

    private fun init() {
        if (dialogFlatOptions != null) {
            dialogFlatOptions?.setName("Gender")
            setGender()
        }
    }

    private fun setGender() {
        viewBind?.llFlatGender?.visibility = View.VISIBLE
        var gender = ""
        if (dialogFlatOptions?.flat != null && dialogFlatOptions?.flat?.isMateSearch?.value == true)
            gender = dialogFlatOptions?.flat?.flatProperties?.gender!!
        else if (dialogFlatOptions?.loggedInUser!!.isFlatSearch.value)
            gender = dialogFlatOptions?.loggedInUser!!.flatProperties.gender
        genderList.clear()
        if (!gender.isEmpty()) {
            if (gender == "Both") {
                viewBind?.ckbFlatMale?.isChecked = true
                viewBind?.ckbFlatFemale?.isChecked = true
                genderList.add("Male")
                genderList.add("Female")
            } else {
                if (gender == "Male") {
                    viewBind?.ckbFlatMale?.isChecked = true
                    viewBind?.ckbFlatFemale?.isChecked = false
                    genderList.add("Male")
                } else if (gender == "Female") {
                    viewBind?.ckbFlatMale?.isChecked = false
                    viewBind?.ckbFlatFemale?.isChecked = true
                    genderList.add("Female")
                }
            }
        } else {
            viewBind?.ckbFlatMale?.isChecked = false
            viewBind?.ckbFlatFemale?.isChecked = false
        }
        viewBind?.ckbFlatMale?.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b) {
                if (!genderList.contains("Male")) genderList.add("Male")
            } else {
                genderList.remove("Male")
            }
        }
        viewBind?.ckbFlatFemale?.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b) {
                if (!genderList.contains("Female")) genderList.add("Female")
            } else {
                genderList.remove("Female")
            }
        }
    }

    fun setGenderIntoApi(): String {
        var w = ""
        if (genderList.size > 0) {
            w = if (genderList.size == 1) {
                genderList[0]
            } else {
                "Both"
            }
        }
        return w
    }
}