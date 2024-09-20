package com.joinflatshare.customviews.bottomsheet

import android.view.View
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.FlatshareCentral.databinding.DialogBottomsheetBinding
import com.joinflatshare.interfaces.OnitemClick
import com.joinflatshare.ui.bottomsheet.BottomSheetBaseView

/**
 * Created by debopam on 09/07/24
 */
class BottomSheetView(
    private val activity: ComponentActivity,
    private val modelBottomSheets: ArrayList<ModelBottomSheet>,
    private val onItemClick: OnitemClick
) : BottomSheetBaseView(activity) {
    private lateinit var viewBind: DialogBottomsheetBinding
    private lateinit var adapter: BottomSheetAdapter
    private lateinit var dialog: BottomSheetDialog

    init {
        create()
    }

    private fun create() {
        dialog = BottomSheetDialog(activity)
        viewBind = DialogBottomsheetBinding.inflate(activity.layoutInflater)
        dialog.setContentView(viewBind.root)
        setup()
        click()
        showDialog(dialog)
    }

    private fun setup() {
        viewBind.rvBottomsheet.layoutManager = LinearLayoutManager(activity)
        adapter = BottomSheetAdapter(activity, modelBottomSheets)
        viewBind.rvBottomsheet.adapter = adapter
    }

    private fun click() {
        adapter.setClickListener { view: View?, position: Int ->
            onItemClick.onitemclick(view, position)
            dismissDialog(dialog)
        }
    }
}