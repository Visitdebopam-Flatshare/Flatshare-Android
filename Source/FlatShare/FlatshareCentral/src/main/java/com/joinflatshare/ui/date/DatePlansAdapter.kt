package com.joinflatshare.ui.date

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemActivitiesBinding
import com.joinflatshare.constants.ThemeConstants
import com.joinflatshare.constants.UrlConstants
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.system.ThemeUtils

class DatePlansAdapter(
    private val activity: DatePreferenceActivity,
    private val list: ArrayList<String>,
    private val recyclerView: RecyclerView
) :
    RecyclerView.Adapter<DatePlansAdapter.ActivitiesViewHolder>() {
    private var currentThemeUrl: String? = null
    private var alternateThemeUrl: String? = null
    private var imageUrl: String? = null
    private val matchedItems = ArrayList<String>()

    init {
        currentThemeUrl = ThemeUtils.getCurrentTheme(activity)
        alternateThemeUrl =
            if (ThemeUtils.isNowInLightMode(activity)) ThemeConstants.THEME_DARK else ThemeConstants.THEME_LIGHT
        imageUrl = UrlConstants.ACTIVITY_URL
        currentThemeUrl += "/"
        alternateThemeUrl += "/"
    }

    fun setActivityUrl() {
        imageUrl = UrlConstants.ACTIVITY_URL
    }

    fun setPlansUrl() {
        imageUrl = UrlConstants.PLANS_URL
    }

    fun setMatchedItems(matchedItems: ArrayList<String>?) {
        this.matchedItems.clear()
        if (!matchedItems.isNullOrEmpty())
            this.matchedItems.addAll(matchedItems)
    }

    fun getMatchedItems(): ArrayList<String> {
        return matchedItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val viewBind =
            ItemActivitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivitiesViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: ActivitiesViewHolder, position: Int) {
        mainHolder.populateActivities(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ActivitiesViewHolder(private val viewBind: ItemActivitiesBinding) :
        RecyclerView.ViewHolder(viewBind.root) {
        fun populateActivities(
            position: Int,
            adapter: DatePlansAdapter
        ) {
            val item = adapter.list[position]
            viewBind.txtDateActivities.text = item
            ImageHelper.loadImage(
                adapter.activity,
                R.mipmap.ic_launcher,
                viewBind.imgDateActivities,
                adapter.imageUrl + ThemeConstants.THEME_DARK + "/" + item + ".png"
            )

            if (adapter.matchedItems.contains(item)) {
                viewBind.cardDate.strokeColor = ContextCompat.getColor(
                    adapter.activity,
                    R.color.button_bg_black
                )
                viewBind.cardDate.setCardBackgroundColor(
                    ContextCompat.getColor(
                        adapter.activity,
                        R.color.button_bg_black
                    )
                )
                viewBind.txtDateActivities.setTextColor(
                    ContextCompat.getColor(
                        adapter.activity,
                        R.color.color_text_secondary
                    )
                )
                ImageHelper.loadImage(
                    adapter.activity,
                    R.mipmap.ic_launcher,
                    viewBind.imgDateActivities,
                    adapter.imageUrl + adapter.currentThemeUrl + item + ".png"
                )
            } else {
                viewBind.cardDate.strokeColor = ContextCompat.getColor(
                    adapter.activity,
                    R.color.color_hint
                )
                viewBind.cardDate.setCardBackgroundColor(
                    ContextCompat.getColor(
                        adapter.activity,
                        R.color.color_bg
                    )
                )
                viewBind.txtDateActivities.setTextColor(
                    ContextCompat.getColor(
                        adapter.activity,
                        R.color.color_text_primary
                    )
                )
                ImageHelper.loadImage(
                    adapter.activity,
                    R.mipmap.ic_launcher,
                    viewBind.imgDateActivities,
                    adapter.imageUrl + adapter.alternateThemeUrl + item + ".png"
                )
            }

            viewBind.cardDate.setOnClickListener {
                adapter.recyclerView.post {
                    val currentSelection: Boolean = adapter.matchedItems.contains(item)
                    if (currentSelection) {
                        adapter.matchedItems.remove(item)
                        updateAdapter(adapter, position)
                    } else {
                        val maxSize = 4
                        if (adapter.matchedItems.size < maxSize) {
                            adapter.matchedItems.add(item)
                            updateAdapter(adapter, position)
                        }
                    }
                }
            }
        }

        private fun updateAdapter(adapter: DatePlansAdapter, position: Int) {
            adapter.notifyItemChanged(position)
            adapter.activity.updatePlanCount("" + adapter.matchedItems.size)
        }
    }
}