package com.joinflatshare.ui.register.ask_friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ItemInviteBinding
import com.joinflatshare.pojo.user.User
import com.joinflatshare.utils.helper.ImageHelper

class AskFriendAdapter(
    private val activity: AskFriendActivity,
    val items: List<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var width = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemInviteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val activity: AskFriendActivity,
        private val holder: ItemInviteBinding
    ) :
        RecyclerView.ViewHolder(holder.root) {
        fun bind(
            position: Int,
            adapter: AskFriendAdapter
        ) {
            val item = adapter.items[position]
            holder.llPeopleonflatshare.visibility = View.GONE
            holder.txtInviteName.visibility = View.VISIBLE
            holder.txtInviteName.text = item.name?.firstName
            holder.txtInviteNumber.setVisibility(View.VISIBLE)
            holder.txtInviteNumber.setText(item.id)
            holder.btnInvite.visibility = View.GONE
            holder.btnInvited.visibility = View.GONE
            holder.btnAdded.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            holder.btnAdd.text = "Ask"
            ImageHelper.loadImage(
                activity, R.drawable.ic_user,
                holder.imgInvite, ImageHelper.getProfileImageWithAws(item)
            )
            holder.btnAdd.setOnClickListener {
                activity.apiController.requestInvite(item.id)
            }
        }
    }
}