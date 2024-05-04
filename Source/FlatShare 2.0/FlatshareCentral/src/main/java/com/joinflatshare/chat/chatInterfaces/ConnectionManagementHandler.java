package com.joinflatshare.chat.chatInterfaces;

public interface ConnectionManagementHandler {
    /**
     * A callback for when connected or reconnected to refresh.
     *
     * @param reconnect Set false if connected, true if reconnected.
     */
    void onConnected(boolean reconnect);
}
