package com.joinflatshare.ui.chat.details.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.chat.details.ChatDetailsAdapter;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.ImageHelper;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.FileMessage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderChatFileUser extends HolderChatBase {
    FileMessage message;
    ImageView img_message;
    CircleImageView img_text_user;

    public HolderChatFileUser(@NonNull View itemView) {
        super(itemView);
        img_message = itemView.findViewById(R.id.img_message);
        img_text_user = itemView.findViewById(R.id.img_text_user);
    }

    public void bind(ChatDetailsActivity activity,
                     ChatDetailsAdapter adapter, int position) {
        init(activity, adapter, position);
        message = (FileMessage) super.message;
        MessageMetaData messageData = new Gson().fromJson(message.getData(), MessageMetaData.class);
        String url = "";
        int width = 0;
        int height = 0;
        if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_LOCATION)) {
            width = CommonMethods.getScreenWidth() / 2;
            height = (width / 16) * 9;
        } else if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_IMAGE)) {
            manageClicks();
            width = CommonMethods.getScreenWidth() / 3;
            height = CommonMethods.getScreenWidth() / 3;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        img_message.setLayoutParams(params);
        url = message.getUrl();
        ImageHelper.loadImage(activity,0,img_message,url);

        // Show user image
        if (position < adapter.getItemCount() - 1) {
            BaseMessage pMessage = activity.messageList.get(position + 1);
            if (pMessage.getSender().getUserId().equals(message.getSender().getUserId())) {
                img_text_user.setVisibility(View.GONE);
            } else {
                img_text_user.setVisibility(View.VISIBLE);
                ImageHelper.loadImage(activity, R.drawable.ic_user, img_text_user,
                        activity.sendBirdChannel.getMessageDisplayImage(message));
            }
        } else {
            img_text_user.setVisibility(View.VISIBLE);
            ImageHelper.loadImage(activity, R.drawable.ic_user, img_text_user,
                    activity.sendBirdChannel.getMessageDisplayImage(message));
        }
    }

    private void manageClicks() {
        ll_chat_click.setOnClickListener(v -> {
            ArrayList<String> images = new ArrayList<>();
            images.add(message.getUrl());
            ImageHelper.showImageSlider(activity, images, 0);
        });
    }
}
