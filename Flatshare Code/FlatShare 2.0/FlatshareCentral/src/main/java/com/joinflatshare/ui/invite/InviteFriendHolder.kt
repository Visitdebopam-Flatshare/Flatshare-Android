package com.joinflatshare.ui.invite

import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ItemInviteBinding
import com.joinflatshare.utils.helper.ImageHelper

class InviteFriendHolder(
    val activity: InviteActivity,
    val holder: ItemInviteBinding,
    val position: Int,
    val adapter: InviteAdapter
) {
    val item = adapter.items.get(position)

    init {
        bind()
    }

    private fun bind() {
        if (position == 0) {
            holder.llPeopleonflatshare.visibility = View.VISIBLE
            if (item.status.equals(InviteActivity.INVITE_STATUS_SUGGESTED_USERS))
                holder.txtInviteHeader.text = "Suggested Friends"
            else if (!item.status.equals(InviteActivity.INVITE_STATUS_UNREGISTERED)
            ) {
                holder.txtInviteHeader.text =
                    "People on " + activity.resources.getString(R.string.app_name)
            } else holder.txtInviteHeader.text =
                "Invite to " + activity.resources.getString(R.string.app_name)
        } else if (adapter.items.get(position - 1).status != InviteActivity.INVITE_STATUS_UNREGISTERED
            && (item.status == InviteActivity.INVITE_STATUS_UNREGISTERED)
        ) {
            holder.llPeopleonflatshare.visibility = View.VISIBLE
            holder.txtInviteHeader.text =
                "Invite to " + activity.resources.getString(R.string.app_name)
        } else holder.llPeopleonflatshare.visibility = View.GONE

        holder.btnInvite.visibility = View.GONE
        holder.btnInvited.visibility = View.GONE
        holder.btnAdded.visibility = View.GONE
        holder.btnAdd.visibility = View.GONE

        // Image
        holder.imgInvite.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_user))

        // Bottom space
        holder.spaceBottom.visibility =
            if (position == adapter.itemCount - 1) View.VISIBLE else View.GONE


        if (item.name?.firstName.isNullOrBlank())
            holder.txtInviteName.visibility = View.GONE
        else {
            holder.txtInviteName.visibility = View.VISIBLE
            holder.txtInviteName.text = item.name?.firstName + " " + item.name?.lastName
        }

        if (item.id.isEmpty())
            holder.txtInviteNumber.setVisibility(View.GONE)
        else {
            holder.txtInviteNumber.setVisibility(View.VISIBLE)
            if (item.status.equals(InviteActivity.INVITE_STATUS_UNREGISTERED)
                || item.status.equals(InviteActivity.INVITE_STATUS_APP_INVITED)
            ) {
                if (item.id.length == 10)
                    holder.txtInviteNumber.setText(
                        item.id.substring(0, 4) + "XXXX" + item.id.substring(
                            8
                        )
                    ) else holder.txtInviteNumber.setText(item.id)
            } else
                holder.txtInviteNumber.setText(item.id)
        }

        if (!item.status.equals(InviteActivity.INVITE_STATUS_UNREGISTERED)) {
            ImageHelper.loadImage(
                activity,
                R.drawable.ic_user,
                holder.imgInvite,ImageHelper.getProfileImageWithAws(item)
            )
            if (item.status.equals(InviteActivity.INVITE_STATUS_FRIEND_INVITED)) {
                holder.btnInvited.visibility = View.VISIBLE
                holder.btnInvited.text = "Added"
            } else if (item.status.equals(InviteActivity.INVITE_STATUS_APP_INVITED)) {
                holder.btnInvited.visibility = View.VISIBLE
                holder.btnInvited.text = "Invited"
            } else if (item.status.equals(InviteActivity.INVITE_STATUS_FRIENDS)) {
                holder.btnAdded.visibility = View.VISIBLE
            } else holder.btnAdd.visibility = View.VISIBLE
        } else {
            holder.btnInvite.visibility = View.VISIBLE
        }
        click()
    }

    private fun click() {
        holder.btnInvite.setOnClickListener {
            activity.apiController.inviteUser(item, position)
        }
        holder.btnAdd.setOnClickListener {
            activity.apiController.addFriends(item, position)
        }
    }
}