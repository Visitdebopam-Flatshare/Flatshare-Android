package com.joinflatshare.chat.chatInterfaces;


import com.joinflatshare.chat.pojo.channel_list.ChannelsItem;
import com.sendbird.android.channel.GroupChannel;

import java.util.ArrayList;
import java.util.List;

public interface OnChannelsFetchedListener {
    void onFetched(List<GroupChannel> allChannels);

    void onFetched(ArrayList<ChannelsItem> allChannels);
}
