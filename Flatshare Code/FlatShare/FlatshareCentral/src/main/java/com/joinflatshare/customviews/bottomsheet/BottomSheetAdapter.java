package com.joinflatshare.customviews.bottomsheet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.utils.helper.ImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {
    private final Activity activity;
    private final ArrayList<ModelBottomSheet> items;
    private OnitemClick onitemClick;

    BottomSheetAdapter(Activity activity, ArrayList<ModelBottomSheet> items) {
        this.activity = activity;
        this.items = items;
    }

    public void setClickListener(OnitemClick onitemClick) {
        this.onitemClick = onitemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bottomsheet, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelBottomSheet item = items.get(position);
        if (item.getName() == null)
            return;
        if (item.getIcon() == 0)
            holder.img_bottomsheet.setVisibility(View.GONE);
        else {
            holder.img_bottomsheet.setVisibility(View.VISIBLE);
            if (item.getType() == 1) {
                holder.img_bottom_rounded.setVisibility(View.VISIBLE);
                holder.img_bottomsheet.setVisibility(View.GONE);
                if (item.getName().contains("_")) {
                    String url = item.getName().substring(item.getName().lastIndexOf("_") + 1);
                    ImageHelper.loadImage(activity, item.icon, holder.img_bottom_rounded, url);
                }
            } else {
                holder.img_bottom_rounded.setVisibility(View.GONE);
                holder.img_bottomsheet.setVisibility(View.VISIBLE);
                holder.img_bottomsheet.setImageResource(item.getIcon());
            }
        }
        if (item.getType() == 1 && item.getName().contains("_"))
            holder.txt_bottomsheet.setText(item.getName().substring(0, item.getName().indexOf("_")));
        else
            holder.txt_bottomsheet.setText(item.getName());
        if (item.getType() == 3)
            holder.txt_bottomsheet.setTextColor(ContextCompat.getColor(activity, R.color.red));
        else
            holder.txt_bottomsheet.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));

        if (item.getCount() > 0) {
            holder.llBottomCountHolder.setVisibility(View.VISIBLE);
            holder.txtBottomCount.setText("" + item.getCount());
        } else holder.llBottomCountHolder.setVisibility(View.GONE);

        if (holder.getBindingAdapterPosition() == (items.size() - 1)) {
            holder.view_botton_line.setVisibility(View.GONE);
        } else holder.view_botton_line.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(view -> {
            onitemClick.onitemclick(holder.itemView, position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_bottomsheet;
        TextView txt_bottomsheet, txtBottomCount;
        LinearLayout ll_bottomsheet, llBottomCountHolder;
        View view_botton_line;
        RoundedImageView img_bottom_rounded;

        ViewHolder(View itemView) {
            super(itemView);
            view_botton_line = itemView.findViewById(R.id.view_botton_line);
            img_bottom_rounded = itemView.findViewById(R.id.img_bottom_rounded);
            ll_bottomsheet = itemView.findViewById(R.id.ll_bottomsheet);
            img_bottomsheet = itemView.findViewById(R.id.img_bottomsheet);
            txt_bottomsheet = itemView.findViewById(R.id.txt_bottomsheet);
            txtBottomCount = itemView.findViewById(R.id.txt_bottom_count);
            llBottomCountHolder = itemView.findViewById(R.id.ll_bottom_count_holder);
        }
    }
}
