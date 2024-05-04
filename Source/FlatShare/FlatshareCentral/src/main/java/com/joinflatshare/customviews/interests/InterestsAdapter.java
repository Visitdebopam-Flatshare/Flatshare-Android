package com.joinflatshare.customviews.interests;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.ThemeConstants;
import com.joinflatshare.constants.UrlConstants;
import com.joinflatshare.ui.base.ApplicationBaseActivity;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.system.ThemeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {
    private ApplicationBaseActivity activity;
    private RecyclerView recyclerView;
    private InterestsView interestsView;
    private DialogInterests dialogInterests;
    private ArrayList<String> allItems;
    private final ArrayList<String> matchedItems;
    private final String viewType;
    private String currentThemeUrl, alternateThemeUrl, imageUrl;

    public InterestsAdapter(@NotNull ApplicationBaseActivity activity, @NotNull InterestsView interestsView) {
        this.viewType = interestsView.getViewType();
        this.interestsView = interestsView;
        this.matchedItems = interestsView.getMatchedContent();
        init(activity, interestsView.recyclerView, interestsView.getContent());
    }

    public InterestsAdapter(@NotNull ApplicationBaseActivity activity, @NotNull DialogInterests dialogInterests,
                            @NotNull RecyclerView rvInterests, @NotNull String viewType,
                            @NotNull ArrayList<String> adapterItems, @NotNull ArrayList<String> matchedItems) {
        this.viewType = viewType;
        this.dialogInterests = dialogInterests;
        this.matchedItems = matchedItems;
        init(activity, rvInterests, adapterItems);

    }

    private void init(@NotNull ApplicationBaseActivity activity, @NotNull RecyclerView recyclerView, ArrayList<String> items) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.allItems = items;
        currentThemeUrl = ThemeUtils.Companion.getCurrentTheme(activity);
        alternateThemeUrl = ThemeUtils.Companion.isNowInLightMode(activity) ? ThemeConstants.THEME_DARK : ThemeConstants.THEME_LIGHT;
        imageUrl = viewType.equals(InterestsView.VIEW_TYPE_INTERESTS) ?
                UrlConstants.INSTANCE.getINTEREST_URL() : UrlConstants.INSTANCE.getLANGUAGE_URL();
        currentThemeUrl += "/";
        alternateThemeUrl += "/";
    }

    public String getInterests() {
//        Collections.sort(matchedItems);
        return TextUtils.join(", ", matchedItems);
    }

    public ArrayList<String> getInterestList() {
        return matchedItems;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_interests, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String item = allItems.get(position);
        if (dialogInterests != null) {
            holder.viewHorizontal.setVisibility(View.GONE);
            holder.viewVertical.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.card_amenity.setLayoutParams(params);
            params.gravity = Gravity.START;
            holder.ll_interest_holder2.setLayoutParams(params);
            holder.ll_interest_holder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        holder.txt_amenity.setText(item);
        if (interestsView != null) {
            if (matchedItems.contains(item)) {
                holder.card_amenity.setStrokeColor(ContextCompat.getColor(activity, R.color.blue));
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(activity, R.color.blue));
                ImageHelper.loadImage(activity, R.mipmap.ic_launcher, holder.img_language, imageUrl + ThemeConstants.THEME_BLUE + "/" + item + ".png");
            } else {
                holder.card_amenity.setStrokeColor(ContextCompat.getColor(activity, R.color.color_grey_icon));
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));
                ImageHelper.loadImage(activity, R.mipmap.ic_launcher, holder.img_language, imageUrl + currentThemeUrl + item + ".png");
            }
        } else {
            if (matchedItems.contains(item)) {
                holder.card_amenity.setStrokeColor(ContextCompat.getColor(activity, R.color.button_bg_black));
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_bg_black));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
                ImageHelper.loadImage(activity, R.mipmap.ic_launcher, holder.img_language, imageUrl + alternateThemeUrl + item + ".png");
            } else {
                holder.card_amenity.setStrokeColor(ContextCompat.getColor(activity, R.color.color_hint));
                holder.card_amenity.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.color_bg));
                holder.txt_amenity.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));
                ImageHelper.loadImage(activity, R.mipmap.ic_launcher, holder.img_language, imageUrl + currentThemeUrl + item + ".png");
            }
        }
        holder.card_amenity.invalidate();
        if (dialogInterests != null) {
            holder.card_amenity.setOnClickListener(view -> {
                recyclerView.post(() -> {
                    boolean currentSelection = matchedItems.contains(item);
                    if (currentSelection) {
                        matchedItems.remove(item);
                        notifyItemChanged(position);
                    } else {
                        int maxSize;
                        if (viewType == InterestsView.VIEW_TYPE_INTERESTS) {
                            maxSize = AppConstants.interestMaxCount;
                        } else maxSize = AppConstants.languageMaxCount;
                        if (matchedItems.size() < maxSize) {
                            matchedItems.add(item);
                            notifyItemChanged(position);
                        }
                    }
                    if (dialogInterests != null) {
                        dialogInterests.getViewBind().txtHeaderCount.setText("" + matchedItems.size());

                    }
                });
            });
        }

    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_amenity;
        MaterialCardView card_amenity;
        ImageView img_language;
        View viewHorizontal, viewVertical;
        LinearLayout ll_interest_holder, ll_interest_holder2;

        ViewHolder(View itemView) {
            super(itemView);
            ll_interest_holder = itemView.findViewById(R.id.ll_interest_holder);
            ll_interest_holder2 = itemView.findViewById(R.id.ll_interest_holder2);
            img_language = itemView.findViewById(R.id.img_language);
            viewHorizontal = itemView.findViewById(R.id.view_horizontal);
            viewVertical = itemView.findViewById(R.id.view_vertical);
            card_amenity = itemView.findViewById(R.id.card_amenity);
            txt_amenity = itemView.findViewById(R.id.txt_amenity);
        }
    }
}
