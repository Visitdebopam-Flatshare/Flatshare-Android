package com.joinflatshare.chat;

import androidx.annotation.NonNull;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.chat.api.SendBirdApiManager;
import com.joinflatshare.chat.chatInterfaces.ConnectionManagementHandler;
import com.joinflatshare.db.daos.AppDao;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.interfaces.OnStringFetched;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.utils.helper.CommonMethod;
import com.sendbird.android.ConnectionState;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.handler.ConnectionHandler;

public class SendBirdConnectionManager {
    public static boolean isSendBirdConnected = false;
    private static final String TAG = SendBirdConnectionManager.class.getSimpleName();

    public static void connect(OnStringFetched callback) {
        // The USER_ID below should be unique to your Sendbird application.
        User loggedInuser = FlatShareApplication.Companion.getDbInstance().userDao().getUser();
        if (loggedInuser == null || loggedInuser.getId().isEmpty())
            return;
        long time = System.currentTimeMillis();
        try {
            CommonMethod.INSTANCE.makeLog(TAG, "Connecting to Sendbird");
            SendbirdChat.connect(loggedInuser.getId(), (user, e) -> {
                if (e != null) {
                    isSendBirdConnected = false;
                    callback.onFetched("0");
                    return;
                }
                isSendBirdConnected = true;
                long diff = System.currentTimeMillis() - time;
                CommonMethod.INSTANCE.makeLog(TAG, "Sendbird connect time " + diff);
                registerPushNotifications();
                callback.onFetched("1");
            });
        } catch (RuntimeException exception) {
            isSendBirdConnected = false;
        }
    }

    private static void registerPushNotifications() {
        String fcmToken = FlatShareApplication.Companion.getDbInstance().appDao().get(AppDao.FIREBASE_TOKEN);
        if (fcmToken != null && !fcmToken.isEmpty()) {
            SendBirdApiManager apiManager = new SendBirdApiManager();
            apiManager.getPushToken(response -> {
                boolean needRegistration = true;
                if (response.getTokens() != null && !response.getTokens().isEmpty()) {
                    for (String token : response.getTokens()) {
                        if (token.equals(fcmToken)) {
                            needRegistration = false;
                            break;
                        }
                    }
                }
                if (needRegistration) {
                    new SendBirdApiManager().deleteAllPushTokens(responses -> SendbirdChat.registerPushToken(fcmToken, (status, error) -> {
                        if (error == null) {
                            CommonMethod.INSTANCE.makeLog(TAG, "Push token Registered");
                            CommonMethod.INSTANCE.makeLog(TAG, status.name());
                        } else {
                            CommonMethod.INSTANCE.makeLog(TAG, "Push token failed to Register");
                        }
                    }));
                }
            });
        }
    }

    public static void disconnect() {
        SendbirdChat.disconnect(() -> isSendBirdConnected = false);
    }

    public static void addConnectionManagementHandler(String handlerId,
                                                      final ConnectionManagementHandler handler) {
        SendbirdChat.addConnectionHandler(handlerId, new ConnectionHandler() {
            @Override
            public void onDisconnected(@NonNull String s) {
                isSendBirdConnected = false;
                if (handler != null) {
                    handler.onConnected(false);
                }
            }

            @Override
            public void onConnected(@NonNull String s) {
                isSendBirdConnected = true;
                if (handler != null) {
                    handler.onConnected(true);
                }
            }

            @Override
            public void onReconnectStarted() {
            }

            @Override
            public void onReconnectSucceeded() {
                isSendBirdConnected = true;
                if (handler != null) {
                    handler.onConnected(true);
                }
            }

            @Override
            public void onReconnectFailed() {
                isSendBirdConnected = false;
                if (handler != null) {
                    handler.onConnected(false);
                }
            }
        });

        if (SendbirdChat.getConnectionState() == ConnectionState.OPEN) {
            if (handler != null) {
                handler.onConnected(false);
                isSendBirdConnected = true;
            }
        } else if (SendbirdChat.getConnectionState() == ConnectionState.CLOSED) { // push notification or system kill
            String userId = FlatShareApplication.Companion.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID);
            SendbirdChat.connect(userId, (user, e) -> {
                isSendBirdConnected = e != null;
                if (handler != null) {
                    handler.onConnected(e != null);
                }
            });
        }
    }
}
