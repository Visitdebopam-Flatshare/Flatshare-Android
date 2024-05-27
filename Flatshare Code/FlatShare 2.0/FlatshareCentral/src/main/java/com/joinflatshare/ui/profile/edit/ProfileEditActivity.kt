package com.joinflatshare.ui.profile.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.debopam.ImagePicker
import com.debopam.ImagePicker.Companion.getError
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_PICK_IMAGE
import com.joinflatshare.pojo.user.Loc
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.ImageHelper

class ProfileEditActivity : BaseActivity() {
    private lateinit var viewBind: ActivityProfileEditBinding
    lateinit var dataBind: ProfileEditDataBinder
    private lateinit var listener: ProfileEditListener
    var latProfile = arrayOfNulls<Loc>(2)

    @kotlin.jvm.JvmField
    var imageClickPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(
            this,
            true,
            AppConstants.loggedInUser?.name?.firstName + " " + AppConstants.loggedInUser?.name?.lastName,
            R.drawable.ic_back,
            0
        )
        dataBind = ProfileEditDataBinder(this, viewBind)
        listener = ProfileEditListener(this, viewBind, dataBind)
    }

    override fun onResume() {
        super.onResume()
        if (dataBind.adapter != null) dataBind.adapter!!.notifyDataSetChanged()
    }

    fun pickImage() {
        ImageHelper.pickImageFromGallery(this, 3f, 4f)
    }

    @Deprecated("Deprecated in Java")
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
                            dataBind.adapter?.setItems(dataBind.adapterUserImages)
                            dataBind.adapter?.notifyDataSetChanged()
                            imageClickPosition = -1
                        }
                    }
            }
        } else {
            if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                CommonMethod.makeToast(status.statusMessage)
                CommonMethod.makeLog("Error", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                CommonMethod.makeToast(getError(data))
            }
        }
    }

    override fun onBackPressed() {
        baseViewBinder.btn_back.performClick()
    }

}