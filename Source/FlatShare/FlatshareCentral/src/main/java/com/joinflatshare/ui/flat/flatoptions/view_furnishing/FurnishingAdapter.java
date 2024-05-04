package com.joinflatshare.ui.flat.flatoptions.view_furnishing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ItemFlatTypeBinding;
import com.joinflatshare.pojo.amenities.AmenitiesItem;
import com.joinflatshare.ui.base.BaseActivity;

import java.util.ArrayList;

public class FurnishingAdapter extends RecyclerView.Adapter<FurnishingAdapter.ViewHolder> {
    private final RecyclerView rv;
    private final ArrayList<AmenitiesItem> items;
    private final BaseActivity activity;
    private final boolean isSingleFurnishingSelection;

    public FurnishingAdapter(BaseActivity activity, RecyclerView rv, boolean isSingleFurnishingSelection, ArrayList<AmenitiesItem> items) {
        this.rv = rv;
        this.items = items;
        this.isSingleFurnishingSelection = isSingleFurnishingSelection;
        this.activity = activity;
    }

    public ArrayList<String> getSelectedFurnishes() {
        ArrayList<String> selectedItems = new ArrayList<>();
        for (AmenitiesItem item : items) {
            if (item.isSelected()) {
                selectedItems.add(item.getName());
            }
        }
        return selectedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFlatTypeBinding viewBind = ItemFlatTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(viewBind);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder baseHolder, int position) {
        final AmenitiesItem item = items.get(position);
        final ItemFlatTypeBinding holder=baseHolder.holder;
        holder.txtAmenity.setText(item.getName().trim());
        if (item.isSelected()) {
            holder.cardAmenity.setStrokeColor(ContextCompat.getColor(activity, R.color.button_bg_black));
            holder.cardAmenity.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_bg_black));
            holder.txtAmenity.setTextColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
        } else {
            holder.cardAmenity.setStrokeColor(ContextCompat.getColor(activity, R.color.color_icon));
            holder.cardAmenity.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.color_bg));
            holder.txtAmenity.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));
        }
        holder.cardAmenity.invalidate();
        holder.cardAmenity.setOnClickListener(view -> rv.post(() -> {
            boolean isChecked = !item.isSelected();
            item.setSelected(isChecked);
            items.set(position, item);
            notifyItemChanged(position);
            if (isChecked && isSingleFurnishingSelection) {
                for (int i = 0; i < items.size(); i++) {
                    if (i != position && items.get(i).isSelected()) {
                        items.get(i).setSelected(false);
                        notifyItemChanged(i);
                    }
                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemFlatTypeBinding holder;

        ViewHolder(ItemFlatTypeBinding itemView) {
            super(itemView.getRoot());
            this.holder = itemView;
        }
    }
}
