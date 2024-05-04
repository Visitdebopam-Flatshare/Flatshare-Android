package com.joinflatshare.ui.flat.flatoptions

import android.content.Intent
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.api.retrofit.OnResponseCallback
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.flat.flat_profile.MyFlatActivity
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.helper.DateUtils.convertToServerFormat

class FlatOptionListener(
    private val flatOption: FlatOptionActivity,
    private var shouldCallApi: Boolean,
    private val callback: OnUiEventClick?
) {

    fun performClick(isSavedClicked: Boolean) {
        var isUpdateFlat = false
        val flatData = FlatShareApplication.getDbInstance().userDao().getFlatData()
        val user = FlatShareApplication.getDbInstance().userDao().getUser()
        when (flatOption.selectedViewConstant) {
            FlatOptionConstant.VIEW_CONSTANT_GENDER -> {
                if (flatData?.isMateSearch?.value == true) {
                    isUpdateFlat = true
                    flatData.flatProperties.gender = flatOption.genderViewBind.setGenderIntoApi()
                } else if (user?.isFlatSearch?.value == true) {
                    isUpdateFlat = false
                    user.flatProperties.gender = flatOption.genderViewBind.setGenderIntoApi()
                }
                flatOption.genderViewBind.viewBind?.llFlatGender?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_MOVEINDATE -> {
                val serverFormat: String
                if (flatOption.moveInViewBind.dateSelected.isEmpty()) {
                    serverFormat = ""
                } else serverFormat = convertToServerFormat(flatOption.moveInViewBind.dateSelected)
                if (flatData?.isMateSearch?.value == true) {
                    isUpdateFlat = true
                    flatData.flatProperties.moveinDate = serverFormat
                } else if (user?.isFlatSearch?.value == true) {
                    isUpdateFlat = false
                    user.flatProperties.moveinDate = serverFormat
                }
                flatOption.moveInViewBind.viewBind?.llFlatDate?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_ROOMSIZE -> {
                user?.flatProperties?.flatSize =
                    flatOption.roomSizeViewBind.roomSizeAdapter?.getRoomSize()!!
                isUpdateFlat = false
                flatOption.roomSizeViewBind.viewBind?.rvFlatFurnishing?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_AMENITY -> {
                val selectedAmenities: ArrayList<String> =
                    flatOption.amenitiesViewBind.amenititesAdapter?.selectedAmenities!!
                if (flatData?.isMateSearch?.value == true) {
                    flatData.flatProperties.amenities = selectedAmenities
                    isUpdateFlat = true
                } else if (user?.isFlatSearch?.value == true) {
                    user.flatProperties.amenities = selectedAmenities
                    isUpdateFlat = false
                }
                flatOption.amenitiesViewBind.viewBind?.rvFlatAmenities?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_FURNISHING -> {
                val selectedFurnishes: ArrayList<String> =
                    flatOption.furnishingViewBind.furnishingAdapter?.selectedFurnishes!!
                if (flatData?.isMateSearch?.value == true) {
                    flatData.flatProperties.furnishing = selectedFurnishes
                    isUpdateFlat = true
                } else if (user?.isFlatSearch?.value == true) {
                    user.flatProperties.furnishing = selectedFurnishes
                    isUpdateFlat = false
                }
                flatOption.furnishingViewBind.viewBind?.rvFlatFurnishing?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_SHARED_PREFERRED_LOCATION -> {
                if (flatOption.preferredLocationViewBind.modelLocation.size == 0) shouldCallApi =
                    false
                else {
                    user?.flatProperties?.preferredLocation?.clear()
                    for (location in flatOption.preferredLocationViewBind.modelLocation) {
                        if (location.loc.coordinates.size > 0) {
                            user?.flatProperties?.preferredLocation?.add(location)
                        }
                    }
                }
                flatOption.preferredLocationViewBind.viewBind?.llPreferredLocation?.visibility =
                    View.GONE
                isUpdateFlat = false
            }

            FlatOptionConstant.VIEW_CONSTANT_LOCATION -> {
                if (flatOption.locationViewBind.modelLocation.name.isBlank()) shouldCallApi = false
                else flatData?.flatProperties?.location = flatOption.locationViewBind.modelLocation
                flatOption.locationViewBind.viewBind?.txtFlatLocation?.visibility = View.GONE
                isUpdateFlat = true
            }

            FlatOptionConstant.VIEW_CONSTANT_RENT -> {
                val rent: String =
                    flatOption.dialogFlatOptions?.viewBind?.edtFlatRent?.getText().toString()
                if (!rent.isBlank()) {
                    flatData!!.flatProperties.rentperPerson = Integer.parseInt(rent)
                    isUpdateFlat = true
                }
                flatOption.rentViewBind.viewBind?.llFlatRent?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_DEPOSIT -> {
                val rent = flatOption.dialogFlatOptions?.viewBind?.edtFlatRent?.getText().toString()
                if (!rent.isBlank()) {
                    flatData?.flatProperties?.depositperPerson = Integer.parseInt(rent)
                    isUpdateFlat = true
                }
                flatOption.rentViewBind.viewBind?.llFlatRent?.visibility = View.GONE
            }

            FlatOptionConstant.VIEW_CONSTANT_ROOMTYPE -> {
                val w: String? = flatOption.roomTypeViewBind?.roomTypeAdapter?.roomType
                user?.flatProperties?.roomType = w!!
                flatOption.roomTypeViewBind.viewBind?.rvFlatFurnishing?.visibility = View.GONE
                isUpdateFlat = false
            }

            FlatOptionConstant.VIEW_CONSTANT_RENT_RANGE -> {
                user?.flatProperties?.rentRange?.startRange = Integer.parseInt(
                    flatOption.dialogFlatOptions?.viewBind?.txtRentRangeMin?.text.toString()
                        .replace(
                            flatOption.activity.getResources().getString(R.string.currency), ""
                        )
                )

                user?.flatProperties?.rentRange?.endRange = Integer.parseInt(
                    flatOption.dialogFlatOptions?.viewBind?.txtRentRangeMax?.text.toString()
                        .replace(
                            flatOption.activity.getResources().getString(R.string.currency), ""
                        )
                )
                flatOption.rentRangeViewBind?.viewBind?.llRentRange?.visibility = View.GONE
                isUpdateFlat = false
            }
        }

        CommonMethods.hideSoftKeyboard(flatOption.activity)
        if (isSavedClicked) {
            if (shouldCallApi) {
                if (isUpdateFlat) {
                    flatOption.activity.apiManager.updateFlat(
                        false,
                        flatData!!,
                        OnResponseCallback { response: Any? ->
                            if (flatOption.activity is MyFlatActivity) {
                                val act = flatOption.activity as MyFlatActivity
                                act.setResponse()
                                act.dataBind.calculateFlatmateCompletion()
                                AppConstants.isFeedReloadRequired = true
                            }
                        })
                } else {
                    flatOption.activity.baseApiController.updateUser(true, user, object :
                        OnUserFetched {
                        override fun userFetched(resp: UserResponse?) {
                            if (flatOption.activity is MyFlatActivity) {
                                val act = flatOption.activity as MyFlatActivity
                                act.setResponse()
                                AppConstants.isFeedReloadRequired = true
                            }
                        }
                    })
                }
            } else {
                val intent = Intent()
                intent.putExtra("user", user)
                intent.putExtra("flat", flatData)
                intent.putExtra("constant", flatOption.selectedViewConstant)
                callback?.onClick(intent, 1)
            }
        }
    }
}