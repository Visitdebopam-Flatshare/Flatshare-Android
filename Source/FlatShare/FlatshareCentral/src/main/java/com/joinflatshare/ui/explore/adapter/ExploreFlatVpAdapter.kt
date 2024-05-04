package com.joinflatshare.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.explore.FlatRecommendationItem
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.holder.AdapterFlatVpHolder

/**
 * Created by debopam on 15/11/22
 */
class ExploreFlatVpAdapter(
    private val activity: BaseActivity,
    private val vpSlide: ArrayList<Int>,
    private val data: FlatRecommendationItem,
    private val flat: MyFlatData,
    private val callback: OnUiEventClick
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
        holder.vpHolder.bindVp(activity, vpSlide[position], data, flat, callback)
    }

    class ViewHolder(itemView: ItemExploreVpBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val vpHolder: AdapterFlatVpHolder = AdapterFlatVpHolder(itemView)
    }
}