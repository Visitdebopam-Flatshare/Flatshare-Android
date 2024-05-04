package com.joinflatshare.ui.profile.create;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding;
import com.joinflatshare.api.retrofit.ApiManager;
import com.joinflatshare.chat.ApplicationChatHandler;
import com.joinflatshare.chat.SendBirdUser;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.customviews.interests.InterestsView;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.pojo.user.Loc;
import com.joinflatshare.pojo.user.ModelLocation;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.base.gpsfetcher.GpsHandlerCallback;
import com.joinflatshare.ui.register.RegisterBaseActivity;
import com.joinflatshare.ui.register.photo.RegisterPhotoActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.system.DeviceInformationCollector;

import java.util.HashMap;

public class ProfileCreateActivity extends RegisterBaseActivity implements OnUiEventClick {
    protected ActivityProfileCreateBinding viewBind;
    public ProfileCreateDataBinder dataBinder;
    UserResponse response;
    User modelUser;
    protected boolean isNewUser = false;
    private ApiManager apiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityProfileCreateBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        init();
    }

    private void init() {
        User loggedInUser = FlatShareApplication.Companion.getDbInstance().userDao().getUser();
        isNewUser = loggedInUser == null || loggedInUser.getName() == null ||
                loggedInUser.getName().getFirstName().isEmpty();

        if (isNewUser) {
            response = CommonMethods.getSerializable(getIntent(), "user", UserResponse.class);
            modelUser = response.getData();
        } else {
            modelUser = loggedInUser;
        }
        apiManager = new ApiManager(this);
        dataBinder = new ProfileCreateDataBinder(this, viewBind);
        new ProfileCreateListener(this, viewBind);
        dataBinder.setData();
    }

    void checkLocation() {
        apiManager.showProgress();
        getLocation(this,new GpsHandlerCallback() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                Loc loc = new Loc();
                loc.getCoordinates().add(longitude);
                loc.getCoordinates().add(latitude);
                modelUser.setLocation(new ModelLocation("My Location", loc));
                apiManager.hideProgress();
                updateUser();
            }

            @Override
            public void onLocationFailed() {
                apiManager.hideProgress();
                CommonMethods.makeToast("Could not proceed without user location");
            }
        });
    }

    void updateUser() {
        ApiManager apiManager = new ApiManager(this);
        apiManager.updateProfile(true, modelUser, response -> {
            new DeviceInformationCollector();
            MixpanelUtils.INSTANCE.sendToMixPanel("Member Joined");
            Intent intent = new Intent(ProfileCreateActivity.this, RegisterPhotoActivity.class);
            CommonMethod.INSTANCE.switchActivity(ProfileCreateActivity.this, intent, true);

            if (AppConstants.isSendbirdLive) {
                new ApplicationChatHandler().initialise(text -> {
                    if (text.equals("1")) {
                        SendBirdUser sendBirdUser = new SendBirdUser(ProfileCreateActivity.this);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id", modelUser.getId());
                        params.put("nickname", modelUser.getName().getFirstName() + " " + modelUser.getName().getLastName());
                        sendBirdUser.createUser(params, response1 -> {
                        });
                    }
                });
            }
            finishAffinity();
        });
    }

    @Override
    public void onClick(Intent intent, int requestCode) {
        if (requestCode == 1) {
            String type = intent.getStringExtra("type");
            if (type.equals(InterestsView.VIEW_TYPE_INTERESTS)) {
                viewBind.txtProfileInterest.setText(intent.getStringExtra("interest"));
                viewBind.txtInterestCount.setText("(" + intent.getIntExtra("count", 0) + "/5)");
            } else if (type.equals(InterestsView.VIEW_TYPE_LANGUAGES)) {
                viewBind.txtProfileLanguages.setText(intent.getStringExtra("interest"));
                viewBind.txtLanguageCount.setText("(" + intent.getIntExtra("count", 0) + "/3)");
            }
        }
    }
}
