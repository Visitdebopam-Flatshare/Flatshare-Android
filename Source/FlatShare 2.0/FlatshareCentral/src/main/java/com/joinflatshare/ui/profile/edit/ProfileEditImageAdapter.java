package com.joinflatshare.ui.profile.edit;

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
import com.google.android.material.card.MaterialCardView;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.customviews.bottomsheet.BottomSheetView;
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.permission.PermissionUtil;

import java.util.ArrayList;

public class ProfileEditImageAdapter extends RecyclerView.Adapter<ProfileEditImageAdapter.ViewHolder> {
    private ArrayList<String> items = new ArrayList<>();
    private final BaseActivity activity;
    private int width, height;
    private BottomSheetView bottomSheetView;

    private OnitemClick onitemClick;

    public ProfileEditImageAdapter(BaseActivity activity) {
        this.activity = activity;
        init();
    }

    public ProfileEditImageAdapter(BaseActivity activity, OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
        this.activity = activity;
        init();
    }

    private void init() {
        if (activity instanceof ProfileEditActivity)
            prepareBottomMenu();
        width = (CommonMethods.getScreenWidth() / 2) - 30;
        height = (width / 3) * 4;
    }

    private void prepareBottomMenu() {
        ArrayList<ModelBottomSheet> menu = new ArrayList<>();
        menu.add(new ModelBottomSheet(R.drawable.ic_cross_red, "Remove Photo"));
        bottomSheetView = new BottomSheetView(activity, menu);
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

        String item = items.get(holder.getBindingAdapterPosition());
        holder.img_profile_banner1.setImageResource(0);
        holder.img_profile_add1.setVisibility(View.VISIBLE);
        if (!item.isEmpty()) {
            holder.img_profile_add1.setVisibility(View.GONE);
            if (item.startsWith("Images/") && items.size() > holder.getBindingAdapterPosition())
                loadImage(holder.getBindingAdapterPosition(), ImageHelper.getUserImagesWithAws(item), holder);
            else holder.img_profile_banner1.setImageURI(Uri.parse(item));
        }

        holder.frame_image.setOnClickListener(v -> {
            if (activity instanceof ProfileEditActivity) {
                final ProfileEditActivity act = (ProfileEditActivity) activity;
                CommonMethod.INSTANCE.makeLog("Position", "" + holder.getBindingAdapterPosition());
                if (!item.isEmpty()) {
                    bottomSheetView.show((view, position1) -> {
                        if (item.startsWith("Images/")) {
                            act.dataBind.deletedUserImages.add(item);
                        }
                        act.dataBind.addedUserImages.remove(act.dataBind.adapterUserImages.get(holder.getBindingAdapterPosition()));
                        act.dataBind.adapterUserImages.remove(holder.getBindingAdapterPosition());
                        if (act.dataBind.adapterUserImages.size() < 10 && !act.dataBind.adapterUserImages.get(0).isEmpty())
                            act.dataBind.adapterUserImages.add(0, "");
                        notifyDataSetChanged();
                        act.dataBind.setCompleteCount();
                    });
                } else {
                    act.imageClickPosition = holder.getBindingAdapterPosition();
                    PermissionUtil.INSTANCE.validatePermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, granted -> {
                        if (granted)
                            act.pickImage();
                        else CommonMethod.INSTANCE.makeToast("Permission not provided");
                    });
                }
            } else {
                if (onitemClick != null) {
                    onitemClick.onitemclick(holder.itemView, holder.getBindingAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_profile_banner1, img_profile_add1;
        MaterialCardView frame_image;

        ViewHolder(View itemView) {
            super(itemView);
            img_profile_banner1 = itemView.findViewById(R.id.img_profile_banner1);
            img_profile_add1 = itemView.findViewById(R.id.img_profile_add1);
            frame_image = itemView.findViewById(R.id.frame_image);
        }
    }

    private void loadImage(int position, String url, ViewHolder holder) {
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
                        CommonMethod.INSTANCE.makeLog("Image URL", url);
                        holder.img_profile_banner1.setImageDrawable(resource);
                        holder.img_profile_add1.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(holder.img_profile_banner1);
    }
}
