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
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.sendbird.android.message.FileMessage;
import com.sendbird.android.message.SendingStatus;

import java.util.ArrayList;

public class HolderChatFileMe extends HolderChatBase {
    FileMessage message;
    ImageView img_message;

    public HolderChatFileMe(@NonNull View itemView) {
        super(itemView);
        img_message = itemView.findViewById(R.id.img_message);
    }

    public void bind(ChatDetailsActivity activity,
                     ChatDetailsAdapter adapter, int position) {
        init(activity, adapter, position);
        message = (FileMessage) super.message;
        seenStatusUpdate();
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
        ImageHelper.loadImage(activity, R.mipmap.ic_launcher, img_message, url);
    }

    private void seenStatusUpdate() {
        if (message.getSendingStatus().getValue().equals(SendingStatus.SUCCEEDED.getValue())) {
            if (activity.groupChannel.getUnreadMemberCount(message) == 0)
                img_chat_seen_status.setImageResource(R.drawable.ic_chat_read);
            else img_chat_seen_status.setImageResource(R.drawable.ic_chat_delivered);
        } else {
            img_chat_seen_status.setImageResource(R.drawable.ic_chat_sent);
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
