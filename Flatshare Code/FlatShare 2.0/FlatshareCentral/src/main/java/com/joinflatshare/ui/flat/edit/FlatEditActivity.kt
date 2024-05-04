package com.joinflatshare.ui.flat.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.debopam.ImagePicker
import com.debopam.ImagePicker.Companion.getError
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatEditBinding
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_PICK_IMAGE
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.ui.flat.flatoptions.view_roomsize.RoomSizeViewBind
import com.joinflatshare.ui.flat.flatoptions.view_roomtype.RoomTypeViewBind
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.ImageHelper

class FlatEditActivity : BaseActivity() {
    lateinit var viewBind: ActivityFlatEditBinding

    @kotlin.jvm.JvmField
    var latFlatLocation: ModelLocation? = null

    @kotlin.jvm.JvmField
    var latSociety: ModelLocation? = null

    lateinit var dataBind: FlatEditDataBind

    @kotlin.jvm.JvmField
    var imageClickPosition = -1

    @kotlin.jvm.JvmField
    var roomTypeViewBind: RoomTypeViewBind? = null

    @kotlin.jvm.JvmField
    var selectedAmenityList = ArrayList<String>()

    @kotlin.jvm.JvmField
    var flatsizeViewBind: RoomSizeViewBind? = null
    var dialogFlatOptions: DialogFlatOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityFlatEditBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Edit Flat Profile", R.drawable.ic_back, 0)
        dataBind = FlatEditDataBind(this)
        FlatEditListener(this, viewBind)
        dialogFlatOptions = DialogFlatOptions(this) { intent: Intent?, requestCode: Int ->
            if (requestCode == 1) {
                val header = dialogFlatOptions?.holder?.txtDialogHeader?.text.toString()
                dialogFlatOptions?.viewBind?.rvFlatFurnishing?.visibility = View.GONE
                if (header.equals("Room Type")) {
                    viewBind.txtRoomtype.text = roomTypeViewBind?.roomTypeAdapter?.roomType
                } else {
                    viewBind.txtFlatsize.text = flatsizeViewBind?.roomSizeAdapter?.roomSizeValue
                }
                dataBind.setCompleteCount()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allPermitted = true
        for (result in grantResults) if (result != PackageManager.PERMISSION_GRANTED) {
            allPermitted = false
            break
        }
        if (allPermitted) pickImage() else CommonMethod.makeToast( "Permission not provided")
    }

    fun pickImage() {
        ImageHelper.pickImageFromGallery(this, 16f, 9f)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE ->
                    if (data != null) {
                        val file = ImageHelper.copyFile(this, data.data)
                        if (file != null && file.exists()) {
                            dataBind.addedUserImages.add(file.absolutePath)

                            if (dataBind.adapterUserImages.size <= 1)
                                dataBind.adapterUserImages.add(file.absolutePath)
                            else dataBind.adapterUserImages.add(1, file.absolutePath)
                            if (dataBind.adapterUserImages.size > 10) {
                                dataBind.adapterUserImages.removeAt(0)
                            }
                            dataBind.adapter?.items = dataBind.adapterUserImages
                            dataBind.adapter?.notifyDataSetChanged()
                            imageClickPosition = -1
                            dataBind.setCompleteCount()
                        }
                    }
            }
        } else {
            if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data)
                CommonMethod.makeToast( status.statusMessage)
                CommonMethod.makeLog("Error", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                CommonMethod.makeToast( getError(data))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (dataBind.adapter != null) dataBind.adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (viewBind.rlMyflatOption.visibility == View.VISIBLE) {
            viewBind.rlMyflatOption.visibility = View.GONE
        } else baseViewBinder.btn_back.performClick()
    }
}