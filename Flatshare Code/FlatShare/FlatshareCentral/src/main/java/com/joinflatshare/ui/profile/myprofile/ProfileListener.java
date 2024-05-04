package com.joinflatshare.ui.profile.myprofile;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.RouteConstants;
import com.joinflatshare.ui.invite.InviteActivity;
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity;
import com.joinflatshare.ui.profile.edit.ProfileEditActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.permission.PermissionUtil;

public class ProfileListener implements View.OnClickListener {
    private final ProfileActivity activity;
    private final ActivityProfileBinding viewBind;

    public ProfileListener(ProfileActivity activity, ActivityProfileBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        manageClicks();
    }

    private void manageClicks() {
        viewBind.llTakePhoto.setOnClickListener(this);
        viewBind.llProfileEdit.setOnClickListener(this);
        viewBind.llProfileView.setOnClickListener(this);
        viewBind.imgProfileVerified.setOnClickListener(this);
        viewBind.btnInvites.setOnClickListener(this);
        viewBind.includeProfileVerify.imgProfileVerifyClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_profile_view) {
            Intent intent = new Intent(activity, ProfileDetailsActivity.class);
            intent.putExtra("phone", AppConstants.loggedInUser.getId());
            CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        } else if (id == R.id.ll_take_photo) {
            PermissionUtil.INSTANCE.validatePermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, granted -> {
                if (granted)
                    activity.pickImage();
                else CommonMethods.makeToast("Permission not provided");
            });
        } else if (id == R.id.ll_profile_edit) {
            Intent intent;
            intent = new Intent(activity, ProfileEditActivity.class);
            CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        } else if (id == R.id.btn_topbar_right) {
        } else if (id == R.id.img_profile_verified) {
            if (AppConstants.loggedInUser.getVerification() == null
                    || !AppConstants.loggedInUser.getVerification().isVerified()) {
                activity.profileVerifyHandler.prepare();
                viewBind.includeProfileVerify.bottomSheetProfileVerify.setVisibility(View.VISIBLE);
                activity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                MixpanelUtils.INSTANCE.onButtonClicked("GV on profile screen");
            }
        } else if (id == R.id.btn_invites) {
            Intent intent;
            intent = new Intent(activity, InviteActivity.class);
            intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, InviteActivity.INVITE_TYPE_APP);
            CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        } else if (id == R.id.img_profile_verify_close) {
            activity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            viewBind.includeProfileVerify.bottomSheetProfileVerify.setVisibility(View.GONE);
            CommonMethods.hideSoftKeyboard(activity);
        }
    }
}
