package com.joinflatshare.ui.profile.edit

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileEditBinding
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_LOCATION
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.user.Loc
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.User
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile
import com.joinflatshare.utils.amazonaws.AmazonUploadFile
import com.joinflatshare.utils.google.AutoCompletePlaces.getPlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.system.ConnectivityListener
import java.io.File

class ProfileEditListener(
    private val activity: ProfileEditActivity,
    private val viewBind: ActivityProfileEditBinding, private val dataBinder: ProfileEditDataBinder
) : View.OnClickListener, OnUiEventClick {

    init {
        countStatusLength()
        activity.baseViewBinder.btn_topbar_right.setOnClickListener(this)
        activity.baseViewBinder.btn_back.setOnClickListener(this)
        viewBind.llProfileInterests.setOnClickListener(this)
        viewBind.llProfileLanguages.setOnClickListener(this)
        for (i in dataBinder.edt_profile.indices) {
            val position: Int = i
            dataBinder.edt_profile[i].setOnClickListener { v: View? ->
                dataBinder.imgSearch[position].setImageResource(R.drawable.ic_search_blue)
                dataBinder.view_profile[position].setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.color_blue_light
                    )
                )
                getPlaces(
                    activity
                ) { intent: Intent?, requestCode: Int ->
                    if (requestCode == REQUEST_CODE_LOCATION) {
                        dataBinder.imgSearch[position].setImageResource(R.drawable.ic_search)
                        dataBinder.view_profile[position].setBackgroundColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.color_hint
                            )
                        )
                        if (intent != null) {
                            val location = CommonMethods.getSerializable(
                                intent,
                                "location",
                                ModelLocation::class.java
                            )
                            dataBinder.edt_profile[position].text = location!!.name
                            activity.latProfile[position] = location.loc
                            dataBinder.setCompleteCount()
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
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            activity.baseViewBinder.btn_topbar_right.id -> {
                validate()
            }

            activity.baseViewBinder.btn_back.id -> {
                checkForUnsavedData()
            }

            viewBind.llProfileInterests.id -> {
                val interestsView = InterestsView(
                    activity,
                    InterestsView.VIEW_TYPE_INTERESTS, viewBind.txtProfileInterest
                )
                interestsView.assignCallback(this)
                interestsView.show()
            }

            viewBind.llProfileLanguages.id -> {
                val interestsView = InterestsView(
                    activity,
                    InterestsView.VIEW_TYPE_LANGUAGES, viewBind.txtProfileLanguages
                )
                interestsView.assignCallback(this)
                interestsView.show()
            }
        }
    }

    private fun validate() {
        if (ConnectivityListener.checkInternet()) {
            val user = FlatShareApplication.getDbInstance().userDao().getUser()
            user?.website = viewBind.edtProfileWebsite.text.toString()
            user?.status = viewBind.edtProfileStatus.text.toString()
            user?.work = viewBind.edtProfileWork.text.toString()

            // website
            var text = viewBind.edtProfileWebsite.text.toString()
            if (text.isNotEmpty() && !CommonMethods.isValidUrl(text)) {
                CommonMethods.makeToast("Please type a valid website")
                return
            }

            // interests
            text = viewBind.txtProfileInterest.text.toString()
            if (text.isEmpty()) {
                CommonMethods.makeToast("Please select your interests")
                return
            }
            var items = ArrayList<String>()
            var split: Array<String?> = text.split(", ").toTypedArray()
            if (split.isNotEmpty()) {
                for (txt in split) {
                    items.add(txt!!)
                }
            }
            user?.flatProperties?.interests = items
            // languages
            items = ArrayList()
            text = viewBind.txtProfileLanguages.text.toString()
            if (text.isEmpty()) {
                CommonMethods.makeToast("Please select your languages")
                return
            }
            split = text.split(", ").toTypedArray()
            if (split.isNotEmpty()) {
                for (txt in split) {
                    items.add(txt!!)
                }
            }
            user?.flatProperties?.languages = items

            if (activity.latProfile[0] != null) {
                val loc = Loc()
                loc.coordinates.add(activity.latProfile[0]?.coordinates!![0])
                loc.coordinates.add(activity.latProfile[0]?.coordinates!![1])
                user?.hometown =
                    ModelLocation(dataBinder.edt_profile.get(0).getText().toString(), loc)
            }
            if (activity.latProfile[1] != null) {
                val loc = Loc()
                loc.coordinates.add(activity.latProfile[1]?.coordinates!![0])
                loc.coordinates.add(activity.latProfile[1]?.coordinates!![1])
                user?.college =
                    ModelLocation(dataBinder.edt_profile[1].text.toString(), loc)
            }
            if (activity.latProfile[2] != null) {
                val loc = Loc()
                loc.coordinates.add(activity.latProfile[2]?.coordinates!![0])
                loc.coordinates.add(activity.latProfile[2]?.coordinates!![1])
                user!!.hangout =
                    ModelLocation(dataBinder.edt_profile[2].text.toString(), loc)
            }
            prepareImages(user)
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
        val path: String = dataBinder.deletedUserImages.get(0)
        deleteFile.delete(path) { _: Intent?, requestCode: Int ->
            if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                dataBinder.deletedUserImages.removeAt(0)
                dataBinder.apiUserImages.remove(path)
                if (dataBinder.deletedUserImages.size > 0) deleteImages(
                    deleteFile,
                    onUiEventClick
                ) else onUiEventClick.onClick(null, 1)
            } else {
                AlertDialog.showAlert(activity, "Failed to delete image")
                activity.apiManager.hideProgress()
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
                    activity.apiManager.hideProgress()
                }

            } else {
                AlertDialog.showAlert(
                    activity,
                    "Failed to upload image"
                )
                activity.apiManager.hideProgress()
            }
        }
    }

    private fun callAPi(user: User?) {
        activity.apiManager.hideProgress()
        user?.images = dataBinder.apiUserImages
        activity.apiManager.updateProfile(true, user) { _: Any? ->
            CommonMethods.makeToast("Profile updated")
            CommonMethod.finishActivity(activity)
        }
    }

    private fun checkForUnsavedData() {
        var hasChanged = false
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        val newInterests = viewBind.txtProfileInterest.text.toString()
        val newLanguages = viewBind.txtProfileLanguages.text.toString();
        val newWebsite: String =
            viewBind.edtProfileWebsite.getText().toString().trim { it <= ' ' }
        val newStatus: String = viewBind.edtProfileStatus.getText().toString().trim { it <= ' ' }
        val newWork: String = viewBind.edtProfileWork.getText().toString().trim { it <= ' ' }
        if (newWebsite != if (user?.website == null) "" else user.website)
            hasChanged = true
        else if (!newLanguages.equals(
                TextUtils.join(
                    ", ",
                    user?.flatProperties?.languages!!
                )
            )
        )
            hasChanged = true
        else if (!newInterests.equals(
                TextUtils.join(
                    ", ",
                    user.flatProperties.interests
                )
            )
        )
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
            if (!hasChanged) {
                if (newSociety[2] != null && user.hangout != null
                    && newSociety[2]!!.coordinates[0] != user.hangout!!.loc.coordinates[0]
                ) hasChanged = true
            }
            if (!hasChanged && dataBinder.addedUserImages.size > 0) hasChanged = true
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

    override fun onClick(intent: Intent?, requestCode: Int) {
        if (requestCode == 1) {
            val type = intent?.getStringExtra("type")
            if (type == InterestsView.VIEW_TYPE_INTERESTS) {
                viewBind.txtProfileInterest.text = intent.getStringExtra("interest")
                viewBind.txtInterestCount.text = "(" + intent.getIntExtra("count", 0) + "/5)"
            } else if (type == InterestsView.VIEW_TYPE_LANGUAGES) {
                viewBind.txtProfileLanguages.text = intent.getStringExtra("interest")
                viewBind.txtLanguageCount.text = "(" + intent.getIntExtra("count", 0) + "/3)"
            }
        }
    }
}