package com.joinflatshare.ui.preferences.flatmate

import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatmateBinding
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet

class PrefFlatmateListener
    (private val activity: PrefFlatmateActivity) : View.OnClickListener {
    private val viewBind: ActivityPrefFlatmateBinding = activity.viewBind

    var isMaleSelected = false
    var isFemaleSelected = false
    var isAllGenderSelected = false

    init {
        viewBind.includePrefFlatmate.txtMale.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtFemale.setOnClickListener(this)
        viewBind.includePrefFlatmate.txtall.setOnClickListener(this)
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnPrefFlatmateSearch.setOnClickListener(this)
        viewBind.txtPrefFlatmateCopyLink.setOnClickListener(this)
        viewBind.txtPrefFlatmateCloseSearch.setOnClickListener(this)
        switchListener()
    }

    override fun onClick(view: View?) {
        /*when (view?.id) {
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

            viewBind.btnPrefFlatmateSearch.id,
            viewBind.btnBack.id -> {
                checkForUnsavedData()
            }

            viewBind.txtPrefFlatmateCloseSearch.id -> {
                val flat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                if (flat == null) {
                    AppConstants.isFeedReloadRequired = true
                    activity.setResult(Activity.RESULT_OK)
                    onButtonClicked("Close Flatmate Search")
                    CommonMethod.finishActivity(activity)
                    return
                }
                flat.isMateSearch.value = false
                activity.apiManager.updateFlat(
                    true, flat
                ) { response: Any? ->
                    CommonMethod.makeToast("Your flatmate search is closed")
                    AppConstants.isFeedReloadRequired = true
                    activity.setResult(Activity.RESULT_OK)
                    onButtonClicked("Close Flatmate Search")
                    CommonMethod.finishActivity(activity)
                }
            }

            viewBind.txtPrefFlatmateCopyLink.id -> {
                activity.apiManager.showProgress()
                DeepLinkHandler.createFlatDynamicLink(
                    activity.updatingFlatData
                ) { text ->
                    DialogCustomProgress.hideProgress(activity);
                    if (!text.isNullOrBlank()) {
                        if (text != "0") {
                            val shareMessage =
                                FlatShareMessageGenerator.generateFlatShareMessage(activity.updatingFlatData) +
                                        "\n\n" + text
                            CommonMethods.copyToClipboard(activity, shareMessage)
                        }
                    }
                }
            }*/
    }

    private fun switchListener() {
        viewBind.includePrefFlatmate.switchVerifiedMember.setOnCheckedChangeListener { _, isChecked ->
            val list = ArrayList<ModelBottomSheet>()
            list.add(ModelBottomSheet(0, "Get Verified For Free"))
            list.add(ModelBottomSheet(0, "Join Flatshare Elite"))
            BottomSheetView(activity, list
            ) { view, position -> }


            /*var shouldCheck = false
            if (isChecked) {
                val myFLat = FlatShareApplication.getDbInstance().userDao().getFlatData()
                if (myFLat != null) {
                    if (myFLat.isVerified) {
                        onButtonClicked("GFV on FMS preferences screen")
                        shouldCheck = true
                    } else {
                        val intent = Intent(activity, FlatVerifyActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }
            viewBind.includePrefFlatmate.switchVerifiedMember.isChecked = shouldCheck
            activity.updatingFlatData?.flatProperties?.isVerifiedOnly = shouldCheck*/
        }
    }

    private fun checkForUnsavedData() {
        /*var hasChanged = false
        val actualFlatData = FlatShareApplication.getDbInstance().userDao().getFlatData()
        if (actualFlatData?.flatProperties?.isVerifiedOnly != activity.updatingFlatData?.flatProperties?.isVerifiedOnly)
            hasChanged = true
        else if (!actualFlatData?.flatProperties?.gender.equals(activity.updatingFlatData?.flatProperties?.gender)) {
            hasChanged = true
        } else if (actualFlatData?.flatProperties?.interests != activity.updatingFlatData?.flatProperties?.interests)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.languages != activity.updatingFlatData?.flatProperties?.languages) {
            hasChanged = true
        } else if (actualFlatData?.flatProperties?.dealBreakers == null && activity.updatingFlatData?.flatProperties?.dealBreakers != null) {
            hasChanged = true
        } else if (actualFlatData?.flatProperties?.dealBreakers?.smoking != activity.updatingFlatData?.flatProperties?.dealBreakers?.smoking)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.dealBreakers?.nonveg != activity.updatingFlatData?.flatProperties?.dealBreakers?.nonveg)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.dealBreakers?.flatparty != activity.updatingFlatData?.flatProperties?.dealBreakers?.flatparty)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.dealBreakers?.eggs != activity.updatingFlatData?.flatProperties?.dealBreakers?.eggs)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.dealBreakers?.pets != activity.updatingFlatData?.flatProperties?.dealBreakers?.pets)
            hasChanged = true
        else if (actualFlatData?.flatProperties?.dealBreakers?.workout != activity.updatingFlatData?.flatProperties?.dealBreakers?.workout)
            hasChanged = true
        if (hasChanged)
            activity.apiManager.updateFlat(
                true,
                activity.updatingFlatData!!
            ) {
                AppConstants.isFeedReloadRequired = true
                activity.setResult(Activity.RESULT_OK)
                CommonMethod.finishActivity(activity)
            }
        else CommonMethod.finishActivity(activity)*/
    }
}