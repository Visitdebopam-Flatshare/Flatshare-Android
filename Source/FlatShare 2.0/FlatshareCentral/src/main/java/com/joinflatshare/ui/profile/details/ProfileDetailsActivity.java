package com.joinflatshare.ui.profile.details;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.util.ArrayList;

public class ProfileDetailsActivity extends BaseActivity {
    private ActivityProfileDetailsBinding viewBinding;
    private ProfileDetailsDataBinder dataBinder;
    protected ProfileDetailsListener listener;
    protected ArrayList<String> image = new ArrayList<>();
    protected UserResponse userResponse;
    protected ProfileDetailsApiController apiController;
    protected ProfileBottomViewHandler profileBottomViewHandler;
    protected User user;
    protected String userId;
    protected boolean isFHTSearch = false;
    protected String chatConnectionType = "";
    protected String chatConnectionForDialogMatch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        init();
        MixpanelUtils.INSTANCE.onScreenOpened("View Profile");
    }

    private void init() {
        dataBinder = new ProfileDetailsDataBinder(this, viewBinding);
        userId = getIntent().getStringExtra("phone");
        apiController = new ProfileDetailsApiController(this, viewBinding);
        profileBottomViewHandler = new ProfileBottomViewHandler(this, viewBinding);
        apiController.getProfile();
    }

    protected void initUserData(UserResponse response) {
        this.userResponse = response;
        this.user = userResponse.getData();
        String name = user.getName().getFirstName() + " " + user.getName().getLastName();
        if (name.length() > 25) {
            name = name.substring(0, 25) + "...";
        }

        showTopBar(this, true, name, 0,
                user.getId().equals(AppConstants.loggedInUser.getId()) ? 0 : R.drawable.ic_threedots);
        dataBinder.setData();
        listener = new ProfileDetailsListener(this, viewBinding);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataBinder.adapter != null)
            dataBinder.adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (apiController.getCallbackIntent() != null)
            setResult(RESULT_OK, apiController.getCallbackIntent());
        super.onBackPressed();
    }
}