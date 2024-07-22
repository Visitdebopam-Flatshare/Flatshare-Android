package com.joinflatshare.ui.profile.edit

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_LOCATION
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.Loc
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.profile.interest.InterestActivity
import com.joinflatshare.ui.profile.language.LanguageActivity
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.google.AutoCompletePlaces.getPlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.system.ConnectivityListener
import java.io.File


class ProfileEditListener(
    private val activity: ProfileEditActivity,
    private val viewBind: ActivityProfileEditBinding, private val dataBinder: ProfileEditDataBinder
) : View.OnClickListener {

    init {
        countStatusLength()
        activity.baseViewBinder.btn_back.setOnClickListener(this)
        viewBind.llProfileInterests.setOnClickListener(this)
        viewBind.llProfileLanguages.setOnClickListener(this)
        for (i in dataBinder.edt_profile.indices) {
            val position: Int = i
            dataBinder.edt_profile[i].setOnClickListener {
                dataBinder.imgSearch[position].setImageResource(R.drawable.ic_search)
                dataBinder.imgSearch[position].setColorFilter(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                getPlaces(
                    activity
                ) { intent: Intent?, requestCode: Int ->
                    if (requestCode == REQUEST_CODE_LOCATION) {
                        dataBinder.imgSearch[position].setImageResource(R.drawable.ic_search)
                        if (intent != null) {
                            val location = CommonMethod.getSerializable(
                                intent,
                                "location",
                                ModelLocation::class.java
                            )
                            dataBinder.edt_profile[position].text = location.name
                            activity.latProfile[position] = location.loc
                        }
                    }
                }
            }
        }
    }

    private fun countStatusLength() {
        viewBind.edtProfileStatus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                dataBinder.bioLimit()
            }
        })
        viewBind.edtProfileWork.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                dataBinder.workLimit()
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            activity.baseViewBinder.btn_back.id -> {
                checkForUnsavedData()
            }

            viewBind.llProfileInterests.id -> {
                val intent = Intent(activity, InterestActivity::class.java)
                intent.putExtra("edit", true)
                intent.putExtra("content", viewBind.txtProfileInterest.text)
                CommonMethod.switchActivity(
                    activity, intent
                ) {
                    if (it?.resultCode == Activity.RESULT_OK) {
                        viewBind.txtProfileInterest.text = it.data?.getStringExtra("interest")
                    }
                }
            }

            viewBind.llProfileLanguages.id -> {
                val intent = Intent(activity, LanguageActivity::class.java)
                intent.putExtra("edit", true)
                intent.putExtra("content", viewBind.txtProfileLanguages.text)
                CommonMethod.switchActivity(
                    activity, intent
                ) {
                    if (it?.resultCode == Activity.RESULT_OK) {
                        viewBind.txtProfileLanguages.text = it.data?.getStringExtra("language")
                    }
                }
            }
        }
    }

    private fun validate() {
        if (ConnectivityListener.checkInternet()) {
            val user = FlatShareApplication.getDbInstance().userDao().getUser()
            user?.status = viewBind.edtProfileStatus.text.toString().trim()
            user?.work = viewBind.edtProfileWork.text.toString().trim()

            // interests
            var text = viewBind.txtProfileInterest.text.toString()
            if (text.isNotEmpty()) {
                val arr = TextUtils.split(text, ", ")
                user?.flatProperties?.interests = arr.toCollection(ArrayList())
            }


            // languages
            text = viewBind.txtProfileLanguages.text.toString()
            if (text.isNotEmpty()) {
                val arr = TextUtils.split(text, ", ")
                user?.flatProperties?.languages = arr.toCollection(ArrayList())
            }

            if (activity.latProfile[0] != null) {
                val loc = Loc()
                loc.coordinates.add(activity.latProfile[0]?.coordinates!![0])
                loc.coordinates.add(activity.latProfile[0]?.coordinates!![1])
                user?.hometown =
                    ModelLocation(dataBinder.edt_profile[0].getText().toString(), loc)
            }
            if (activity.latProfile[1] != null) {
                val loc = Loc()
                loc.coordinates.add(activity.latProfile[1]?.coordinates!![0])
                loc.coordinates.add(activity.latProfile[1]?.coordinates!![1])
                user?.college =
                    ModelLocation(dataBinder.edt_profile[1].text.toString(), loc)
            }
//            prepareImages(user)
            callAPi(user)
        }
    }

    private fun prepareImages(user: User?) {
        // Check for images to delete
        if (dataBinder.deletedUserImages.size > 0) {
            activity.apiManager.showProgress()
            val deleteFile = AmazonDeleteFile()
            deleteImages(deleteFile) { _: Intent?, requestCode: Int ->
                if (requestCode == 1) {
                    prepareUpload(user)
                }
            }
        } else prepareUpload(user)
    }

    private fun deleteImages(deleteFile: AmazonDeleteFile, onUiEventClick: OnUiEventClick) {
        val path: String = dataBinder.deletedUserImages[0]
        deleteFile.delete(path) { _: Intent?, requestCode: Int ->
            if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                dataBinder.deletedUserImages.removeAt(0)
//                dataBinder.apiUserImages.remove(path)
                if (dataBinder.deletedUserImages.size > 0) deleteImages(
                    deleteFile,
                    onUiEventClick
                ) else onUiEventClick.onClick(null, 1)
            } else {
                AlertDialog.showAlert(activity, "Failed to delete image")
                DialogCustomProgress.hideProgress(activity)
            }
        }
    }

    private fun prepareUpload(user: User?) {
        if (dataBinder.addedUserImages.size > 0) {
            activity.apiManager.showProgress()
            uploadImages { _: Intent?, requestCode: Int ->
                if (requestCode == 1) {
                    callAPi(user)
                }
            }
        } else callAPi(user)
    }

    private fun uploadImages(onUiEventClick: OnUiEventClick?) {
        val path: String = dataBinder.addedUserImages[0]
        AmazonUploadFile().upload(
            File(path),
            AmazonUploadFile.AWS_TYPE_USER_IMAGE
        ) { intent: Intent?, requestCode: Int ->
            if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                val serverPath = intent?.getStringExtra("localpath")
                if (!serverPath.isNullOrEmpty()) {
                    dataBinder.apiUserImages.add(serverPath)
                    dataBinder.addedUserImages.removeAt(0)
                    if (dataBinder.addedUserImages.size > 0) uploadImages(onUiEventClick)
                    else onUiEventClick?.onClick(
                        null,
                        1
                    )
                } else {
                    AlertDialog.showAlert(
                        activity,
                        "Failed to upload image"
                    )
                    DialogCustomProgress.hideProgress(activity)
                }

            } else {
                AlertDialog.showAlert(
                    activity,
                    "Failed to upload image"
                )
                DialogCustomProgress.hideProgress(activity)
            }
        }
    }

    private fun callAPi(user: User?) {
//        DialogCustomProgress.hideProgress(activity)
//        user?.images = dataBinder.apiUserImages
        activity.baseApiController.updateUser(true, user, object :
            OnUserFetched {
            override fun userFetched(resp: UserResponse?) {
                CommonMethod.finishActivity(activity)
            }
        })
    }

    private fun checkForUnsavedData() {
        var hasChanged = false
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        val newInterests = viewBind.txtProfileInterest.text.toString()
        val newLanguages = viewBind.txtProfileLanguages.text.toString()
        val newStatus: String = viewBind.edtProfileStatus.getText().toString().trim()
        val newWork: String = viewBind.edtProfileWork.getText().toString().trim()
        if (newLanguages != TextUtils.join(", ", user?.flatProperties?.languages!!))
            hasChanged = true
        else if (newInterests != TextUtils.join(", ", user.flatProperties.interests))
            hasChanged = true
        else if (newStatus != if (user.status == null) "" else user.status)
            hasChanged = true
        else if (newWork != if (user.work == null) "" else user.work)
            hasChanged = true
        else {
            val newSociety = activity.latProfile
            if (newSociety[0] != null && user.hometown != null
                && newSociety[0]!!.coordinates[0] != user.hometown!!.loc.coordinates[0]
            ) hasChanged = true
            if (!hasChanged) {
                if (newSociety[1] != null && user.college != null
                    && newSociety[1]!!.coordinates[0] != user.college!!.loc.coordinates[0]
                ) hasChanged = true
            }
        }
        if (!hasChanged) {
            // check the images
            if (dataBinder.addedUserImages.size > 0)
                hasChanged = true
            else if (dataBinder.deletedUserImages.size > 0)
                hasChanged = true
        }
        if (hasChanged)
            validate() else CommonMethod.finishActivity(activity)
    }
}