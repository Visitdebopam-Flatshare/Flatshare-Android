package com.joinflatshare.ui.flat.flatoptions.view_preferred_location

import android.view.View
import android.widget.TextView
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.utils.google.AutoCompletePlaces
import com.joinflatshare.utils.helper.CommonMethod

class PreferredLocationViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null
    lateinit var txt_flat_plocation: Array<TextView?>
    val modelLocation = ArrayList<ModelLocation>()

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init()
    }

    fun init() {
        viewBind?.llPreferredLocation?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Preferred Flat Location")
        txt_flat_plocation = arrayOf(
            viewBind?.txtFlatPlocation1,
            viewBind?.txtFlatPlocation2, viewBind?.txtFlatPlocation3
        )
        if (dialogFlatOptions?.loggedInUser?.flatProperties?.preferredLocation?.isEmpty() == false) {
            for (temp in dialogFlatOptions?.loggedInUser?.flatProperties?.preferredLocation!!) {
                if (!temp.name.isEmpty()) {
                    modelLocation.add(temp)
                }
            }
        }

        for (i in modelLocation.indices) {
            txt_flat_plocation.get(i)?.setText(modelLocation.get(i).name)
        }
        click()
    }

    private fun click() {
        for (i in txt_flat_plocation.indices) {
            txt_flat_plocation[i]!!.setOnClickListener { v: View? ->
                AutoCompletePlaces.getPlaces(
                    dialogFlatOptions?.activity!!
                ) { intent, requestCode ->
                    if (requestCode == RequestCodeConstants.REQUEST_CODE_LOCATION) {
                        if (intent != null) {
                            val location = CommonMethod.getSerializable(
                                intent,
                                "location",
                                ModelLocation::class.java
                            )
                            if (i < modelLocation.size)
                                modelLocation[i] = location else modelLocation.add(location)
                            txt_flat_plocation[i]!!.text = location.name
                        }
                    }
                }
            }
        }
    }
}