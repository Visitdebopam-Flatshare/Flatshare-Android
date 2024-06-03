package com.joinflatshare.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.api.retrofit.ApiManager;
import com.joinflatshare.chat.ApplicationChatHandler;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.ChatRequestConstants;
import com.joinflatshare.constants.IntentFilterConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.base.gpsfetcher.GpsHandler;
import com.joinflatshare.ui.chat.list.ChatListActivity;
import com.joinflatshare.ui.explore.ExploreActivity;
import com.joinflatshare.ui.checks.ChecksActivity;
import com.joinflatshare.ui.checks.RequestHandler;
import com.joinflatshare.ui.profile.myprofile.ProfileActivity;
import com.joinflatshare.utils.helper.CommonMethod;

import java.util.Objects;

public class BaseActivity extends GpsHandler {
    public BaseViewBinder baseViewBinder;
    protected BaseClickListener baseClickListener;
    public BaseApiController baseApiController;
    public ApiManager apiManager;

    //    constants
    @Deprecated
    public static final String TYPE_FLAT = "flats";
    public static final String TYPE_FHT = "fht";
    @Deprecated
    public static final String TYPE_USER = "users";
    @Deprecated
    public static final String TYPE_DATE = "dating";
    @Deprecated
    public static final String TYPE_DATE_CASUAL = "csu";
    @Deprecated
    public static final String TYPE_DATE_LONG_TERM = "ltr";
    @Deprecated
    public static final String TYPE_DATE_ACTIVITY_PARTNERS = "act";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignLoggedInUser();
        apiManager = new ApiManager(this);
        baseViewBinder = new BaseViewBinder();
        baseClickListener = new BaseClickListener(this, baseViewBinder);
        baseApiController = new BaseApiController(this);
        if (AppConstants.isSendbirdLive) {
            if (SendBirdConstants.SENDBIRD_APPID.isEmpty())
                new ApplicationChatHandler().initialise(null);
            else new ApplicationChatHandler().connectChat(null);
        }
    }

    public void showTopBar(Activity activity, boolean showback, String header, int backIcon, int icon) {
        baseViewBinder.showBack(activity);
        baseViewBinder.btn_back.setVisibility(showback ? View.VISIBLE : View.GONE);
        baseViewBinder.txt_topbar_header.setText(header);
        baseViewBinder.view_topbar_back_circle.setVisibility(View.GONE);
        ImageView img = (ImageView) baseViewBinder.btn_back.getChildAt(0);
        if (backIcon == 0)
            img.setImageResource(R.drawable.ic_back);
        else img.setImageResource(backIcon);
        if (icon == 0) {
            baseViewBinder.btn_topbar_right.setVisibility(View.GONE);
        } else if (icon == R.drawable.ic_notification) {
            baseViewBinder.btn_topbar_right.setVisibility(View.GONE);
            baseViewBinder.frame_count_notification.setVisibility(View.GONE);
        } else {
            baseViewBinder.btn_topbar_right.setVisibility(View.VISIBLE);
            baseViewBinder.btn_topbar_right.setVisibility(View.VISIBLE);
            baseViewBinder.btn_topbar_right.setImageResource(icon);
        }
        baseClickListener.manageTopbarClicks();
    }

    public void showTopbarLogo() {
        baseViewBinder.btn_back.setVisibility(View.GONE);
        baseViewBinder.img_topbar_profile.setVisibility(View.GONE);
        baseViewBinder.img_topbar_logo.setVisibility(View.VISIBLE);
    }

    public void showBottomMenu(BaseActivity activity) {
        baseViewBinder.initBottomMenu(activity);
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    // PERMISSION UTILITIES
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    public boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationIconHandler.Companion.evaluateIcon(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_NOTIFICATION);
        intentFilter.addAction(IntentFilterConstants.INTENT_FILTER_CONSTANT_CHAT_COUNT);
        intentFilter.addAction(IntentFilterConstants.INTENT_FILTER_CONSTANT_FIREBASE_TOKEN_UPDATED);
        intentFilter.addAction(IntentFilterConstants.INTENT_FILTER_CONSTANT_USER_LOCATION_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(baseBroadcastReceiver,
                intentFilter);
        // Check if fcm update is required
        if (Objects.equals(FlatShareApplication.Companion.getDbInstance().userDao().get(UserDao.USER_NEED_FCM_UPDATE), "1")) {
            baseApiController.updateUserOnFirebaseTokenUpdate();
        }
        if (this instanceof ExploreActivity ||
                this instanceof ChatListActivity || this instanceof ChecksActivity
                || this instanceof ProfileActivity)
            RequestHandler.INSTANCE.calculateTotalRequestCount(this);
    }

    private void assignLoggedInUser() {
        if (AppConstants.loggedInUser == null) {
            User user = FlatShareApplication.Companion.getDbInstance().userDao().getUser();
            if (user != null)
                AppConstants.loggedInUser = user;
            else CommonMethod.INSTANCE.logout(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(baseBroadcastReceiver);
    }


    // Incoming Broadcast Receiver
    private final BroadcastReceiver baseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                    case IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_NOTIFICATION ->
                            reloadRequest(intent);
                    case IntentFilterConstants.INTENT_FILTER_CONSTANT_CHAT_COUNT ->
                            setUnreadMessageCount();
                    case IntentFilterConstants.INTENT_FILTER_CONSTANT_FIREBASE_TOKEN_UPDATED ->
                            baseApiController.updateUserOnFirebaseTokenUpdate();
                    case IntentFilterConstants.INTENT_FILTER_CONSTANT_USER_LOCATION_UPDATED ->
                            baseApiController.updateUserLocation();
                }
            }
        }
    };

    private void reloadRequest(Intent intent) {
        String requestType = intent.getStringExtra("requestType");
        switch (requestType) {
            case ChatRequestConstants.FLAT_REQUEST_CONSTANT ->
                    RequestHandler.INSTANCE.getFlatRequests(BaseActivity.this, false);
            case ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U ->
                    RequestHandler.INSTANCE.getF2URequests(BaseActivity.this, false);
            case ChatRequestConstants.CHAT_REQUEST_CONSTANT_U2F ->
                    RequestHandler.INSTANCE.getU2FRequests(BaseActivity.this, false);
            case ChatRequestConstants.CHAT_REQUEST_CONSTANT_FHT ->
                    RequestHandler.INSTANCE.getFHTRequests(BaseActivity.this, null);
            case "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL ->
                    RequestHandler.INSTANCE.getCasualDateRequests(BaseActivity.this, false);
            case "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM ->
                    RequestHandler.INSTANCE.getLongTermRequests(BaseActivity.this, false);
            case "" + ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS ->
                    RequestHandler.INSTANCE.getActivityPartnerRequests(BaseActivity.this);
        }
    }

    private void setUnreadMessageCount() {
        if (baseViewBinder != null && baseViewBinder.view_menu_chat_circle != null) {
            baseViewBinder.view_menu_chat_circle.setVisibility(SendBirdConstants.unreadChannelCount == 0 ? View.GONE : View.VISIBLE);
        }
    }
}
