package com.joinflatshare.chat;

import androidx.activity.ComponentActivity;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.api.retrofit.OnResponseCallback;
import com.joinflatshare.chat.api.SendBirdApiManager;
import com.joinflatshare.chat.chatInterfaces.OnUserListFetchedListener;
import com.joinflatshare.chat.metadata.UserMetadata;
import com.joinflatshare.chat.pojo.ModelUserMetadataRequest;
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse;
import com.joinflatshare.chat.pojo.user_list.ModelUserListResponse;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.pojo.BaseResponse;
import com.joinflatshare.utils.helper.CommonMethod;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.params.BlockedUserListQueryParams;
import com.sendbird.android.user.query.BlockedUserListQuery;

import java.util.ArrayList;
import java.util.HashMap;

public class SendBirdUser {
    private ComponentActivity activity;
    public SendBirdApiManager apiManager;

    public SendBirdUser(ComponentActivity activity) {
        this.activity = activity;
        apiManager = new SendBirdApiManager();
    }

    public void getAllUsers(HashMap<String, String> params, OnResponseCallback<ModelUserListResponse> callback) {
        apiManager.getAllUsers(params, callback);
    }

    public void createUser(HashMap<String, String> params, OnResponseCallback<ModelChatUserResponse> callback) {
        apiManager.createUser(params, callback);
    }

    public void updateUser(String userId, HashMap<String, String> params, OnResponseCallback<ModelChatUserResponse> callback) {
        apiManager.updateProfile(userId, params, callback);
    }

    public void deleteUser(String userId) {
        apiManager.deleteUser(userId, response -> {

        });
    }

    public void updateUserMetadata(UserMetadata metadata, OnResponseCallback<ModelChatUserResponse> user) {
        ModelUserMetadataRequest request = new ModelUserMetadataRequest();
        request.setMetadata(metadata);
        apiManager.updateUserMetadata(FlatShareApplication.Companion.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID), request, user);
    }

    public void getUserDetails(String userId, OnResponseCallback<ModelChatUserResponse> user) {
        apiManager.getUserDetails(userId, user);
    }

    public void blockUser(String userId, OnitemClick onitemClick) {
        SendbirdChat.blockUser(userId, (user, e) -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to block");
                return;
            }
            onitemClick.onitemclick(null, 1);
        });
    }

    public void unblockUser(String userId, OnitemClick onitemClick) {
        SendbirdChat.unblockUser(userId, e -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to unblock");
                return;
            }
            onitemClick.onitemclick(null, 1);
        });
    }

    public void getBlockedUserList(ArrayList<String> userIds, OnUserListFetchedListener onUserListFetchedListener) {
        BlockedUserListQueryParams params = new BlockedUserListQueryParams();
        if (userIds != null && userIds.size() > 0)
            params.setUserIdsFilter(userIds);

        BlockedUserListQuery listQuery = SendbirdChat.createBlockedUserListQuery(params);
        listQuery.next((list, e) -> {
            if (e != null) {
                CommonMethod.INSTANCE.makeToast("Failed to retrieve blocked list");
                onUserListFetchedListener.onUsersFetched(null);
                return;
            }
            onUserListFetchedListener.onUsersFetched(list);
        });
    }
}
