
package com.joinflatshare.ui.profile.myprofile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.debopam.ImagePicker;
import com.debopam.flatshareprogress.DialogCustomProgress;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding;
import com.joinflatshare.chat.SendBirdUser;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.IntentFilterConstants;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.interfaces.OnUserFetched;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile;
import com.joinflatshare.utils.amazonaws.AmazonUploadFile;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.io.File;
import java.util.HashMap;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding viewBind;
    ProfileViewBind dataBinder;
    private ProfileButtonBinder buttonBinder;
    protected User loggedInUser;
    protected BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    protected ProfileVerifyHandler profileVerifyHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        dataBinder = new ProfileViewBind(this, viewBind);
        showBottomMenu(this);
        new ProfileListener(this, viewBind);
        buttonBinder = new ProfileButtonBinder(this, viewBind);
        buttonBinder.setAdapter();
        profileVerifyHandler = new ProfileVerifyHandler(this, viewBind);
        MixpanelUtils.INSTANCE.onScreenOpened("Profile");
        // Check if profile has come from feed preferences and wants to open user verification
        if (getIntent().getBooleanExtra("verify", false)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> viewBind.imgProfileVerified.performClick(), 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.menuSelected = 3;
        baseViewBinder.applyMenuClick();
        dataBinder.setData();
    }

    void pickImage() {
        ImageHelper.pickImageFromGallery(this, 1, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            if (data != null) {
                Uri uri = data.getData();
                uploadFile(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CommonMethod.INSTANCE.makeToast(ImagePicker.Companion.getError(data));
        }
    }

    private void uploadFile(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            AlertDialog.showAlert(this, "Invalid Image. Please try a different image");
            return;
        }
        apiManager.showProgress();
        new AmazonUploadFile().upload(new File(path),
                AmazonUploadFile.AWS_TYPE_PROFILE_IMAGE, (intent, requestCode1) -> {
                    if (requestCode1 == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                        String serverPath = intent.getStringExtra("localpath");
                        User user = AppConstants.loggedInUser;
                        deleteOldProfileImage(user.getDp());
                        user.setDp(serverPath);
                        baseApiController.updateUser(
                                false, user, resp -> {
                                    DialogCustomProgress.INSTANCE.hideProgress(this);
                                    ;
                                    Intent intnt = new Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_RELOAD_PROFILE_IMAGE);
                                    LocalBroadcastManager.getInstance(ProfileActivity.this).sendBroadcast(intnt);
                                    updateSendbirdUserProfile();
                                    ImageHelper.loadProfileImage(ProfileActivity.this, viewBind.imgProfile, null, AppConstants.loggedInUser);
                                    ImageHelper.loadProfileImage(ProfileActivity.this, baseViewBinder.img_menu_profile, null, AppConstants.loggedInUser);
                                    CommonMethod.INSTANCE.makeToast("Profile picture updated");
                                });
                    } else {
                        DialogCustomProgress.INSTANCE.hideProgress(this);
                        ;
                        CommonMethod.INSTANCE.makeToast("Failed to update profile picture");
                    }
                });
    }

    private void updateSendbirdUserProfile() {
        SendBirdUser sendBirdUser = new SendBirdUser(this);
        HashMap<String, String> params = new HashMap();
        params.put("profile_url", TextUtils.isEmpty(AppConstants.loggedInUser.getDp()) ? "" : AppConstants.loggedInUser.getDp());
        sendBirdUser.updateUser(
                params, response -> {

                }
        );
    }

    private void deleteOldProfileImage(String path) {
        if (!TextUtils.isEmpty(path))
            new AmazonDeleteFile().delete(path, null);
    }

}
