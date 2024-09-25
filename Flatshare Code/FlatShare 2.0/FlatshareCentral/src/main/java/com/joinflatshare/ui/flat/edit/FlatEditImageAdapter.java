package com.joinflatshare.ui.flat.edit;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.customviews.bottomsheet.BottomSheetView;
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.permission.PermissionUtil;
import com.joinflatshare.utils.touchitemhelper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class FlatEditImageAdapter extends RecyclerView.Adapter<FlatEditImageAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, OnitemClick {
    private ArrayList<String> items = new ArrayList<>();
    private final FlatEditActivity activity;
    private final int width, height;
    private BottomSheetView bottomSheetView;

    public FlatEditImageAdapter(FlatEditActivity activity) {
        this.activity = activity;
        width = (CommonMethods.getScreenWidth() * 60) / 100;
        height = (width / 4) * 3;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public ArrayList<String> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        holder.img_profile_banner1.setLayoutParams(params);

        holder.img_profile_banner1.setImageResource(0);
        holder.img_profile_add1.setVisibility(View.VISIBLE);

        String item = items.get(position);
        holder.img_profile_banner1.setImageResource(0);
        holder.img_profile_add1.setVisibility(View.VISIBLE);
        if (!item.isEmpty()) {
            holder.img_profile_add1.setVisibility(View.GONE);
            if (item.startsWith("Images/") && items.size() > holder.getAdapterPosition())
                loadImage(position, ImageHelper.getFlatImagesWithAws(item), holder);
            else holder.img_profile_banner1.setImageURI(Uri.parse(item));
        }

        holder.frame_image.setOnClickListener(v -> {
            CommonMethod.INSTANCE.makeLog("Position", "" + holder.getAdapterPosition());
            if (!item.isEmpty()) {
                ArrayList<ModelBottomSheet> menu = new ArrayList<>();
                menu.add(new ModelBottomSheet(R.drawable.ic_cross_red, "Remove Photo"));
                bottomSheetView = new BottomSheetView(activity, menu, new OnitemClick() {
                    @Override
                    public void onitemclick(View view, int position) {
                        if (item.startsWith("Images/")) {
                            activity.dataBind.deletedUserImages.add(item);
                        }
                        activity.dataBind.addedUserImages.remove(activity.dataBind.adapterUserImages.get(holder.getAdapterPosition()));
                        activity.dataBind.adapterUserImages.remove(holder.getAdapterPosition());
                        if (activity.dataBind.adapterUserImages.size() < 10 && !activity.dataBind.adapterUserImages.get(0).isEmpty())
                            activity.dataBind.adapterUserImages.add(0, "");
                        notifyDataSetChanged();
                        activity.dataBind.setCompleteCount();
                    }
                });
            } else {
                activity.imageClickPosition = holder.getAdapterPosition();
                PermissionUtil.INSTANCE.validatePermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, granted -> {
                    if (granted)
                        activity.pickImage();
                    else CommonMethod.INSTANCE.makeToast("Permission not provided");
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onitemclick(View view, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_profile_banner1, img_profile_add1;
        FrameLayout frame_image;

        ViewHolder(View itemView) {
            super(itemView);
            img_profile_banner1 = itemView.findViewById(R.id.img_profile_banner1);
            img_profile_add1 = itemView.findViewById(R.id.img_profile_add1);
            frame_image = itemView.findViewById(R.id.frame_image);
        }
    }

    private void loadImage(int position, String url, FlatEditImageAdapter.ViewHolder holder) {
        Glide.with(activity)
                .load(url)
                .apply(
                        new RequestOptions()
                                .error(0)
                                .centerCrop()
                )
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.img_profile_banner1.setImageResource(0);
                        holder.img_profile_add1.setVisibility(View.VISIBLE);
                        items.set(position, "");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.img_profile_banner1.setImageDrawable(resource);
                        holder.img_profile_add1.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(holder.img_profile_banner1);
    }
}
