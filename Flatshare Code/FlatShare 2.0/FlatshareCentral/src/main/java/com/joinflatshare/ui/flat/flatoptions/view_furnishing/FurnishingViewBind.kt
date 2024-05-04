package com.joinflatshare.ui.flat.flatoptions.view_furnishing

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.pojo.amenities.AmenitiesItem
import com.joinflatshare.ui.dialogs.DialogFlatOptions

class FurnishingViewBind(private val dialogFlatOptions: DialogFlatOptions) {
    var viewBind: DialogFlatOptionsBinding? = null

    var furnishingAdapter: FurnishingAdapter? = null
    private var isSingleFurnishingSelection = false

    init {
        viewBind = dialogFlatOptions.viewBind
        dialogFlatOptions.setName("Furnishing")
        viewBind?.rvFlatAmenities?.visibility = View.GONE
        viewBind?.rvFlatFurnishing?.visibility = View.VISIBLE
        setFurnishing()
    }

    private fun setFurnishing() {
        val wholeSize = ArrayList<String>()
        if (dialogFlatOptions.loggedInUser?.isFlatSearch?.value == true) {
            isSingleFurnishingSelection = false
            if (dialogFlatOptions.loggedInUser?.flatProperties?.furnishing?.isEmpty() == false
            ) {
                wholeSize.addAll(dialogFlatOptions.loggedInUser!!.flatProperties.furnishing)
            }
        } else if (dialogFlatOptions.flat?.isMateSearch?.value == true) {
            isSingleFurnishingSelection = true
            if (dialogFlatOptions.flat?.flatProperties?.furnishing?.isEmpty() == false) {
                wholeSize.addAll(dialogFlatOptions.flat?.flatProperties?.furnishing!!)
            }
        }
        val items = ArrayList<AmenitiesItem>()
        // Furnishing
        for (amen in AppData.flatData!!.furnishing) {
            val temp = AmenitiesItem()
            temp.isSelected = wholeSize.contains(amen)
            temp.id = 1
            temp.name = amen
            items.add(temp)
        }
        setFurnishingAdapter(items)
    }

    private fun setFurnishingAdapter(items: ArrayList<AmenitiesItem>) {
        if (furnishingAdapter == null) {
            viewBind?.rvFlatFurnishing?.setLayoutManager(
                GridLayoutManager(
                    dialogFlatOptions.activity,
                    2
                )
            )
            furnishingAdapter =
                FurnishingAdapter(
                    dialogFlatOptions.activity,
                    viewBind?.rvFlatFurnishing,
                    isSingleFurnishingSelection,
                    items
                )
            if (viewBind?.rvFlatFurnishing?.getItemDecorationCount()!! < 1) {
                val spanCount = 2
                val spacing = 25
                val includeEdge = false
                viewBind?.rvFlatFurnishing?.addItemDecoration(
                    GridSpacingItemDecoration(
                        spanCount,
                        spacing,
                        includeEdge
                    )
                )
            }
            viewBind?.rvFlatFurnishing?.setAdapter(furnishingAdapter)
        } else furnishingAdapter?.notifyDataSetChanged()
    }
}