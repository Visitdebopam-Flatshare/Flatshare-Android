package com.joinflatshare.chat;

import com.debopam.progressdialog.DialogCustomProgress;
import com.joinflatshare.chat.chatInterfaces.OnFileMessageSentListener;
import com.joinflatshare.chat.chatInterfaces.OnMessageHistoryFetched;
import com.joinflatshare.chat.chatInterfaces.OnTextMessageSentListener;
import com.joinflatshare.chat.metadata.MessageMetaData;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.sendbird.android.channel.GroupChannel;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.MentionType;
import com.sendbird.android.message.PushNotificationDeliveryOption;
import com.sendbird.android.message.ReplyType;
import com.sendbird.android.message.query.PreviousMessageListQuery;
import com.sendbird.android.params.FileMessageCreateParams;
import com.sendbird.android.params.PreviousMessageListQueryParams;
import com.sendbird.android.params.UserMessageCreateParams;
import com.sendbird.android.params.common.MessagePayloadFilter;

import java.util.List;

public class SendBirdMessage {
    private BaseActivity activity;

    public SendBirdMessage(BaseActivity activity) {
        this.activity = activity;
    }

    public void sendTextMessage(GroupChannel groupChannel, UserMessageCreateParams params,
                                OnTextMessageSentListener onTextMessageSentListener) {
                /*.setMetaArrays(Arrays.asList(new MessageMetaArray("itemType", Arrays.asList("tablet")),
                        new MessageMetaArray("quality", Arrays.asList("best", "good")))*/
        params.setPushNotificationDeliveryOption(PushNotificationDeliveryOption.DEFAULT); // Either DEFAULT or SUPPRESS

        groupChannel.sendUserMessage(params, (userMessage, e) -> {
            if (e != null) {
                if (e.getMessage().contains("blocked")) {
                    CommonMethod.INSTANCE.makeToast(e.getMessage());
                } else
                    CommonMethod.INSTANCE.makeToast("Failed to send message");
                return;
            }
            onTextMessageSentListener.onTextMessageSent(userMessage);
        });
    }

    public void sendFileMessage(GroupChannel groupChannel, FileMessageCreateParams params,
                                OnFileMessageSentListener onFileMessageSentListener) {
        params.setPushNotificationDeliveryOption(PushNotificationDeliveryOption.DEFAULT); // Either DEFAULT or SUPPRESS

        groupChannel.sendFileMessage(params, (userMessage, e) -> {
            if (e != null) {
                DialogCustomProgress.INSTANCE.hideProgress(activity);
                if (e.getMessage().contains("blocked")) {
                    CommonMethod.INSTANCE.makeToast(e.getMessage());
                } else
                    CommonMethod.INSTANCE.makeToast("Failed to send message");
                return;
            }
            onFileMessageSentListener.onFileMessageSent(userMessage);
        });
    }

    public void sendTextMessageWithMention(GroupChannel groupChannel, String message,
                                           List<String> userIDsToMention,
                                           OnTextMessageSentListener onTextMessageSentListener) {
        UserMessageCreateParams params = new UserMessageCreateParams();
        params.setMessage(message);
        params.setData(new MessageMetaData().getJson(null));
        params.setMentionType(MentionType.USERS);      // Either USERS or CHANNEL
        params.setMentionedUserIds(userIDsToMention);      // Or .setMentionedUsers(LIST_OF_USERS_TO_MENTION)
                /*.setMetaArrays(Arrays.asList(new MessageMetaArray("itemType", Arrays.asList("tablet")),
                        new MessageMetaArray("quality", Arrays.asList("best", "good")))*/
        params.setPushNotificationDeliveryOption(PushNotificationDeliveryOption.DEFAULT); // Either DEFAULT or SUPPRESS

        groupChannel.sendUserMessage(params, (userMessage, e) -> {
            if (e != null) {
                if (e.getMessage().contains("blocked")) {
                    CommonMethod.INSTANCE.makeToast(e.getMessage());
                } else
                    CommonMethod.INSTANCE.makeToast("Failed to send message");
                return;
            }
            onTextMessageSentListener.onTextMessageSent(userMessage);
        });
    }

    public void getMessageHistory(GroupChannel groupChannel, OnMessageHistoryFetched onMessageHistoryFetched) {
        PreviousMessageListQueryParams params = new PreviousMessageListQueryParams();
        params.setLimit(100);
        params.setLimit(100);
        params.setReverse(true);
        params.setReplyType(ReplyType.ALL);

        MessagePayloadFilter payloadParams = new MessagePayloadFilter();
        payloadParams.setIncludeParentMessageInfo(true);
        payloadParams.setIncludeThreadInfo(true);
        params.setMessagePayloadFilter(payloadParams);

        PreviousMessageListQuery listQuery = groupChannel.createPreviousMessageListQuery(params);
        listQuery.load((list, e) -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to retrieve messages");
                return;
            }
            onMessageHistoryFetched.onMessageFetched(list);
        });
    }

    public void deleteMessage(GroupChannel groupChannel, BaseMessage message) {
        groupChannel.deleteMessage(message, e -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast(e.getMessage());
                return;
            }
        });
    }

    public void deleteChatHistory(GroupChannel groupChannel) {
        groupChannel.resetMyHistory(e -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to delete messages");
            }
        });
    }
}
