package com.joinflatshare.ui.friends

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.debopam.flatshareprogress.DialogCustomProgress
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemFriendListBinding
import com.joinflatshare.chat.SendBirdUserChannel
import com.joinflatshare.constants.SendBirdConstants
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.chat.details.ChatDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.ImageHelper

class FriendListAdapter(
    private val activity: FriendListActivity,
    private val items: List<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var width = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val activity: FriendListActivity,
        private val view: ItemFriendListBinding
    ) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int,
            adapter: FriendListAdapter
        ) {
            if (adapter.width == 0 && position == 0) {
                view.imgFriend.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        view.imgFriend.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        adapter.width = view.imgFriend.width - 30
                        adapter.notifyDataSetChanged()
                    }

                })
            } else {
                view.imgFriend.layoutParams.width = adapter.width
                view.imgFriend.layoutParams.height = adapter.width
                view.imgFriend.requestLayout()
            }

            val item = adapter.items[position]
            ImageHelper.loadImage(
                activity, R.drawable.ic_user, view.imgFriend,
                ImageHelper.getProfileImageWithAws(item)
            )
            view.txtFriendName.text = item.name?.firstName
            view.imgFriend.setOnClickListener {
                activity.apiManager.showProgress()
                val channel = SendBirdUserChannel(activity)
                channel.joinUserChannel(
                    item.id,
                    SendBirdConstants.CHANNEL_TYPE_FRIEND
                ) { text ->
                    DialogCustomProgress.hideProgress(activity);
                    if (!text.equals("0")) {
                        // Channel exist
                        val intent = Intent(activity, ChatDetailsActivity::class.java)
                        intent.putExtra("channel", text)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }
        }
    }
}