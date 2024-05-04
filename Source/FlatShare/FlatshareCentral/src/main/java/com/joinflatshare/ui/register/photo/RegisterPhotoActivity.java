package com.joinflatshare.ui.register.photo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.debopam.ImagePicker;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterPhotoBinding;
import com.joinflatshare.chat.SendBirdUser;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.fcm.NotificationPermissionHandler;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.explore.ExploreActivity;
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile;
import com.joinflatshare.utils.amazonaws.AmazonUploadFile;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.logger.Logger;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.permission.PermissionUtil;

import java.io.File;
import java.util.HashMap;

public class RegisterPhotoActivity extends BaseActivity {
    User user;
    private boolean isUploaded = false;
    private ActivityRegisterPhotoBinding viewBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityRegisterPhotoBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        user = FlatShareApplication.Companion.getDbInstance().userDao().getUser();
        init();
    }

    private void init() {
        viewBind.txtRegisterPhotoName.setText(user.getName().getFirstName() + " " + user.getName().getLastName());
        viewBind.llTakePhoto.setOnClickListener(v -> {
            PermissionUtil.INSTANCE.validatePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, granted -> {
                if (granted)
                    pickImage();
                else {
                    String message = "Storage permission not provided for uploading image";
                    CommonMethods.makeToast( message);
                    Logger.log(message, Logger.LOG_TYPE_PERMISSION);
                }
            });
        });
        viewBind.btnCheckInvite.setOnClickListener(v -> {
            if (!isUploaded) {
                CommonMethods.makeToast( "Please upload your best photo");
            } else {
                // Requesting Notification permission for Android 13
                new NotificationPermissionHandler(this).showNotificationPermission(text -> {
                    FlatShareApplication.Companion.getDbInstance().userDao().insert(UserDao.HAS_USER_DP, "1");
                    MixpanelUtils.INSTANCE.sendToMixPanel("Registration Complete");
                    Intent intent = new Intent(RegisterPhotoActivity.this, ExploreActivity.class);
                    CommonMethod.INSTANCE.switchActivity(RegisterPhotoActivity.this, intent, false);
                    finishAffinity();
                });
            }
        });
    }

    private void pickImage() {
        ImageHelper.pickImage(this, 1, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            if (data != null) {
                apiManager.showProgress();
                Uri uri = data.getData();
                AmazonUploadFile ap = new AmazonUploadFile();
                ap.upload(new File(uri.getPath()),
                        AmazonUploadFile.AWS_TYPE_PROFILE_IMAGE, (intent, requestCode1) -> {
                            apiManager.hideProgress();
                            if (requestCode1 == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                                String serverPath = intent.getStringExtra("localpath");
                                deleteOldProfileImage(user.getDp());
                                user.setDp(serverPath);
                                apiManager.updateProfile(false, user, response -> {
                                    apiManager.hideProgress();
                                    isUploaded = true;
                                    viewBind.imgPhoto.setImageURI(uri);
                                    updateSendbirdUserProfile();
                                    FlatShareApplication.Companion.getDbInstance().userDao().insert(UserDao.HAS_USER_DP, "1");
                                    CommonMethods.makeToast( "Profile picture updated");
                                    MixpanelUtils.INSTANCE.sendToMixPanel("Photo Uploaded");
                                });
                            } else {
                                apiManager.hideProgress();
                                Logger.log("Failed to update profile picture", Logger.LOG_TYPE_ERROR);
                                CommonMethods.makeToast( "Failed to update profile picture");
                            }
                        });

            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            CommonMethods.makeToast( ImagePicker.Companion.getError(data));
        }
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
