package com.joinflatshare.ui.preferences.flat

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.view.View
import android.widget.DatePicker
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.profile.myprofile.ProfileActivity
import com.joinflatshare.utils.deeplink.DeepLinkHandler
import com.joinflatshare.utils.deeplink.UserShareMessageGenerator
import com.joinflatshare.utils.google.AutoCompletePlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.DateUtils
import com.joinflatshare.utils.mixpanel.MixpanelUtils
import com.joinflatshare.utils.system.ThemeUtils
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Calendar

class PrefFlatListener(private val activity: PrefFlatActivity) : View.OnClickListener {
    private val viewBind: ActivityPrefFlatBinding = activity.viewBind

    var isMaleSelected = false
    var isFemaleSelected = false

    init {
        viewBind.includePrefFlat.txtPrefFlatLocation.setOnClickListener(this)
        viewBind.includePrefFlat.txtPrefFlatMovein.setOnClickListener(this)
        viewBind.includePrefFlat.cardPrefFlatPrivateRoom.setOnClickListener(this)
        viewBind.includePrefFlat.cardPrefFlatSharedRoom.setOnClickListener(this)
        viewBind.includePrefFlat.llShowless.setOnClickListener(this)
        viewBind.includePrefFlatmate.cardProfileMale.setOnClickListener(this)
        viewBind.includePrefFlatmate.cardProfileFemale.setOnClickListener(this)
        viewBind.includePrefFlatmate.llProfileInterests.setOnClickListener(this)
        viewBind.includePrefFlatmate.llProfileLanguages.setOnClickListener(this)
        viewBind.txtPrefFlatCopyLink.setOnClickListener(this)
        viewBind.txtPrefFlatCloseSearch.setOnClickListener(this)
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnPrefFlatSearch.setOnClickListener(this)
        switchListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.includePrefFlat.txtPrefFlatLocation.id -> {
                AutoCompletePlaces.getPlaces(
                    activity
                ) { intent, requestCode ->
                    if (requestCode == RequestCodeConstants.REQUEST_CODE_LOCATION) {
                        if (intent != null) {
                            val location = CommonMethods.getSerializable(
                                intent,
                                "location",
                                ModelLocation::class.java
                            )
                            activity.user?.flatProperties?.preferredLocation = ArrayList()
                            activity.user?.flatProperties?.preferredLocation?.add(location)
                            viewBind.includePrefFlat.txtPrefFlatLocation.text = location.name
                        }
                    }
                }
            }

            viewBind.includePrefFlat.txtPrefFlatMovein.id -> {
                val calendar = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    activity,
                    ThemeUtils.getTheme(activity),
                    { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val format: NumberFormat = DecimalFormat("00")
                        val dd = format.format(dayOfMonth.toLong())
                        val mm = format.format((monthOfYear + 1).toLong())
                        viewBind.includePrefFlat.txtPrefFlatMovein.text = "$dd/$mm/$year"
                        activity.user?.flatProperties?.moveinDate =
                            DateUtils.convertToServerFormat(viewBind.includePrefFlat.txtPrefFlatMovein.text.toString())
                    }, calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                dpd.datePicker.minDate = calendar.timeInMillis
                dpd.show()
            }

            viewBind.includePrefFlat.cardPrefFlatPrivateRoom.id -> {
                var roomType = activity.user?.flatProperties?.roomType
                if (roomType.isNullOrBlank()) {
                    roomType = "Private Room"
                } else {
                    when (roomType) {
                        "Both" -> {
                            roomType = "Shared Room"
                        }

                        "Private Room" -> {
                            roomType = ""
                        }

                        "Shared Room" -> {
                            roomType = "Both"
                        }
                    }
                }
                activity.user?.flatProperties?.roomType = roomType
                activity.setRoomType()
            }

            viewBind.includePrefFlat.cardPrefFlatSharedRoom.id -> {
                var roomType = activity.user?.flatProperties?.roomType
                if (roomType.isNullOrBlank()) {
                    roomType = "Shared Room"
                } else {
                    when (roomType) {
                        "Both" -> {
                            roomType = "Private Room"
                        }

                        "Private Room" -> {
                            roomType = "Both"
                        }

                        "Shared Room" -> {
                            roomType = ""
                        }
                    }
                }
                activity.user?.flatProperties?.roomType = roomType
                activity.setRoomType()
            }

            viewBind.includePrefFlat.llShowless.id -> {
                activity.showLessAmenities = !activity.showLessAmenities
                activity.setAmenities()
            }

            viewBind.includePrefFlatmate.cardProfileMale.id -> {
                isMaleSelected = !isMaleSelected
                activity.setGenderButton()
            }

            viewBind.includePrefFlatmate.cardProfileFemale.id -> {
                isFemaleSelected = !isFemaleSelected
                activity.setGenderButton()
            }

            viewBind.includePrefFlatmate.llProfileInterests.id -> {
                val interestsView = InterestsView(
                    activity,
                    InterestsView.VIEW_TYPE_INTERESTS,
                    viewBind.includePrefFlatmate.txtProfileInterest
                )
                interestsView.assignCallback(activity)
                interestsView.show()
            }

            viewBind.includePrefFlatmate.llProfileLanguages.id -> {
                val interestsView = InterestsView(
                    activity,
                    InterestsView.VIEW_TYPE_LANGUAGES,
                    viewBind.includePrefFlatmate.txtProfileLanguages
                )
                interestsView.assignCallback(activity)
                interestsView.show()
            }

            viewBind.btnPrefFlatSearch.id,
            viewBind.btnBack.id -> {
                validate()
            }

            viewBind.txtPrefFlatCloseSearch.id -> {
                val user = FlatShareApplication.getDbInstance().userDao().getUser()
                if (activity.isFhtView)
                    user?.isFHTSearch?.value = false
                else user?.isFlatSearch?.value = false
                activity.apiManager.updateProfile(
                    true, user
                ) {
                    AppConstants.isFeedReloadRequired = true
                    if (activity.isFhtView) {
                        CommonMethods.makeToast("Flathunt Together is closed")
                        MixpanelUtils.onButtonClicked("Close Flathunt Together Search")
                    } else {
                        CommonMethods.makeToast("Your shared flat search is closed")
                        MixpanelUtils.onButtonClicked("Close Shared Flat Search")
                    }
                    activity.setResult(Activity.RESULT_OK)
                    CommonMethod.finishActivity(activity)

                }
            }

            viewBind.txtPrefFlatCopyLink.id -> {
                activity.apiManager.showProgress()
                if (activity.isFhtView) {
                    DeepLinkHandler.createUserFHTDynamicLink(
                        AppConstants.loggedInUser
                    ) { text ->
                        activity.apiManager.hideProgress()
                        if (!text.isNullOrBlank()) {
                            if (text != "0") {
                                val shareMessage =
                                    UserShareMessageGenerator.generateFHTMessage(AppConstants.loggedInUser) + "\n\n" + text
                                CommonMethods.copyToClipboard(activity, shareMessage)
                            }
                        }
                    }
                } else {
                    DeepLinkHandler.createUserSFSDynamicLink(
                        AppConstants.loggedInUser
                    ) { text ->
                        activity.apiManager.hideProgress()
                        if (!text.isNullOrBlank()) {
                            if (text != "0") {
                                val shareMessage =
                                    UserShareMessageGenerator.generateUserMessage(AppConstants.loggedInUser) + "\n\n" + text
                                CommonMethods.copyToClipboard(activity, shareMessage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun switchListener() {
        viewBind.includePrefFlat.switchVerifiedFlat.setOnCheckedChangeListener { _, isChecked ->
            var shouldCheck = false
            if (isChecked) {
                val user = FlatShareApplication.getDbInstance().userDao().getUser()
                if (user != null) {
                    if (user.verification?.isVerified == true) {
                        MixpanelUtils.onButtonClicked("GV on SFS preferences screen")
                        shouldCheck = true
                    } else {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        intent.putExtra("verify", true)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }
            activity.user?.flatProperties?.isVerifiedOnly = shouldCheck
            viewBind.includePrefFlat.switchVerifiedFlat.isChecked = shouldCheck
        }

        viewBind.includePrefFlatmate.switchVerifiedMember.setOnCheckedChangeListener { _, isChecked ->
            var shouldCheck = false
            if (isChecked) {
                val user = FlatShareApplication.getDbInstance().userDao().getUser()
                if (user != null) {
                    if (user.verification?.isVerified == true) {
                        MixpanelUtils.onButtonClicked("GVon FHT preferences screen")
                        shouldCheck = true
                    } else {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        intent.putExtra("verify", true)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }
            activity.user?.flatProperties?.isVerifiedOnly = shouldCheck
            viewBind.includePrefFlatmate.switchVerifiedMember.isChecked = shouldCheck
        }
    }

    fun seekListener() {
        viewBind.includePrefFlat.seekbarRent.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
            activity.user?.flatProperties?.rentRange?.startRange = minValue.toInt()
            activity.user?.flatProperties?.rentRange?.endRange = maxValue.toInt()
            viewBind.includePrefFlat.txtPrefFlatRentrange.text =
                activity.resources.getString(R.string.currency) +
                        "${activity.user?.flatProperties?.rentRange?.startRange} - ${
                            activity.resources.getString(
                                R.string.currency
                            )
                        }${activity.user?.flatProperties?.rentRange?.endRange}"
        }
    }

    private fun validate() {
        if (activity.user?.flatProperties?.interests?.isEmpty() == true) {
            CommonMethods.makeToast("Please select your interests")
        } else if (activity.user?.flatProperties?.languages?.isEmpty() == true) {
            CommonMethods.makeToast("Please select your languages")
        } else checkForUnsavedData()
    }

    private fun checkForUnsavedData() {
        var hasChanged = false
        val actualUserData = FlatShareApplication.getDbInstance().userDao().getUser()
        // Get Flat size
        if (activity.flatsizeAdapter != null)
            activity.user?.flatProperties?.flatSize = activity.flatsizeAdapter!!.selectedFurnishes
        // Get amenities
        activity.user?.flatProperties?.amenities = ArrayList()
        if (activity.amenityAdapter != null)
            activity.user?.flatProperties?.amenities!!.addAll(activity.amenityAdapter!!.selectedAmenityList)
        // Get Flat Furnishing
        if (activity.furnishingAdapter != null)
            activity.user?.flatProperties?.furnishing =
                activity.furnishingAdapter!!.selectedFurnishes

        if (actualUserData?.flatProperties?.isVerifiedOnly != activity.user?.flatProperties?.isVerifiedOnly)
            hasChanged = true
        else if (actualUserData?.flatProperties?.preferredLocation?.equals(activity.user?.flatProperties?.preferredLocation) == false) {
            hasChanged = true
            MixpanelUtils.sendToMixPanel("Preferred Flat Location Filled")
        } else if (actualUserData?.flatProperties?.moveinDate != activity.user?.flatProperties?.moveinDate)
            hasChanged = true
        else if (actualUserData?.flatProperties?.rentRange?.startRange != activity.user?.flatProperties?.rentRange?.startRange)
            hasChanged = true
        else if (actualUserData?.flatProperties?.rentRange?.endRange != activity.user?.flatProperties?.rentRange?.endRange)
            hasChanged = true
        else if (actualUserData?.flatProperties?.flatSize != activity.user?.flatProperties?.flatSize)
            hasChanged = true
        else if (actualUserData?.flatProperties?.roomType?.equals(activity.user?.flatProperties?.roomType) == false)
            hasChanged = true
        else if (actualUserData?.flatProperties?.amenities != activity.user?.flatProperties?.amenities)
            hasChanged = true
        else if (actualUserData?.flatProperties?.furnishing != activity.user?.flatProperties?.furnishing)
            hasChanged = true
        else if (!actualUserData?.flatProperties?.gender.equals(activity.user?.flatProperties?.gender)) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.interests != activity.user?.flatProperties?.interests) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.languages != activity.user?.flatProperties?.languages) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.dealBreakers == null && activity.user?.flatProperties?.dealBreakers != null) {
            hasChanged = true
        } else if (actualUserData?.flatProperties?.dealBreakers?.smoking != activity.user?.flatProperties?.dealBreakers?.smoking)
            hasChanged = true
        else if (actualUserData?.flatProperties?.dealBreakers?.nonveg != activity.user?.flatProperties?.dealBreakers?.nonveg)
            hasChanged = true
        else if (actualUserData?.flatProperties?.dealBreakers?.flatparty != activity.user?.flatProperties?.dealBreakers?.flatparty)
            hasChanged = true
        else if (actualUserData?.flatProperties?.dealBreakers?.eggs != activity.user?.flatProperties?.dealBreakers?.eggs)
            hasChanged = true
        else if (actualUserData?.flatProperties?.dealBreakers?.pets != activity.user?.flatProperties?.dealBreakers?.pets)
            hasChanged = true
        else if (actualUserData?.flatProperties?.dealBreakers?.workout != activity.user?.flatProperties?.dealBreakers?.workout)
            hasChanged = true

        if (hasChanged)
            activity.apiManager.updateProfile(
                true,
                activity.user
            )
            {
                AppConstants.isFeedReloadRequired = true
                val intent = Intent()
                intent.putExtra("isFHT", activity.isFhtView)
                activity.setResult(Activity.RESULT_OK, intent)
                CommonMethod.finishActivity(activity)
            }
        else CommonMethod.finishActivity(activity)
    }
}