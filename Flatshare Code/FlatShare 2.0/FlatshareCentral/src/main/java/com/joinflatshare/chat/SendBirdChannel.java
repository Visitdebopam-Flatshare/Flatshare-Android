package com.joinflatshare.chat;

import com.joinflatshare.chat.api.SendBirdApiManager;
import com.joinflatshare.chat.chatInterfaces.OnChannelCreatedListener;
import com.joinflatshare.chat.chatInterfaces.OnChannelsFetchedListener;
import com.joinflatshare.customviews.alert.AlertImageDialog;
import com.joinflatshare.interfaces.OnStringFetched;
import com.joinflatshare.ui.base.ApplicationBaseActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.channel.GroupChannel;
import com.sendbird.android.channel.query.GroupChannelListQuery;
import com.sendbird.android.channel.query.GroupChannelListQueryOrder;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.params.GroupChannelListQueryParams;
import com.sendbird.android.user.Member;

import java.util.ArrayList;
import java.util.List;

public class SendBirdChannel {
    private final ApplicationBaseActivity activity;
    private final SendBirdUser sendBirdUser;
    SendBirdApiManager sendBirdApiManager;

    public SendBirdChannel(ApplicationBaseActivity activity) {
        this.activity = activity;
        sendBirdUser = new SendBirdUser(activity);
        sendBirdApiManager = new SendBirdApiManager();
    }

    public void getChannelList(OnChannelsFetchedListener onChannelsFetchedListener) {
        GroupChannelListQueryParams params = new GroupChannelListQueryParams();
        params.setIncludeEmpty(true);
        params.setOrder(GroupChannelListQueryOrder.LATEST_LAST_MESSAGE);    // CHRONOLOGICAL, CHANNEL_NAME_ALPHABETICAL, and METADATA_VALUE_ALPHABETICAL
        params.setLimit(100);

        GroupChannelListQuery listQuery = GroupChannel.createMyGroupChannelListQuery(params);
        listQuery.next((list, e) -> {
            if (e != null) {
                AlertImageDialog.INSTANCE.somethingWentWrong(activity);
                return;
            }
            onChannelsFetchedListener.onFetched(list);
        });

    }

    public void getChannelDetail(String channelUrl, OnChannelCreatedListener
            onChannelCreatedListener) {
        GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
            if (e != null) {
                AlertImageDialog.INSTANCE.somethingWentWrong(activity);
                return;
            }
            onChannelCreatedListener.onCreated(groupChannel);
        });
    }


    public void joinChannel(String channelUrl, String userId, OnStringFetched callback) {
        sendBirdApiManager.joinChannel(channelUrl, userId, response -> {
            callback.onFetched("1");
        });
    }

    public void deleteChannel(String channelUrl) {
        /*sendBirdApiManager.deleteChannel(channelUrl, response -> {

        });*/
    }

    public void leaveChannel(String channelUrl, ArrayList<String> userIds, OnStringFetched onStringFetched) {
        sendBirdApiManager.leaveChannel(channelUrl, userIds, response -> {
            if (response == null)
                onStringFetched.onFetched("");
            else onStringFetched.onFetched("success");
        });
    }

    public String getChannelDisplayName(GroupChannel groupChannel) {
        List<Member> members = groupChannel.getMembers();

        if (members.size() < 2 || SendbirdChat.getCurrentUser() == null) {
            return "No Members";
        } else {
            if (groupChannel.getMembers().size() == 2) {
                for (Member member : groupChannel.getMembers()) {
                    if (member.getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                        continue;
                    }
                    return member.getNickname();
                }
            }
        }
        return "";
    }

    public String getChannelDisplayImage(GroupChannel groupChannel) {
        if (groupChannel.getUrl().startsWith("USER")) {
            List<Member> members = groupChannel.getMembers();
            if (members.size() < 2 || SendbirdChat.getCurrentUser() == null) {
                return "";
            } else {
                String image = "";
                if (members.size() == 2) {
                    for (Member member : members) {
                        if (!member.getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                            String url = member.getProfileUrl();
                            if (url.contains("flatshare"))
                                image = url;
                            else
                                image = ImageHelper.getProfileImageWithAwsFromPath(member.getProfileUrl());
                            break;
                        }

                    }
                }
                return image;
            }
        } else
            return ImageHelper.getFlatDpWithAws(getFlatId(groupChannel.getUrl()));
    }

    public String getMessageDisplayImage(BaseMessage message) {
        return ImageHelper.getProfileImageWithAwsFromPath(message.getSender().getProfileUrl());
    }

    public String getChannelDisplayUserId(GroupChannel groupChannel) {
        List<Member> members = groupChannel.getMembers();
        if (members.size() == 2) {
            for (Member member : members) {
                if (!member.getUserId().equals(SendbirdChat.getCurrentUser().getUserId())) {
                    return member.getUserId();
                }
            }
        }
        return "";
    }

    public String getTypingUser(GroupChannel channel) {
        List<com.sendbird.android.user.User> typingUsers = channel.getTypingUsers();
        if (channel.isTyping()) {
            if (typingUsers.size() < 3) {
                return "typing...";
            } else {
                return "Someone is typing...";
            }
        } else return "";
    }

    public void getOnlineStatus(GroupChannel channel, OnStringFetched onStringFetched) {
        if (channel.getMembers().size() == 2) {
            String channelDisplayUserId = getChannelDisplayUserId(channel);
            SendBirdUser sendBirdUser = new SendBirdUser(activity);
            sendBirdUser.getUserDetails(channelDisplayUserId, user -> {
                if (user != null) {
                    onStringFetched.onFetched(user.isOnline() ? "Online" : "Offline");
                } else {
                    onStringFetched.onFetched("Offline");
                }
            });
        } else onStringFetched.onFetched("");
    }

    public void removeChatHistory(GroupChannel groupChannel, OnChannelCreatedListener listener) {
        groupChannel.resetMyHistory(e -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to delete history");
                return;
            }
            listener.onCreated(groupChannel);
        });
    }

    public String getFlatId(String url) {
        CommonMethod.INSTANCE.makeLog("Sendbird url", url);
        if (url.startsWith("FLAT_"))
            return url.substring(5);
        else if (url.startsWith("FLATMATE_") && url.length() >= 10)
            return url.substring(9, url.lastIndexOf("_"));
        return "";
    }
}
