package com.joinflatshare.ui.flat.flatoptions.view_amenity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.amenities.AmenitiesItem;

import java.util.ArrayList;

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ViewHolder> {
    private RecyclerView rv;
    private ArrayList<AmenitiesItem> items;

    public AmenitiesAdapter(RecyclerView rv, ArrayList<AmenitiesItem> items) {
        this.rv = rv;
        this.items = items;
    }

    public ArrayList<String> getSelectedAmenities() {
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roomtype, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AmenitiesItem item = items.get(position);
        holder.ckb_roomtype.setChecked(item.isSelected());
        holder.ckb_roomtype.setText(item.getName());
        holder.ckb_roomtype.setOnCheckedChangeListener((compoundButton, b) -> {
            rv.post(() -> {
                item.setSelected(!item.isSelected());
                holder.ckb_roomtype.setChecked(item.isSelected());
                items.set(position, item);
            });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox ckb_roomtype;

        ViewHolder(View itemView) {
            super(itemView);
            ckb_roomtype = itemView.findViewById(R.id.ckb_roomtype);
        }
    }
}
