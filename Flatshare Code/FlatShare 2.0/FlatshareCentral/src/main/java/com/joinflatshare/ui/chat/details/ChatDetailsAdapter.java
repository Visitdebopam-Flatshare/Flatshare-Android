package com.joinflatshare.ui.chat.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.ui.chat.details.holder.HolderChatFileMe;
import com.joinflatshare.ui.chat.details.holder.HolderChatFileUser;
import com.joinflatshare.ui.chat.details.holder.HolderChatTextAdmin;
import com.joinflatshare.ui.chat.details.holder.HolderChatTextMe;
import com.joinflatshare.ui.chat.details.holder.HolderChatTextUser;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.message.AdminMessage;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.FileMessage;
import com.sendbird.android.message.UserMessage;

import java.util.List;

public class ChatDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<BaseMessage> items;
    private ChatDetailsActivity activity;
    private static final int VIEW_TYPE_USER_MESSAGE_ME = 10;
    private static final int VIEW_TYPE_USER_MESSAGE_USER = 11;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 20;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_USER = 21;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_ME = 30;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER = 31;
    private static final int VIEW_TYPE_FILE_MESSAGE_LOCATION_ME = 40;
    private static final int VIEW_TYPE_FILE_MESSAGE_LOCATION_OTHER = 41;
    private static final int VIEW_TYPE_FILE_MESSAGE_CONTACT_ME = 50;
    private static final int VIEW_TYPE_FILE_MESSAGE_CONTACT_USER = 51;
    private static final int VIEW_TYPE_ADMIN_MESSAGE = 60;
    public BaseMessage selectedMessage = null;

    public ChatDetailsAdapter(ChatDetailsActivity activity, List<BaseMessage> items) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_USER_MESSAGE_ME:
            case VIEW_TYPE_FILE_MESSAGE_CONTACT_ME:
                View myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_text_me, parent, false);
                return new HolderChatTextMe(myUserMsgView);
            case VIEW_TYPE_USER_MESSAGE_USER:
            case VIEW_TYPE_FILE_MESSAGE_CONTACT_USER:
                myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_text_user, parent, false);
                return new HolderChatTextUser(myUserMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_image_me, parent, false);
                return new HolderChatFileMe(myUserMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_USER:
                myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_image_user, parent, false);
                return new HolderChatFileUser(myUserMsgView);
            case VIEW_TYPE_ADMIN_MESSAGE:
                View adminMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_text_user, parent, false);
                return new HolderChatTextAdmin(adminMsgView);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseMessage message = items.get(position);
        if (message instanceof AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE;
        }
        if (message instanceof UserMessage) {
            // If the sender is current user
            if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                return VIEW_TYPE_USER_MESSAGE_ME;
            } else {
                return VIEW_TYPE_USER_MESSAGE_USER;
            }
        } else if (message instanceof FileMessage) {
            MessageMetaData metaData = new Gson().fromJson(message.getData(), MessageMetaData.class);
            if (metaData != null) {
                if (metaData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_IMAGE)
                        || metaData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_LOCATION)) {
                    // If the sender is current user
                    if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                    } else {
                        return VIEW_TYPE_FILE_MESSAGE_IMAGE_USER;
                    }
                } else if (metaData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_VIDEO)) {
                    if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        return VIEW_TYPE_FILE_MESSAGE_VIDEO_ME;
                    } else {
                        return VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER;
                    }
                } else if (metaData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_AUDIO)) {
                    if (message.getSender().getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                    } else {
                        return VIEW_TYPE_FILE_MESSAGE_IMAGE_USER;
                    }
                }
            }

        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE_ME:
            case VIEW_TYPE_FILE_MESSAGE_CONTACT_ME:
                ((HolderChatTextMe) holder).bind(activity, this, holder.getAdapterPosition());
                break;
            case VIEW_TYPE_USER_MESSAGE_USER:
            case VIEW_TYPE_FILE_MESSAGE_CONTACT_USER:
                ((HolderChatTextUser) holder).bind(activity, this, holder.getAdapterPosition());
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((HolderChatFileMe) holder).bind(activity, this, holder.getAdapterPosition());
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_USER:
                ((HolderChatFileUser) holder).bind(activity, this, holder.getAdapterPosition());
                break;
            case VIEW_TYPE_ADMIN_MESSAGE:
                ((HolderChatTextAdmin) holder).bind(activity, this, holder.getAdapterPosition());
                break;

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
