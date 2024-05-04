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
import com.sendbird.android.message.SendingStatus;
import com.sendbird.android.message.UserMessage;

import java.util.regex.Pattern;

;

public class HolderChatTextMe extends HolderChatBase {
    TextView txt_message;
    UserMessage message;
    LinearLayout ll_chat_contact;

    public HolderChatTextMe(@NonNull View itemView) {
        super(itemView);
        txt_message = itemView.findViewById(R.id.txt_message);
        ll_chat_contact = itemView.findViewById(R.id.ll_chat_contact);
    }

    public void bind(ChatDetailsActivity activity, ChatDetailsAdapter adapter, int position) {
        init(activity, adapter, position);
        message = (UserMessage) super.message;
        seenStatusUpdate();
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
    }

    private void seenStatusUpdate() {
        if (message.getSendingStatus().getValue().equals(SendingStatus.SUCCEEDED.getValue())) {
            if (activity.groupChannel.getMemberCount() == 1)
                img_chat_seen_status.setImageResource(R.drawable.ic_chat_delivered);
            else {
                if (activity.groupChannel.getUnreadMemberCount(message) == 0)
                    img_chat_seen_status.setImageResource(R.drawable.ic_chat_read);
                else img_chat_seen_status.setImageResource(R.drawable.ic_chat_delivered);
            }
        } else {
            img_chat_seen_status.setImageResource(R.drawable.ic_chat_sent);
        }
    }
}
