package com.joinflatshare.ui.flat.flatoptions.view_roomtype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.amenities.AmenitiesItem;

import java.util.ArrayList;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {
    private RecyclerView rv;
    private ArrayList<AmenitiesItem> items;
    int lastCheckedPosition = -1;

    RoomTypeAdapter(RecyclerView rv, ArrayList<AmenitiesItem> items) {
        this.rv = rv;
        this.items = items;
    }

    public String getRoomType() {
        for (AmenitiesItem item : items) {
            if (item.isSelected()) {
                return item.getName();
            }
        }
        return "";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_roomsize, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AmenitiesItem item = items.get(holder.getAdapterPosition());
        holder.rdb_roomsize.setText(item.getName());
        holder.rdb_roomsize.setChecked(item.isSelected());
        if (item.isSelected())
            lastCheckedPosition = holder.getAdapterPosition();
        holder.rdb_roomsize.setOnCheckedChangeListener((compoundButton, b) -> {
            rv.post(() -> {
                if (lastCheckedPosition > -1) {
                    items.get(lastCheckedPosition).setSelected(false);
                    notifyItemChanged(lastCheckedPosition);
                }
                items.get(holder.getAdapterPosition()).setSelected(true);
                lastCheckedPosition = holder.getAdapterPosition();
                notifyItemChanged(lastCheckedPosition);
            });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rdb_roomsize;

        ViewHolder(View itemView) {
            super(itemView);
            rdb_roomsize = itemView.findViewById(R.id.rdb_roomsize);
        }
    }
}
