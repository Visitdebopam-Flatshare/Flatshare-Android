package com.joinflatshare.ui.flat.flatoptions.view_roomsize;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.amenities.AmenitiesItem;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;

import java.util.ArrayList;

;

public class RoomSizeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerView rv;
    private ArrayList<AmenitiesItem> items;
    private int viewType;
    private Activity act;

    public static final int VIEW_TYPE_SINGLE_SELECTION = 1;
    public static final int VIEW_TYPE_MULTIPLE_SELECTION = 2;

    public RoomSizeAdapter(Activity activity, RecyclerView rv, ArrayList<AmenitiesItem> items, int viewType) {
        this.rv = rv;
        this.act = activity;
        this.items = items;
        this.viewType = viewType;
    }

    public ArrayList<String> getRoomSize() {
        ArrayList<String> selectedItems = new ArrayList<>();
        for (AmenitiesItem item : items) {
            if (item.isSelected()) {
                selectedItems.add(item.getName());
            }
        }
        return selectedItems;
    }

    public String getRoomSizeValue() {
        if (items != null && items.size() > 0)
            for (AmenitiesItem item : items) {
                if (item.isSelected()) {
                    return item.getName();
                }
            }
        return "";
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        View itemView;
        if (viewType == VIEW_TYPE_MULTIPLE_SELECTION) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_roomtype, parent, false);
            return new ViewHolderMultiple(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_flat_type, parent, false);
            return new ViewHolderSingle(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {
        if (hold instanceof ViewHolderMultiple) {
            ViewHolderMultiple holder = (ViewHolderMultiple) hold;
            final AmenitiesItem item = items.get(position);
            holder.ckb_roomtype.setChecked(item.isSelected());
            holder.ckb_roomtype.setText(item.getName());
            holder.ckb_roomtype.setOnCheckedChangeListener((compoundButton, b) -> {
                rv.post(() -> {
                    boolean isChecked = !item.isSelected();
                    item.setSelected(isChecked);
                    items.set(position, item);
                    notifyItemChanged(position);
                });
            });
        } else if (hold instanceof ViewHolderSingle) {
            ViewHolderSingle holder = (ViewHolderSingle) hold;
            final AmenitiesItem item = items.get(position);
            holder.txt_amenity.setText(item.getName().trim());
            holder.card_amenity.setStrokeColor(ContextCompat.getColor(act, R.color.button_bg_black));
            if (item.isSelected()) {
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(act, R.color.button_bg_black));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(act, R.color.color_text_secondary));
            } else {
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(act, R.color.color_text_secondary));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(act, R.color.color_text_primary));
            }
            holder.card_amenity.invalidate();
            holder.card_amenity.setOnClickListener(view -> rv.post(() -> {
                boolean isChecked = !item.isSelected();
                item.setSelected(isChecked);
                items.set(position, item);
                notifyItemChanged(position);
                if (isChecked) {
                    for (int i = 0; i < items.size(); i++) {
                        if (i != position && items.get(i).isSelected()) {
                            items.get(i).setSelected(false);
                            notifyItemChanged(i);
                            if (act instanceof FlatEditActivity)
                                ((FlatEditActivity) act).dataBind.setCompleteCount();
                        }
                    }
                }
            }));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderSingle extends RecyclerView.ViewHolder {
        TextView txt_amenity;
        MaterialCardView card_amenity;

        ViewHolderSingle(View itemView) {
            super(itemView);
            card_amenity = itemView.findViewById(R.id.card_amenity);
            txt_amenity = itemView.findViewById(R.id.txt_amenity);
        }
    }

    class ViewHolderMultiple extends RecyclerView.ViewHolder {
        CheckBox ckb_roomtype;

        ViewHolderMultiple(View itemView) {
            super(itemView);
            ckb_roomtype = itemView.findViewById(R.id.ckb_roomtype);
        }
    }
}
