package com.joinflatshare.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.checks.ChecksActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.holder.AdapterUserVpHolder

/**
 * Created by debopam on 15/11/22
 */
class ExploreUserVpAdapter(
    private val activity: BaseActivity,
    private val vpSlide: ArrayList<Int>,
    private val user: User
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemExploreVpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun getItemCount(): Int {
        return vpSlide.size
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        if (activity is ExploreActivity)
            holder.vpHolder.bindVp(activity, vpSlide[position], user)
        else if (activity is ChecksActivity)
            holder.vpHolder.bindVp(activity, user)
    }

    class ViewHolder(itemView: ViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val vpHolder: AdapterUserVpHolder = AdapterUserVpHolder(itemView)
    }
}