package com.joinflatshare.ui.flat.flatoptions.view_location

import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.utils.google.AutoCompletePlaces
import com.joinflatshare.utils.helper.CommonMethod


class LocationViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null
    var modelLocation = ModelLocation()

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init()
        click()
    }

    private fun init() {
        viewBind?.txtFlatLocation?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Flat Location")
        if (!dialogFlatOptions?.flat?.flatProperties?.location?.name.isNullOrBlank())
            modelLocation = dialogFlatOptions?.flat?.flatProperties?.location!!
        viewBind?.txtFlatLocation?.setText(modelLocation.name)
    }

    private fun click() {
        viewBind?.txtFlatLocation?.setOnClickListener { v ->
            AutoCompletePlaces.getPlaces(
                dialogFlatOptions?.activity!!
            ) { intent, requestCode ->
                if (requestCode == RequestCodeConstants.REQUEST_CODE_LOCATION) {
                    if (intent != null) {
                        modelLocation = CommonMethod.getSerializable(
                            intent,
                            "location",
                            ModelLocation::class.java
                        )
                        viewBind?.txtFlatLocation?.text = modelLocation.name
                    }
                }
            }
        }
    }
}