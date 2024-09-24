package com.joinflatshare.ui.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemInviteBinding
import com.joinflatshare.pojo.user.User

class InviteAdapter(
    private val activity: InviteActivity,
    val items: List<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var width = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemInviteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    fun reload() {
        notifyDataSetChanged();
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.adapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val activity: InviteActivity,
        private val view: ItemInviteBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: InviteAdapter
        ) {
            when (activity.selectedType) {
                InviteActivity.INVITE_TYPE_APP -> {
                    InviteAppHolder(activity, view, position, adapter)
                }
                InviteActivity.INVITE_TYPE_FLAT -> {
                    InviteFlatHolder(activity, view, position, adapter)
                }
                InviteActivity.INVITE_TYPE_FRIEND -> {
                    InviteFriendHolder(activity, view, position, adapter)
                }
            }
        }
    }
}