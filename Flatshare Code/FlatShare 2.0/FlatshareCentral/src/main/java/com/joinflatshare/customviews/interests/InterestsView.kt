package com.joinflatshare.customviews.interests

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.alert.AlertDialog
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 17/12/22
 */
class InterestsView(private val activity: BaseActivity, val viewType: String) {
    lateinit var recyclerView: RecyclerView
    val content = ArrayList<String>()
    val matchedContent = ArrayList<String>()
    var callback: OnUiEventClick? = null
    private var isOnlyViewable: Boolean = true
    private var adapter: InterestAdapter? = null


    constructor(
        activity: BaseActivity,
        recyclerView: RecyclerView,
        viewType: String,
        isOnlyViewable: Boolean
    ) : this(
        activity,
        viewType
    ) {
        this.isOnlyViewable = isOnlyViewable
        this.recyclerView = recyclerView
    }

    @Deprecated("To be removed")
    constructor(
        activity: BaseActivity,
        recyclerView: RecyclerView, viewType: String
    ) : this(
        activity,
        viewType
    ) {
    }

    @Deprecated("To be removed")
    constructor(activity: BaseActivity, viewType: String, textView: TextView) : this(
        activity,
        viewType
    ) {
        compareSelectedContent(textView)
    }

    companion object {
        const val VIEW_TYPE_INTERESTS = "INTERESTS"
        const val VIEW_TYPE_LANGUAGES = "LANGUAGES"
    }

    fun assignCallback(callback: OnUiEventClick) {
        this.callback = callback
    }

    fun setContentValues(content: ArrayList<String>?) {
        this.content.clear()
        if (content != null)
            this.content.addAll(content)
    }

    fun show() {
        if (viewType.isBlank())
            throw Exception("Interest or Language view type cannot be null")
        var message = ""
        when (viewType) {
            VIEW_TYPE_LANGUAGES -> {
                if (AppData.flatData?.languages.isNullOrEmpty()
                ) {
                    message = "We did not find any languages"
                }
            }

            VIEW_TYPE_INTERESTS -> {
                if (AppData.flatData?.interests.isNullOrEmpty()
                ) {
                    message = "We did not find any interests"
                }
            }
        }
        if (message.isNotEmpty()
        ) {
            AlertDialog.showAlert(activity, message)
            return
        }

        if (content.isEmpty())
            content.addAll(
                if (viewType == VIEW_TYPE_INTERESTS) AppData.flatData?.interests!!
                else AppData.flatData?.languages!!
            )

        val layoutManager = FlexboxLayoutManager(activity)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recyclerView.layoutManager = layoutManager
        adapter = InterestAdapter(activity, content, matchedContent, viewType, callback)
        recyclerView.adapter = adapter

        if (!isOnlyViewable) {
            if (callback == null) {
                message = "There is no callback on adapter"
                AlertDialog.showAlert(activity, message)
                return
            }

        }
    }

    fun calculateMatchingContent(compareContent: ArrayList<String>?) {
        this.matchedContent.clear()
        if (compareContent != null) {
            for (item in content) {
                var found = false
                for (compareItem in compareContent) {
                    if (compareItem.equals(item)) {
                        found = true
                        break
                    }
                }
                if (found) matchedContent.add(item)
            }
        }
    }

    private fun compareSelectedContent(textView: TextView) {
        this.matchedContent.clear()
        if (textView.text.toString().isNotEmpty()) {
            val text = textView.text.toString()
            val split = text.split(", ").toTypedArray()
            if (split.isNotEmpty()) {
                for (txt in split) {
                    matchedContent.add(txt)
                }
            }
        }
    }
}