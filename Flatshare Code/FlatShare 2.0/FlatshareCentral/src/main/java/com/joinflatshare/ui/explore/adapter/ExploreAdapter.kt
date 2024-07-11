package com.joinflatshare.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.holder.AdapterUserHolder
import com.joinflatshare.ui.liked.LikedActivity

class ExploreAdapter(
    private val activity: BaseActivity, private val flat: MyFlatData?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        var size = 0
        if (activity is ExploreActivity) size = activity.userData.size
        else if (activity is LikedActivity) size = activity.users.size
        return size
    }

    class UserViewHolder(itemView: ItemExploreBinding) :
        RecyclerView.ViewHolder(itemView.root) {
    }
}