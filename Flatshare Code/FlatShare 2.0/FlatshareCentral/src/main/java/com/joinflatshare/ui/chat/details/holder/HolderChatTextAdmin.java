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
import com.sendbird.android.message.AdminMessage;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.UserMessage;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

;

public class HolderChatTextAdmin extends HolderChatBase {
    TextView txt_message;
    AdminMessage message;
    LinearLayout ll_chat_contact;
    CircleImageView img_text_user;

    public HolderChatTextAdmin(@NonNull View itemView) {
        super(itemView);
        txt_message = itemView.findViewById(R.id.txt_message);
        ll_chat_contact = itemView.findViewById(R.id.ll_chat_contact);
        img_text_user = itemView.findViewById(R.id.img_text_user);
    }

    public void bind(ChatDetailsActivity activity,
                     ChatDetailsAdapter adapter, int position) {
        init(activity, adapter, position);
        message = (AdminMessage) super.message;
        txt_message.setText(message.getMessage());
        txt_message.setVisibility(View.VISIBLE);
        ll_chat_contact.setVisibility(View.GONE);
        // Show user image
        img_text_user.setVisibility(View.VISIBLE);
        img_text_user.setImageResource(R.mipmap.ic_launcher_round);
    }
}
