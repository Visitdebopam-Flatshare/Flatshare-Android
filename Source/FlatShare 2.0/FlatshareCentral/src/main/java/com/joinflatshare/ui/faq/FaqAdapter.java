package com.joinflatshare.ui.faq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.faq.FaqItem;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.util.ArrayList;

;

public class FaqAdapter extends BaseExpandableListAdapter {
    private final FaqActivity activity;
    private final ArrayList<FaqItem> faqItems;
    private int rowOpenPosition = -1;

    public FaqAdapter(FaqActivity activity, ArrayList<FaqItem> faqItems) {
        this.activity = activity;
        this.faqItems = faqItems;
    }

    @Override
    public int getGroupCount() {
        return faqItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return faqItems.get(groupPosition).getAnswers().size();
    }

    @Override
    public FaqItem getGroup(int groupPosition) {
        return faqItems.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return faqItems.get(groupPosition).getAnswers().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_faq, null);
        }
        TextView txt = convertView.findViewById(R.id.txt_amenity);
        txt.setText(getGroup(groupPosition).getQuestion());
        ImageView img = convertView.findViewById(R.id.img_faq_arrow);
        if (isExpanded) {
            MixpanelUtils.INSTANCE.onFAQOpened((groupPosition + 1));
            img.setImageResource(R.drawable.ic_minus);
        } else img.setImageResource(R.drawable.ic_plus);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_faq_child, null);
        }
        TextView txt = convertView.findViewById(R.id.txt_amenity);
        txt.setText(getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (rowOpenPosition > -1) {
            activity.collapseGroup(rowOpenPosition);
        }
        rowOpenPosition = groupPosition;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
