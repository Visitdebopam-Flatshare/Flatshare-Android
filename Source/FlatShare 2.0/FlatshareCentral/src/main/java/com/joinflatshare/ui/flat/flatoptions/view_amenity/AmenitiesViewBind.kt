package com.joinflatshare.ui.flat.flatoptions.view_amenity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.pojo.amenities.AmenitiesItem
import com.joinflatshare.ui.dialogs.DialogFlatOptions

class AmenitiesViewBind(private val dialogFlatOptions: DialogFlatOptions) {
    var viewBind: DialogFlatOptionsBinding? = null
    var amenititesAdapter: AmenitiesAdapter? = null

    init {
        viewBind = dialogFlatOptions.viewBind
        dialogFlatOptions.setName("Amenities")
        viewBind?.rvFlatAmenities?.visibility = View.VISIBLE
        viewBind?.rvFlatFurnishing?.visibility = View.GONE
        setAmenity()
    }

    private fun setAmenity() {
        // Amenities
        val wholeSize = ArrayList<String>()
        if (dialogFlatOptions.loggedInUser?.isFlatSearch?.value == true
            && dialogFlatOptions.loggedInUser?.flatProperties?.amenities?.isEmpty() == false
        )
            wholeSize.addAll(dialogFlatOptions.loggedInUser?.flatProperties?.amenities!!)
        else if (dialogFlatOptions.flat?.isMateSearch?.value == true &&
            dialogFlatOptions.flat?.flatProperties?.amenities?.isEmpty() == false
        )
            wholeSize.addAll(dialogFlatOptions.flat?.flatProperties?.amenities!!)
        val items = ArrayList<AmenitiesItem>()
        for (amen in AppData.flatData!!.amenities) {
            val temp = AmenitiesItem()
            temp.isSelected = wholeSize.contains(amen)
            temp.id = 1
            temp.name = amen
            items.add(temp)
        }
        setAmenitiesAdapter(items)
    }

    private fun setAmenitiesAdapter(items: ArrayList<AmenitiesItem>) {
        if (amenititesAdapter == null) {
            viewBind?.rvFlatAmenities?.setLayoutManager(LinearLayoutManager(dialogFlatOptions.activity))
            amenititesAdapter = AmenitiesAdapter(viewBind?.rvFlatAmenities, items)
            viewBind?.rvFlatAmenities?.setAdapter(amenititesAdapter)
        } else amenititesAdapter?.notifyDataSetChanged()
    }
}