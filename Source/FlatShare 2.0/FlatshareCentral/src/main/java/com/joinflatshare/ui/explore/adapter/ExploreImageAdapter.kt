package com.joinflatshare.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreImagesBinding
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.helper.ImageHelper

class ExploreImageAdapter(
    private val activity: BaseActivity,
    private val images: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var width = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemExploreImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserImageHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val imageHolder = mainHolder as UserImageHolder
        imageHolder.bind(this, mainHolder.bindingAdapterPosition)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class UserImageHolder(
        private val activity: BaseActivity,
        private val holder: ItemExploreImagesBinding
    ) :
        RecyclerView.ViewHolder(holder.root) {
        fun bind(adapter: ExploreImageAdapter, position: Int) {
            if (position == 0 && adapter.width == 0) {
                holder.imgExplore.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        holder.imgExplore.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val height = holder.imgExplore.height
                        adapter.width = (height / 16) * 9
                        adapter.notifyDataSetChanged()
                    }
                })
            } else {
                holder.imgExplore.layoutParams =
                    FrameLayout.LayoutParams(adapter.width, FrameLayout.LayoutParams.MATCH_PARENT)
                val item = adapter.images[position]
                ImageHelper.loadImage(
                    activity,
                    0,
                    holder.imgExplore,
                    ImageHelper.getUserImagesWithAws(item)
                )
            }
        }
    }
}