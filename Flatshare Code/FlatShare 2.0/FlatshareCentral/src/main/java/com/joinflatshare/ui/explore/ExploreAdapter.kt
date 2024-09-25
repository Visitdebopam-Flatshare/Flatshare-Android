package com.joinflatshare.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.ui.explore.holder.AdapterUserHolder

class ExploreAdapter(
    val activity: ExploreActivity,
    private val items: ArrayList<UserRecommendationItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.layoutPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(position: Int) {
        if (position < items.size) {
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            items.removeAt(position)
        }
    }

    class ViewHolder(
        private val view: ItemExploreBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: ExploreAdapter
        ) {
            val item = adapter.items[position]
            AdapterUserHolder().bindUser(adapter, item, view, position)
        }
    }
}