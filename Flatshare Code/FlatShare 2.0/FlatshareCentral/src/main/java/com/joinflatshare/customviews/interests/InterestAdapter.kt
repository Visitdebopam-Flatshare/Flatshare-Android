package com.joinflatshare.customviews.interests

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemInterestsBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ThemeConstants
import com.joinflatshare.constants.UrlConstants.INTEREST_URL
import com.joinflatshare.constants.UrlConstants.LANGUAGE_URL
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.ImageHelper

/**
 * Created by debopam on 08/03/24
 */
class InterestAdapter(
    private val activity: BaseActivity,
    private var allItems: ArrayList<String>,
    private val matchedItems: ArrayList<String>,
    private val viewType: String,
    private val callback: OnUiEventClick?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageUrl: String = ""

    init {
        imageUrl = if (viewType == InterestsView.VIEW_TYPE_INTERESTS) INTEREST_URL else LANGUAGE_URL
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemInterestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as InterestAdapter.ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return allItems.size
    }

    class ViewHolder(
        private val activity: BaseActivity,
        private val holder: ItemInterestsBinding
    ) :
        RecyclerView.ViewHolder(holder.root) {
        fun bind(
            position: Int,
            adapter: InterestAdapter
        ) {

            val item = adapter.allItems[position]
            holder.txtAmenity.text = item
            if (adapter.viewType == InterestsView.VIEW_TYPE_INTERESTS)
                holder.imgLanguage.visibility = View.VISIBLE
            else holder.imgLanguage.visibility = View.GONE

            if (adapter.matchedItems.contains(item)) {
                holder.cardAmenity.setCardBackgroundColor(
                    ContextCompat.getColor(
                        activity, R.color.blue_dark
                    )
                )
                holder.txtAmenity.setTextColor(ContextCompat.getColor(activity, R.color.white))
                ImageHelper.loadImage(
                    activity,
                    R.mipmap.ic_launcher,
                    holder.imgLanguage,
                    adapter.imageUrl + item.replace(" ", "+") + ".png"
                )
            } else {
                holder.cardAmenity.setCardBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        android.R.color.transparent
                    )
                )
                holder.txtAmenity.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.black
                    )
                )
                ImageHelper.loadImage(
                    activity,
                    R.mipmap.ic_launcher,
                    holder.imgLanguage,
                    adapter.imageUrl + item.replace(" ", "+") + ".png"
                )
            }

            holder.cardAmenity.setOnClickListener {
                if (adapter.matchedItems.contains(item))
                    adapter.matchedItems.remove(item)
                else {
                    if (adapter.viewType == InterestsView.VIEW_TYPE_LANGUAGES) {
                        if (adapter.matchedItems.size == AppConstants.languageMaxCount)
                            return@setOnClickListener
                        adapter.matchedItems.add(item)
                    } else if (adapter.viewType == InterestsView.VIEW_TYPE_INTERESTS) {
                        if (adapter.matchedItems.size == AppConstants.interestMaxCount)
                            return@setOnClickListener
                        adapter.matchedItems.add(item)
                    }
                }
                if (adapter.callback != null) {
                    val intent = Intent()
                    intent.putExtra("count", adapter.matchedItems.size)
                    adapter.callback.onClick(intent, 0)
                }
                adapter.notifyItemChanged(position)
            }

        }
    }
}