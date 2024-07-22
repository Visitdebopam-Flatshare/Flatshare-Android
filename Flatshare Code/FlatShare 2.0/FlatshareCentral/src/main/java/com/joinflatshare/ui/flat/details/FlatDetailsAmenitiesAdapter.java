package com.joinflatshare.ui.flat.details;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.ThemeConstants;
import com.joinflatshare.constants.UrlConstants;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.system.ThemeUtils;

import java.util.List;

public class FlatDetailsAmenitiesAdapter extends RecyclerView.Adapter<FlatDetailsAmenitiesAdapter.ViewHolder> {
    private final FlatDetailsActivity activity;
    private final List<String> items;
    private String currentThemeUrl;

    public FlatDetailsAmenitiesAdapter(FlatDetailsActivity activity, List<String> items) {
        this.activity = activity;
        this.items = items;
        currentThemeUrl = ThemeUtils.Companion.getTheme(activity) == AppCompatDelegate.MODE_NIGHT_NO ? ThemeConstants.THEME_LIGHT : ThemeConstants.THEME_DARK;
        currentThemeUrl += "/";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flat_amenities, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        ImageHelper.loadImage(activity, 0, holder.img_flat_amenity, UrlConstants.INSTANCE.getAMENITY_URL() + currentThemeUrl + item + ".png");
        holder.txt_flat_amenity.setText(item);
        holder.frame_amenities.setOnClickListener(view -> {
            holder.txt_flat_amenity.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Animation expandOut = AnimationUtils.loadAnimation(activity, R.anim.bubble_out);
                holder.txt_flat_amenity.startAnimation(expandOut);
                new Handler().postDelayed(() -> holder.txt_flat_amenity.setVisibility(View.INVISIBLE), 400);
            }, 2000);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_flat_amenity;
        TextView txt_flat_amenity;
        FrameLayout frame_amenities;

        ViewHolder(View itemView) {
            super(itemView);
            img_flat_amenity = itemView.findViewById(R.id.img_flat_amenity);
            txt_flat_amenity = itemView.findViewById(R.id.txt_flat_amenity);
            frame_amenities = itemView.findViewById(R.id.frame_amenities);
        }
    }
}
