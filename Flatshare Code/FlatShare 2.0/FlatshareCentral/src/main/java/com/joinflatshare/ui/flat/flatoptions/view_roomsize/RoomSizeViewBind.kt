package com.joinflatshare.ui.flat.flatoptions.view_roomsize

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.pojo.amenities.AmenitiesItem
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

class RoomSizeViewBind() {
    var dialogFlatOptions: DialogFlatOptions? = null
    var viewBind: DialogFlatOptionsBinding? = null
    var roomSizeAdapter: RoomSizeAdapter? = null
    var viewType: Int = 0

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewType = RoomSizeAdapter.VIEW_TYPE_MULTIPLE_SELECTION
        init("")
    }

    constructor(dialogFlatOptions: DialogFlatOptions, selected: String) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewType = RoomSizeAdapter.VIEW_TYPE_SINGLE_SELECTION
        init(selected)
    }

    private fun init(selected: String) {
        viewBind = dialogFlatOptions?.viewBind
        viewBind?.rvFlatFurnishing?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Flat Type")
        setRoomSize(selected)
    }

    private fun setRoomSize(selected: String) {
        if (AppData.flatData?.flatSize?.isEmpty() == true) {
            CommonMethod.makeToast( "We did not find any flat types")
            return
        } else {
            val wholeSize = ArrayList<String>()
            if (dialogFlatOptions?.loggedInUser?.isFlatSearch?.value == true
                && !dialogFlatOptions?.loggedInUser?.flatProperties?.flatSize.isNullOrEmpty()
            ) {
                wholeSize.addAll(
                    dialogFlatOptions?.loggedInUser?.flatProperties?.flatSize!!
                )
            } else {
                if (!selected.isEmpty())
                    wholeSize.add(selected)
            }
            val items = ArrayList<AmenitiesItem>()
            for (size in AppData.flatData!!.flatSize) {
                val temp = AmenitiesItem()
                temp.isSelected = wholeSize.contains(size)
                temp.id = 1
                temp.name = size
                items.add(temp)
            }
            if (roomSizeAdapter == null) {
                viewBind?.rvFlatFurnishing?.setLayoutManager(
                    GridLayoutManager(
                        dialogFlatOptions?.activity,
                        2
                    )
                )
                roomSizeAdapter = RoomSizeAdapter(
                    dialogFlatOptions?.activity,
                    viewBind?.rvFlatFurnishing,
                    items,
                    viewType
                )
                if (viewBind?.rvFlatFurnishing?.itemDecorationCount!! < 1) {
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
                viewBind?.rvFlatFurnishing?.adapter = roomSizeAdapter
            } else roomSizeAdapter?.notifyDataSetChanged()
        }

    }
}