package com.joinflatshare.ui.flat.flatoptions.view_roomtype

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.DialogFlatOptionsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.pojo.amenities.AmenitiesItem
import com.joinflatshare.ui.dialogs.DialogFlatOptions
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

class RoomTypeViewBind() {
    var viewBind: DialogFlatOptionsBinding? = null
    private var dialogFlatOptions: DialogFlatOptions? = null
    var roomTypeAdapter: RoomTypeAdapter? = null

    constructor(dialogFlatOptions: DialogFlatOptions) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init("")
    }

    constructor(dialogFlatOptions: DialogFlatOptions, selected: String) : this() {
        this.dialogFlatOptions = dialogFlatOptions
        viewBind = dialogFlatOptions.viewBind
        init(selected)
    }

    private fun init(selected: String) {
        viewBind?.rvFlatFurnishing?.visibility = View.VISIBLE
        dialogFlatOptions?.setName("Room Type")
        setRoomType(selected)
    }

    fun setRoomType(selected: String) {
        var selected = selected
        if (AppData.flatData == null || AppData.flatData!!.roomType.isEmpty()) {
            CommonMethod.makeToast( "We did not find any room types")
            return
        }
        if (dialogFlatOptions?.loggedInUser?.isFlatSearch?.value == true) {
            selected = dialogFlatOptions?.loggedInUser?.flatProperties?.roomType!!
        }
        val items = ArrayList<AmenitiesItem>()
        for (size in AppData.flatData!!.roomType) {
            val temp = AmenitiesItem()
            temp.isSelected = selected == size
            temp.id = 1
            temp.name = size
            items.add(temp)
        }
        setAdapter(items)
    }

    private fun setAdapter(items: ArrayList<AmenitiesItem>) {
        if (roomTypeAdapter == null) {
            viewBind?.rvFlatFurnishing?.setLayoutManager(
                GridLayoutManager(
                    dialogFlatOptions?.activity,
                    2
                )
            )
            roomTypeAdapter = RoomTypeAdapter(viewBind?.rvFlatFurnishing, items)
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
            viewBind?.rvFlatFurnishing?.setAdapter(roomTypeAdapter)
        } else roomTypeAdapter!!.notifyDataSetChanged()
    }
}