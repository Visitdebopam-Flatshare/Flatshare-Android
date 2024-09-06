package com.joinflatshare.chat.api;

import static com.joinflatshare.chat.api.SendBirdRetrofitClient.getClient;

import com.joinflatshare.api.retrofit.OnResponseCallback;
import com.joinflatshare.chat.pojo.ModelUserMetadataRequest;
import com.joinflatshare.chat.pojo.channel_detail.ChannelDetailResponse;
import com.joinflatshare.chat.pojo.channel_list.ChannelListResponse;
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse;
import com.joinflatshare.chat.pojo.user_list.ModelUserListResponse;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.pojo.BaseResponse;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.system.ConnectivityListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class SendBirdApiManager {

    public void getAllUsers(HashMap<String, String> params, OnResponseCallback<ModelUserListResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive) {
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().getAllUsers(params).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
        }
    }

    public void createUser(HashMap<String, String> params, OnResponseCallback<ModelChatUserResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive) {
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().createUser(params).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
        }
    }

    public void getUserDetails(String userId, OnResponseCallback<ModelChatUserResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().getUserDetails(userId).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void updateProfile(String userId, HashMap<String, String> params, OnResponseCallback<ModelChatUserResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().updateProfile(userId, params).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void updateUserMetadata(String userId, ModelUserMetadataRequest metadata, OnResponseCallback<ModelChatUserResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().updateUserMetadata(userId, metadata).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void joinChannel(String channelUrl, String userId, OnResponseCallback<BaseResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                new CompositeDisposable().add(getClient().joinChannel(channelUrl, map).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void searchChannel(HashMap<String, String> params, OnResponseCallback<ChannelListResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().getChannelList(params).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void getChannelDetail(String channelUrl, OnResponseCallback<ChannelDetailResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().getChannelDetail(channelUrl, "true").subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void updateChannelDetail(String channelUrl, HashMap<String, String> params, OnResponseCallback<ChannelDetailResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().updateChannel(channelUrl, params).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> sendResponse(trendsResponse, onResponseCallback),
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void leaveChannel(String channelUrl, ArrayList<String> userIds, OnResponseCallback<BaseResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                map.put("user_ids", userIds);
                new CompositeDisposable().add(getClient().leaveChannel(channelUrl, map).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> {
                                    CommonMethod.INSTANCE.makeLog("Sendbird", "Channel Left " + channelUrl);
                                    sendResponse(trendsResponse, onResponseCallback);
                                },
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void deleteChannel(String channelUrl, OnResponseCallback<BaseResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().deleteChannel(channelUrl).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> {
                                    CommonMethod.INSTANCE.makeLog("Sendbird", "Channel Deleted " + channelUrl);
                                    sendResponse(trendsResponse, onResponseCallback);
                                },
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }

    public void deleteAllPushTokens(OnResponseCallback<BaseResponse> onResponseCallback) {
        if (AppConstants.isSendbirdLive)
            if (ConnectivityListener.checkInternet()) {
                new CompositeDisposable().add(getClient().deleteAllPushTokens(AppConstants.loggedInUser.getId()).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(trendsResponse -> {
                                    CommonMethod.INSTANCE.makeLog("Sendbird", "Push Tokens deleted");
                                    sendResponse(trendsResponse, onResponseCallback);
                                },
                                throwable -> onResponseCallback.oncallBack(null)));
            }
    }


    private void sendResponse(Object response, OnResponseCallback onResponseCallback) {
        onResponseCallback.oncallBack(response);
    }

    private void handleError(Throwable throwable, OnResponseCallback<BaseResponse> onResponseCallback) {
        if (throwable instanceof HttpException) {
            try {
                String errorJson = ((HttpException) throwable).response().errorBody().string();
                JSONObject jObject = new JSONObject(errorJson);
                boolean isError = jObject.optBoolean("error");
                String message = jObject.optString("message");
                BaseResponse resp = new BaseResponse();
                resp.setError(isError);
                resp.setMessage(message);
                onResponseCallback.oncallBack(resp);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
}
