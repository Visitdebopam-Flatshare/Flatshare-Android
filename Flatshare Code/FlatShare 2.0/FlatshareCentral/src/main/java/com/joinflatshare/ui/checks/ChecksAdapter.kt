package com.joinflatshare.ui.checks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksBinding
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.ui.explore.holder.AdapterUserHolder

class ChecksAdapter(
    private val activity: ChecksActivity,
    private val items: ArrayList<UserRecommendationItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemChecksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    class ViewHolder(
        private val view: ItemChecksBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: ChecksAdapter
        ) {
            val item = adapter.items[position]
            AdapterUserHolder().bindUser(adapter.activity, item, position, view)
        }
    }
}