package com.joinflatshare.utils.google

import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.joinflatshare.constants.GoogleConstants.initialiseGoogleSdk
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_LOCATION
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.user.Loc
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.system.GlobalActivityResult
import java.util.Arrays

object AutoCompletePlaces {
    fun getPlaces(activity: ApplicationBaseActivity, callback: OnUiEventClick) {
        try {
            initialiseGoogleSdk(activity) { text: String ->
                if (text == "1") {
                    val fields =
                        Arrays.asList(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.LAT_LNG
                        )
                    val countryList = ArrayList<String>()
                    countryList.add("IN")
                    val intnt =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                            .setCountries(countryList).build(activity)
                    CommonMethod.switchActivity(
                        activity,
                        intnt, object : GlobalActivityResult.OnActivityResult<ActivityResult?> {
                            override fun onActivityResult(result: ActivityResult?) {

                                if (result?.data != null) {
                                    val data = result.data!!
                                    CommonMethod.makeLog(
                                        "Auto Complete Places",
                                        Autocomplete.getStatusFromIntent(data).toString()
                                    )
                                    if (result.resultCode == AutocompleteActivity.RESULT_OK) {
                                        try {
                                            val place =
                                                Autocomplete.getPlaceFromIntent(result.data!!)
                                            val loc = Loc()
                                            place.latLng?.let { loc.coordinates.add(it.longitude) }
                                            place.latLng?.let { loc.coordinates.add(it.latitude) }
                                            val locationObject =
                                                place.name?.let { ModelLocation(it, loc) }
                                            val intent = Intent()
                                            intent.putExtra("location", locationObject)
                                            callback.onClick(intent, REQUEST_CODE_LOCATION)
                                            return
                                        } catch (exception: Exception) {
                                            CommonMethod.makeToast("Failed to fetch places")
                                        }
                                    }
                                }
                                callback.onClick(null, REQUEST_CODE_LOCATION)
                            }
                        }
                    )
                } else {
                    CommonMethod.makeToast("Failed to fetch places")
                    callback.onClick(null, REQUEST_CODE_LOCATION)
                }
            }
        } catch (exception: Exception) {
            CommonMethod.makeToast("Failed to fetch places")
            callback.onClick(null, REQUEST_CODE_LOCATION)
        }
    }
}