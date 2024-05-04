package com.joinflatshare.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreBinding
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.explore.holder.AdapterFlatHolder
import com.joinflatshare.ui.explore.holder.AdapterUserHolder
import com.joinflatshare.ui.liked.LikedActivity

class ExploreAdapter(
    private val activity: BaseActivity,
    private var SEARCH_TYPE: String,
    private val flat: MyFlatData?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VP_SLIDE_INTEREST = 1
        const val VP_SLIDE_DEAL_BREAKERS = 2
        const val VP_SLIDE_LANGUAGES = 3
        const val VP_SLIDE_ABOUT = 4
        const val VP_SLIDE_WORK = 5
        const val VP_SLIDE_NORMS = 6
        const val VP_SLIDE_FLATMATES = 7
        const val VP_SLIDE_FLATDETAILS = 8
        const val CHARACTER_LIMIT_IN_USER_NAME = 8
        const val CHARACTER_LIMIT_IN_FLAT_NAME = 8
        var shouldShowPaymentGateway = true
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemExploreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (SEARCH_TYPE == BaseActivity.TYPE_FLAT) FlatViewHolder(viewBind) else UserViewHolder(
            viewBind
        )
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        if (!shouldShowPaymentGateway) {
            val adapterPosition = mainHolder.absoluteAdapterPosition
            if (adapterPosition < (itemCount - 2))
                shouldShowPaymentGateway = true
        }
        if (SEARCH_TYPE == BaseActivity.TYPE_FLAT) {
            val flatHolder = mainHolder as FlatViewHolder
            flatHolder.flatHolder.bindFlat(
                activity,
                mainHolder.absoluteAdapterPosition
            )
        } else if (SEARCH_TYPE == BaseActivity.TYPE_USER
            || SEARCH_TYPE == BaseActivity.TYPE_FHT
            || SEARCH_TYPE == BaseActivity.TYPE_DATE
        ) {
            val userHolder = mainHolder as UserViewHolder
            userHolder.userHolder.bindUser(
                activity,
                mainHolder.absoluteAdapterPosition, flat
            )
        }
    }

    override fun getItemCount(): Int {
        var size = 0
        if (SEARCH_TYPE == BaseActivity.TYPE_FLAT) {
            if (activity is ExploreActivity)
                size = activity.flatData.size
            else if (activity is LikedActivity) {
                size = activity.flats.size
            }
        } else if (SEARCH_TYPE == BaseActivity.TYPE_USER
            || SEARCH_TYPE == BaseActivity.TYPE_FHT
            || SEARCH_TYPE == BaseActivity.TYPE_DATE
        ) {
            if (activity is ExploreActivity)
                size = activity.userData.size
            else if (activity is LikedActivity)
                size = activity.users.size
        }
        return size
    }

    class FlatViewHolder(itemView: ItemExploreBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val flatHolder: AdapterFlatHolder = AdapterFlatHolder(itemView)
    }

    class UserViewHolder(itemView: ItemExploreBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val userHolder: AdapterUserHolder = AdapterUserHolder(itemView)
    }
}