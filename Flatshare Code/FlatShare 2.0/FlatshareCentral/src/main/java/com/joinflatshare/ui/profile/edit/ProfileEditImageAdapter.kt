package com.joinflatshare.ui.profile.edit

import android.Manifest
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemProfileImageBinding
import com.joinflatshare.customviews.bottomsheet.BottomSheetView
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.utils.helper.CommonMethod.makeLog
import com.joinflatshare.utils.helper.CommonMethod.makeToast
import com.joinflatshare.utils.helper.ImageHelper
import com.joinflatshare.utils.permission.PermissionUtil

class ProfileEditImageAdapter(
    private val activity: ProfileEditActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var height = 0
    private val items = ArrayList<String>()

    fun setItems(items: ArrayList<String>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemProfileImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.adapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val view: ItemProfileImageBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: ProfileEditImageAdapter
        ) {
            if (position == 0 && adapter.height == 0) {
                view.frameImage.viewTreeObserver.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        view.frameImage.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val width = view.frameImage.width
                        adapter.height = (width / 3) * 4
                        adapter.notifyDataSetChanged()
                    }
                })
            } else {
                view.frameImage.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    adapter.height
                )
                view.imgProfileAdd1.visibility = View.VISIBLE
                view.imgProfileBanner1.setImageResource(0)

                val item = adapter.items[position]
                if (item.isNotEmpty()) {
                    view.imgProfileAdd1.setVisibility(View.GONE)
                    if (item.startsWith("Images/"))
                        loadImage(
                            adapter,
                            position, ImageHelper.getUserImagesWithAws(item), view
                        )
                    else view.imgProfileBanner1.setImageURI(Uri.parse(item))
                }
                view.frameImage.setOnClickListener { _: View? ->
                    makeLog("Position", "" + position)
                    if (item.isNotEmpty()) {
                        val menu = ArrayList<ModelBottomSheet>()
                        menu.add(ModelBottomSheet(R.drawable.ic_cross_red, "Remove Photo"))
                        BottomSheetView(
                            adapter.activity, menu
                        ) { _, position ->
                            if (item.startsWith("Images/")) {
                                adapter.activity.dataBind.deletedUserImages.add(item)
                            }
                            adapter.activity.dataBind.addedUserImages.remove(
                                adapter.activity.dataBind.adapterUserImages[position]
                            )
                            adapter.activity.dataBind.adapterUserImages.removeAt(position)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        adapter.activity.imageClickPosition = position
                        PermissionUtil.validatePermission(
                            adapter.activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) { granted: Boolean ->
                            if (granted) adapter.activity.pickImage()
                            else makeToast("Permission not provided")
                        }
                    }
                    /*else {
                       val stfalconViewer: StfalconImageViewer<String> =
                           StfalconImageViewer.Builder(
                               adapter.activity, adapter.items
                           ) { imageView: ImageView?, imageUrl: String? ->
                               ImageHelper.loadImage(
                                   adapter.activity,
                                   R.mipmap.ic_launcher,
                                   imageView,
                                   imageUrl
                               )
                           }.allowZooming(true).withBackgroundColor(
                               ContextCompat.getColor(
                                   adapter.activity,
                                   R.color.color_bg
                               )
                           ).withHiddenStatusBar(true).withStartPosition(position).build()
                       stfalconViewer.show()
                   }*/
                }
            }
        }


        private fun loadImage(
            adapter: ProfileEditImageAdapter,
            position: Int,
            url: String,
            holder: ItemProfileImageBinding
        ) {
            Glide.with(adapter.activity)
                .load(url)
                .apply(
                    RequestOptions()
                        .error(0)
                        .centerCrop()
                )
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.imgProfileBanner1.setImageResource(0)
                        holder.imgProfileAdd1.setVisibility(View.VISIBLE)
                        adapter.items[position] = ""
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        makeLog("Image URL", url)
                        holder.imgProfileBanner1.setImageDrawable(resource)
                        holder.imgProfileAdd1.setVisibility(View.GONE)
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imgProfileBanner1)
        }
    }


}