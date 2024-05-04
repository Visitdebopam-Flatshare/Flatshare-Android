package com.joinflatshare.ui.notifications;

import static com.joinflatshare.constants.ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_CHAT;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_CONNECTION_F2U;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_CONNECTION_FHT;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_CONNECTION_U2F;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_GENERAL_FLAT;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_GENERAL_USER;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_INVITE_FLAT_MEMBER;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_INVITE_FRIEND;
import static com.joinflatshare.constants.NotificationConstants.NOTIFICATION_TYPE_NO_NAVIGATION;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.pojo.notification.NotificationItem;
import com.joinflatshare.ui.chat.list.ChatListActivity;
import com.joinflatshare.ui.flat.details.FlatDetailsActivity;
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ContactsLookup;
import com.joinflatshare.utils.helper.DateUtils;
import com.joinflatshare.utils.helper.ImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final ArrayList<NotificationItem> items;
    private final NotificationActivity activity;
    private ContactsLookup contactsLookup;

    public NotificationAdapter(NotificationActivity activity, ArrayList<NotificationItem> items) {
        this.activity = activity;
        this.items = items;
        contactsLookup = new ContactsLookup(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = items.get(position);

        final String msg = item.getMsg();
        holder.ll_notification_buttons.setVisibility(View.GONE);
        // Image
        switch (item.getType()) {
            case NOTIFICATION_TYPE_INVITE_FLAT_MEMBER:
            case NOTIFICATION_TYPE_INVITE_FRIEND:
            case NOTIFICATION_TYPE_GENERAL_USER:
            case NOTIFICATION_TYPE_CONNECTION_U2F:
            case NOTIFICATION_TYPE_CONNECTION_FHT:
                ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_notification_dp,
                        ImageHelper.getProfileImageWithAws(item));
                break;
            case NOTIFICATION_TYPE_CONNECTION_F2U:
            case NOTIFICATION_TYPE_GENERAL_FLAT:
            case NOTIFICATION_TYPE_CHAT:
            case CHAT_REQUEST_CONSTANT_F2U:
                ImageHelper.loadImage(activity, R.drawable.ic_flat_bg, holder.img_notification_dp,
                        ImageHelper.getFlatDpWithAws(item.getFrom()));
                break;
            case NOTIFICATION_TYPE_NO_NAVIGATION:
            default:
                if (item.getFrom() != null && item.getFrom().length() == 10
                        && !item.getFrom().equals(AppConstants.loggedInUser.getId())) {
                    ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_notification_dp,
                            ImageHelper.getProfileImageWithAws(item));
                } else
                    holder.img_notification_dp.setImageResource(R.mipmap.ic_launcher);
        }

        // Message
        switch (item.getType()) {
            case NOTIFICATION_TYPE_INVITE_FLAT_MEMBER:
            case NOTIFICATION_TYPE_INVITE_FRIEND:
                boolean firstSpaceCrossed = false;
                int spacePosition = 0;
                String name = "";
                for (int i = 0; i < msg.length(); i++) {
                    if (msg.charAt(i) == ' ') {
                        if (!firstSpaceCrossed)
                            firstSpaceCrossed = true;
                        else {
                            spacePosition = i;
                            name = msg.substring(0, spacePosition);
                            break;
                        }
                    }
                }
                String end = msg.substring(spacePosition);
                String text = activity.getResources().getString
                        (R.string.notification_text_invite, name, end, DateUtils.INSTANCE.getPostTime(item.getCreatedAt()));
                holder.txt_notification.setText(HtmlCompat.fromHtml(text,
                        HtmlCompat.FROM_HTML_MODE_LEGACY));
                break;
            default:
                text = activity.getResources().getString
                        (R.string.notification_text_others, msg, DateUtils.INSTANCE.getPostTime(item.getCreatedAt()));
                holder.txt_notification.setText(HtmlCompat.fromHtml(text,
                        HtmlCompat.FROM_HTML_MODE_LEGACY));
                break;
        }

        // Click
        holder.ll_notification_holder.setOnClickListener(view -> {
            Intent intent = null;
            switch (item.getType()) {
                case NOTIFICATION_TYPE_INVITE_FLAT_MEMBER:
                case NOTIFICATION_TYPE_INVITE_FRIEND:
                case NOTIFICATION_TYPE_GENERAL_USER:
                    if (item.getFrom() != null && item.getFrom().length() == 10) {
                        intent = new Intent(activity, ProfileDetailsActivity.class);
                        intent.putExtra("phone", item.getFrom());
                    }
                    break;
                case NOTIFICATION_TYPE_CHAT:
                    intent = new Intent(activity, ChatListActivity.class);
                    intent.putExtra("phone", item.getFrom());
                    break;
                case NOTIFICATION_TYPE_GENERAL_FLAT:
                    if (item.getFrom() != null && item.getFrom().length() == 10) {
                        intent = new Intent(activity, FlatDetailsActivity.class);
                        intent.putExtra("phone", item.getFrom());
                    }
                    break;
            }
            if (intent != null)
                CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        });
        holder.view_line.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img_notification_dp;
        TextView txt_notification, txt_notification_date;
        LinearLayout ll_notification_buttons, ll_notification_holder, ll_notification_decline, ll_notification_accept;
        View view_line;

        ViewHolder(View itemView) {
            super(itemView);
            view_line = itemView.findViewById(R.id.view_line);
            ll_notification_holder = itemView.findViewById(R.id.ll_notification_holder);
            ll_notification_decline = itemView.findViewById(R.id.ll_notification_decline);
            txt_notification = itemView.findViewById(R.id.txt_notification);
            ll_notification_buttons = itemView.findViewById(R.id.ll_notification_buttons);
            img_notification_dp = itemView.findViewById(R.id.img_notification_dp);
            ll_notification_accept = itemView.findViewById(R.id.ll_notification_accept);
            txt_notification_date = itemView.findViewById(R.id.txt_notification_date);
        }
    }
}
