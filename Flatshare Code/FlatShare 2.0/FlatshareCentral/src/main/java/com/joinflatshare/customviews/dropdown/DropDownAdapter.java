package com.joinflatshare.customviews.dropdown;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;

import java.util.ArrayList;

class DropDownAdapter extends RecyclerView.Adapter<DropDownAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<String> items;
    private OnitemClick onitemClickListner;

    DropDownAdapter(Activity activity, ArrayList<String> items,
                    OnitemClick onitemClickListner) {
        this.activity = activity;
        this.items = items;
        this.onitemClickListner = onitemClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_popup, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_dropdown.setText(items.get(position));
        if (onitemClickListner != null) {
            holder.tv_dropdown.setOnClickListener(view1 ->
                    onitemClickListner.onitemclick(holder.tv_dropdown, position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_dropdown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_dropdown = itemView.findViewById(R.id.tv_dropdown);
        }
    }
}
