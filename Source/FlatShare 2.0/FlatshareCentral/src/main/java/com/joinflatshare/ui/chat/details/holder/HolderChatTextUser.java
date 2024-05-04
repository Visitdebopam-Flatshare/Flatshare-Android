package com.joinflatshare.ui.chat.details.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.metadata.MessageMetaData;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.chat.details.ChatDetailsAdapter;
import com.joinflatshare.utils.helper.ImageHelper;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.UserMessage;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

;

public class HolderChatTextUser extends HolderChatBase {
    TextView txt_message;
    UserMessage message;
    LinearLayout ll_chat_contact;
    CircleImageView img_text_user;

    public HolderChatTextUser(@NonNull View itemView) {
        super(itemView);
        txt_message = itemView.findViewById(R.id.txt_message);
        ll_chat_contact = itemView.findViewById(R.id.ll_chat_contact);
        img_text_user = itemView.findViewById(R.id.img_text_user);
    }

    public void bind(ChatDetailsActivity activity,
                     ChatDetailsAdapter adapter, int position) {
        init(activity, adapter, position);
        message = (UserMessage) super.message;
        MessageMetaData messageData = new Gson().fromJson(message.getData(), MessageMetaData.class);
        if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_TEXT)) {
            txt_message.setText(message.getMessage());
            txt_message.setVisibility(View.VISIBLE);
            ll_chat_contact.setVisibility(View.GONE);
        } else if (messageData.getMessageType().equals(SendBirdConstants.MESSAGE_TYPE_CONTACT)) {
            txt_message.setVisibility(View.GONE);
            ll_chat_contact.setVisibility(View.VISIBLE);
            ((TextView) ll_chat_contact.getChildAt(1)).setText(message.getMessage().split(Pattern.quote(","))[0]);
        }
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
}
