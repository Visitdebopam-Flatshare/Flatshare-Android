package com.joinflatshare.ui.chat.details.holder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData;
import com.joinflatshare.chat.metadata.PreviousMessageMetaData;
import com.joinflatshare.chat.utils.DateUtils;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.chat.details.ChatDetailsAdapter;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.message.BaseMessage;

import java.util.regex.Pattern;

;

public class HolderChatBase extends RecyclerView.ViewHolder {
    TextView txt_chat_date, txt_message_timestamp;
    RelativeLayout ll_message_chat;
    LinearLayout ll_chat_click;
    ImageView img_chat_seen_status;

    // Reply holder
    MaterialCardView card_chat_reply_holder;
    View view_chat_reply;
    TextView txt_chat_reply_sender, txt_chat_reply_message;
    ImageView img_chat_reply_photo;

    ChatDetailsActivity activity;
    ChatDetailsAdapter adapter;
    BaseMessage message, prevMessage;
    int position;
    boolean isNewDay = false;

    public HolderChatBase(@NonNull View itemView) {
        super(itemView);
        txt_chat_date = itemView.findViewById(R.id.txt_chat_date);
        txt_message_timestamp = itemView.findViewById(R.id.txt_message_timestamp);
        ll_message_chat = itemView.findViewById(R.id.ll_message_chat);
        ll_chat_click = itemView.findViewById(R.id.ll_chat_click);
        img_chat_seen_status = itemView.findViewById(R.id.img_chat_seen_status);

        // Reply
        card_chat_reply_holder = itemView.findViewById(R.id.card_chat_reply_holder);
        view_chat_reply = itemView.findViewById(R.id.view_chat_reply);
        txt_chat_reply_sender = itemView.findViewById(R.id.txt_chat_reply_sender);
        txt_chat_reply_message = itemView.findViewById(R.id.txt_chat_reply_message);
        img_chat_reply_photo = itemView.findViewById(R.id.img_chat_reply_photo);
    }

    public void init(ChatDetailsActivity activity,
                     ChatDetailsAdapter adapter, int position) {
        this.adapter = adapter;
        this.activity = activity;
        this.position = position;
        message = activity.messageList.get(position);
        bind();
        manageClicks();
        handleReply();
    }

    private void bind() {
        txt_chat_date.setVisibility(View.GONE);
        card_chat_reply_holder.setVisibility(View.GONE);
        txt_message_timestamp.setText(DateUtils.formatTime(message.getCreatedAt()));
        isNewDay = false;
        if (position == adapter.items.size() - 1)
            isNewDay = true;
        else {
            prevMessage = activity.messageList.get(position + 1);
            if (!DateUtils.hasSameDate(message.getCreatedAt(), prevMessage.getCreatedAt())) {
                isNewDay = true;
            }
        }

        // If this message has started on a new day
        if (isNewDay) {
            txt_chat_date.setVisibility(View.VISIBLE);
            txt_chat_date.setText(DateUtils.formatDate(message.getCreatedAt()));
        } else {
            txt_chat_date.setVisibility(View.GONE);
        }

        // Message selected
        if (adapter.selectedMessage == null)
            ll_message_chat.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        else {
            BaseMessage message = activity.messageList.get(position);
            if (adapter.selectedMessage.getMessageId() == message.getMessageId())
                ll_message_chat.setBackgroundColor(Color.parseColor("#332B8FF4"));
            else
                ll_message_chat.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        }
    }

    private void handleReply() {
        if (message.getParentMessageId() > 0) {
            // Reply to a message
            MessageMetaData messageData = new Gson().fromJson(message.getData(), MessageMetaData.class);
            if (messageData != null) {
                PreviousMessageMetaData parentMessage = messageData.getPreviousMessageMetaData();
                if (parentMessage != null) {
                    card_chat_reply_holder.setVisibility(View.VISIBLE);
                    // SENDER
                    if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        // Me
                        view_chat_reply.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
                        txt_chat_reply_message.setTextColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
                        txt_chat_reply_sender.setTextColor(ContextCompat.getColor(activity, R.color.color_blue_light));
                    } else {
                        // User
                        view_chat_reply.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
                        txt_chat_reply_message.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        txt_chat_reply_sender.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                    }
                    if (parentMessage.getSenderId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        // Parent message sent by me
                        txt_chat_reply_sender.setText("You");
                    } else {
                        // Parent message sent by user
                        activity.sendBirdApiManager.getUserDetails(parentMessage.getSenderId(), response -> {
                            txt_chat_reply_sender.setText(response.getNickname());
                        });
                    }
                    // MESSAGE
                    if (parentMessage.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_TEXT)) {
                        img_chat_reply_photo.setVisibility(View.GONE);
                        txt_chat_reply_message.setVisibility(View.VISIBLE);
                        if (parentMessage.getMessage().length() > 230)
                            parentMessage.setMessage(parentMessage.getMessage().substring(0, 227) + "...");
                        txt_chat_reply_message.setText(parentMessage.getMessage());
                    } else if (parentMessage.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_LOCATION)) {
                        img_chat_reply_photo.setVisibility(View.VISIBLE);
                        txt_chat_reply_message.setVisibility(View.GONE);
                        img_chat_reply_photo.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        img_chat_reply_photo.setImageResource(R.drawable.ic_location);
                    } else if (parentMessage.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_IMAGE)) {
                        img_chat_reply_photo.setVisibility(View.VISIBLE);
                        txt_chat_reply_message.setVisibility(View.GONE);
                        img_chat_reply_photo.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
                        ImageHelper.loadImage(activity, 0, img_chat_reply_photo, parentMessage.getMessage());
                    } else if (parentMessage.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_CONTACT)) {
                        img_chat_reply_photo.setVisibility(View.VISIBLE);
                        txt_chat_reply_message.setVisibility(View.GONE);
                        img_chat_reply_photo.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        img_chat_reply_photo.setImageResource(R.drawable.ic_user_grey);
                    }
                    card_chat_reply_holder.setOnClickListener(v -> {
                        long parentMessageId = parentMessage.getMessageId();
                        for (int i = position - 1; i >= 0; i--)
                            if (adapter.items.get(i).getMessageId() == parentMessageId) {
                                activity.viewBind.rvMessageList.smoothScrollToPosition(i);
                                break;
                            }
                    });
                }
            }

        }
    }

    private void manageClicks() {
        ll_chat_click.setOnClickListener(v -> {
            MessageMetaData messageData = new Gson().fromJson(message.getData(), MessageMetaData.class);
            if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_LOCATION)) {
                String strUri = "http://maps.google.com/maps?q=loc:" + messageData.getLocationLatitude()
                        + "," + messageData.getLocationLongitude();
                CommonMethod.INSTANCE.makeLog("Location", strUri);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                activity.startActivity(intent);
            } else if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_CONTACT)) {
                String splitData = message.getMessage();
                String name = splitData.split(Pattern.quote(","))[0];
                String number = splitData.split(Pattern.quote(","))[1];
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, number);
                activity.activityLauncher.launch(contactIntent);
            }
        });
        ll_chat_click.setOnLongClickListener(v -> {
            BaseMessage message = activity.messageList.get(position);
            if (adapter.selectedMessage != null && adapter.selectedMessage.getMessageId() == message.getMessageId())
                return false;

            adapter.selectedMessage = message;
            adapter.notifyDataSetChanged();
            // check whether message is from me
            if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                // My message
                activity.topbarHandler.showTopBarIconOne(R.drawable.ic_chat_reply);
                activity.topbarHandler.showTopBarIconTwo(R.drawable.ic_chat_copy);
                activity.topbarHandler.showTopBarIconThree(R.drawable.ic_chat_delete);
            } else {
                // User's message
                activity.topbarHandler.showTopBarIconOne(R.drawable.ic_chat_reply);
                activity.topbarHandler.showTopBarIconTwo(R.drawable.ic_chat_copy);
                activity.topbarHandler.showTopBarIconThree(0);
            }


            // Check whether user has already clicked on reply button
            if (activity.dataBind.replyViewBind.isVisible())
                activity.viewBind.includeChatTopbar.llChatTopbarIconone.performClick();
            return true;
        });
    }
}
