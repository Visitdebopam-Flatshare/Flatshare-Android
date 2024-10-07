package com.joinflatshare.ui.connection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemChecksBinding
import com.joinflatshare.pojo.explore.UserRecommendationItem
import com.joinflatshare.ui.explore.holder.AdapterUserHolder

class ConnectionAdapter(
    private val activity: ConnectionListActivity,
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

    class ViewHolder(
        private val view: ItemChecksBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: ConnectionAdapter
        ) {
            val item = adapter.items[position]
            AdapterUserHolder().bindUser(adapter.activity, item, position, view)
        }
    }
}