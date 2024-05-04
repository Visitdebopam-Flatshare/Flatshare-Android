package com.joinflatshare.chat.api;


import com.joinflatshare.chat.pojo.ModelUserMetadataRequest;
import com.joinflatshare.chat.pojo.channel_detail.ChannelDetailResponse;
import com.joinflatshare.chat.pojo.channel_list.ChannelListResponse;
import com.joinflatshare.chat.pojo.user.ModelChatUserResponse;
import com.joinflatshare.pojo.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface SendBirdApiInterface {

    @POST("users")
    Observable<ModelChatUserResponse> createUser(@Body HashMap<String, String> params);

    @GET("users/{user_id}")
    Observable<ModelChatUserResponse> getUserDetails(@Path("user_id") String user_id);

    @PUT("users/{user_id}")
    Observable<ModelChatUserResponse> updateProfile(@Path("user_id") String user_id,
                                                    @Body HashMap<String, String> params);

    @PUT("users/{user_id}/metadata")
    Observable<ModelChatUserResponse> updateUserMetadata(@Path("user_id") String user_id,
                                                         @Body ModelUserMetadataRequest metadata);

    @GET("group_channels")
    Observable<ChannelListResponse> getChannelList(@QueryMap HashMap<String, String> params);

    @GET("group_channels/{channel_url}")
    Observable<ChannelDetailResponse> getChannelDetail(@Path("channel_url") String channelUrl,
                                                       @Query("show_member") String show_member);

    @PUT("group_channels/{channel_url}")
    Observable<ChannelDetailResponse> updateChannel(@Path("channel_url") String channelUrl,
                                                    @Body HashMap<String, String> userId);

    @PUT("group_channels/{channel_url}/join")
    Observable<BaseResponse> joinChannel(@Path("channel_url") String channelUrl,
                                         @Body HashMap<String, String> userId);

    @PUT("group_channels/{channel_url}/leave")
    Observable<BaseResponse> leaveChannel(@Path("channel_url") String channelUrl,
                                          @Body HashMap<String, ArrayList<String>> userIds);

    @DELETE("group_channels/{channel_url}")
    Observable<BaseResponse> deleteChannel(@Path("channel_url") String channelUrl);

    @DELETE("users/{userId}/push")
    Observable<BaseResponse> deleteAllPushTokens(@Path("userId") String userId);

    /*@POST("profile")
    Observable<OtpResponse> updateProfile(@Body ModelUser user);

    @GET("profile")
    Observable<OtpResponse> getProfile(@Query("phone") String phone);

    @GET("getflatoptions")
    Observable<FlatAmenityResponse> getFlatOptions();

    @GET("flatprofile")
    Observable<FlatDataResponse> getMyFlat(@Query("phone") String phone);

    @POST("flatprofile")
    Observable<FlatDataResponse> updateFlat(@Body FlatDataResponse dataResponse);

    @GET("getfaqs")
    Observable<FaqResponse> getFaqs();

    @GET("getAWSKeys")
    Observable<AwsResponse> getAwsKeys();

    @POST("remove_member")
    Observable<FlatDataResponse> removeMember(@Body HashMap<String, String> params);

    @POST("transfer_ownership")
    Observable<FlatDataResponse> makeAdmin(@Body HashMap<String, String> params);*/
}
