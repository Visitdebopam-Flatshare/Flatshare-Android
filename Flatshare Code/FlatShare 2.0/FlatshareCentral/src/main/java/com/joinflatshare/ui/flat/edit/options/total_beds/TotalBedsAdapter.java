package com.joinflatshare.ui.flat.edit.options.total_beds;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.amenities.AmenitiesItem;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;

import java.util.ArrayList;

;

public class TotalBedsAdapter extends RecyclerView.Adapter<TotalBedsAdapter.ViewHolder> {
    private final RecyclerView rv;
    private final Activity activity;
    private final ArrayList<AmenitiesItem> items;
    int lastCheckedPosition = -1;

    TotalBedsAdapter(Activity activity, RecyclerView rv, ArrayList<AmenitiesItem> items) {
        this.rv = rv;
        this.activity = activity;
        this.items = items;
    }

    public String getBeds() {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_beds, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AmenitiesItem item = items.get(holder.getAdapterPosition());
        holder.txt_beds.setText(item.getName());
        holder.rdb_beds.setImageResource(item.isSelected() ? R.drawable.ic_rdb_selected : R.drawable.ic_rdb);
        if (item.isSelected())
            lastCheckedPosition = holder.getBindingAdapterPosition();
        holder.rl_beds.setOnClickListener(view -> rv.post(() -> {
            rv.post(() -> {
                if (lastCheckedPosition > -1) {
                    items.get(lastCheckedPosition).setSelected(false);
                }
                items.get(holder.getBindingAdapterPosition()).setSelected(true);
                lastCheckedPosition = holder.getBindingAdapterPosition();
                rv.post(() -> notifyDataSetChanged());
                if (activity instanceof FlatEditActivity) {
                    ((FlatEditActivity) activity).dataBind.setCompleteCount();
                }
            });
        }));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rdb_beds;
        TextView txt_beds;
        RelativeLayout rl_beds;

        ViewHolder(View itemView) {
            super(itemView);
            rl_beds = itemView.findViewById(R.id.rl_beds);
            txt_beds = itemView.findViewById(R.id.txt_beds);
            rdb_beds = itemView.findViewById(R.id.rdb_beds);
        }
    }
}
