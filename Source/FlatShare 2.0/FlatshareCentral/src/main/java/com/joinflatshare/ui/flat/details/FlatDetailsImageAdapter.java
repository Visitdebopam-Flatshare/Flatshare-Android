package com.joinflatshare.ui.flat.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;

import java.util.ArrayList;

public class FlatDetailsImageAdapter extends RecyclerView.Adapter<FlatDetailsImageAdapter.ViewHolder> {
    private final BaseActivity activity;
    private final int width;
    private final int height;
    private final ArrayList<String> items;

    public FlatDetailsImageAdapter(BaseActivity activity, ArrayList<String> items) {
        this.activity = activity;
        this.items = items;
        width = (CommonMethods.getScreenWidth() * 60) / 100;
        height = (width / 4) * 3;
    }

    public FlatDetailsImageAdapter(BaseActivity activity, ArrayList<String> items, OnitemClick onitemClick) {
        this.activity = activity;
        this.items = items;
        width = (CommonMethods.getScreenWidth() * 60) / 100;
        height = (width / 4) * 3;
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
        holder.img_profile_add1.setVisibility(View.GONE);
        String item = items.get(position);
        ImageHelper.loadImageWithCacheClear(
                activity, R.drawable.ic_flat_house, holder.img_profile_banner1,
                ImageHelper.getFlatImagesWithAws(item)

        );
        holder.frame_image.setOnClickListener(v -> {
            ArrayList<String> images = new ArrayList<>();
            for (String url : items)
                images.add(ImageHelper.getFlatImagesWithAws(url));
            ImageHelper.showImageSlider(activity, images, holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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
}
