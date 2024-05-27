package com.joinflatshare.customviews.interests

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogInterestsBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods

class DialogInterests(
    private val activity: ApplicationBaseActivity,
    private val callback: OnUiEventClick
) {
    lateinit var viewType: String

    constructor(
        activity: ApplicationBaseActivity, viewType: String,
        callback: OnUiEventClick
    ) : this(activity, callback) {
        this.viewType = viewType
    }

    var viewBind: DialogInterestsBinding
    var interestsAdapter: InterestsAdapter? = null
    val dialog: AlertDialog
    private val selectedItems = ArrayList<String>()
    val adapterItems = ArrayList<String>()

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        viewBind = DialogInterestsBinding.inflate(activity.layoutInflater)
        builder.setView(viewBind.root)
        builder.setCancelable(true)
        dialog = builder.create()
        viewBind.rvInterests.requestFocus()
        viewBind.rvInterests.requestFocusFromTouch()
    }

    fun show(selectedInterests: ArrayList<String>) {
        selectedItems.clear()
        selectedItems.addAll(selectedInterests)
        viewBind.txtOptionHeader.text =
            if (viewType == InterestsView.VIEW_TYPE_INTERESTS) "Interests" else "Languages"
        viewBind.txtHeaderCount.text = "" + selectedInterests.size
        setRoomSize()
        click()
        CommonMethods.showDialog(activity, dialog)
    }

    private fun setRoomSize() {
        viewBind.txtHeaderCountTotal.text =
            if (viewType == InterestsView.VIEW_TYPE_INTERESTS) "/5 selected)" else "/3 selected)"
        viewBind.viewBg.setOnClickListener { }
        if (AppData.flatData?.interests != null) {
            adapterItems.clear()
            adapterItems.addAll(
                if (viewType == InterestsView.VIEW_TYPE_INTERESTS)
                    AppData.flatData?.interests!! else AppData.flatData?.languages!!
            )
            setAdapter()
        } else {
            CommonMethod.makeToast("No Interests found")
            CommonMethods.dismissDialog(activity, dialog)
        }
    }

    private fun setAdapter() {
        viewBind.rvInterests.layoutManager = GridLayoutManager(activity, 2)
        interestsAdapter = InterestsAdapter(
            activity, this, viewBind.rvInterests, viewType, adapterItems, selectedItems
        )
        if (viewBind.rvInterests.itemDecorationCount < 1) {
            val spanCount = 2
            val spacing = 25
            val includeEdge = false
            viewBind.rvInterests.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount,
                    spacing,
                    includeEdge
                )
            )
        }
        viewBind.rvInterests.adapter = interestsAdapter
        search()
    }

    private fun click() {
        viewBind.imgAmenititesCross.setOnClickListener { view ->
            CommonMethods.dismissDialog(activity, dialog)
        }

        viewBind.btnPopupSave.setOnClickListener {
            val intent = Intent()
            intent.putExtra("interest", interestsAdapter?.interests)
            intent.putExtra("interestlist", interestsAdapter?.interestList)
            intent.putExtra("count", selectedItems.size)
            intent.putExtra("type", viewType)
            callback.onClick(intent, 1)
            CommonMethods.dismissDialog(activity, dialog)
        }
    }

    private fun search() {
        viewBind.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                if (AppData.flatData?.interests != null) {
                    val query = text.toString().trim()
                    adapterItems.clear()
                    if (query.isBlank()) {
                        adapterItems.addAll(AppData.flatData!!.interests)
                    } else {
                        for (interest in AppData.flatData?.interests!!) {
                            if (interest.lowercase().contains(query.lowercase())) {
                                adapterItems.add(interest)
                            }
                        }
                    }
                    interestsAdapter?.notifyDataSetChanged()
                }
            }

        })
    }
}