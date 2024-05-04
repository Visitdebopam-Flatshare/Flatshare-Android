package com.joinflatshare.ui.register.setup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.RequestCodeConstants;
import com.joinflatshare.customviews.CustomButton;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.profile.create.ProfileCreateActivity;
import com.joinflatshare.ui.register.RegisterBaseActivity;
import com.joinflatshare.ui.register.ask_friend.AskFriendActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.ImageHelper;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.permission.PermissionUtil;
import com.makeramen.roundedimageview.RoundedImageView;

;

public class RegisterInvitedActivity extends RegisterBaseActivity {
    UserResponse response;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_invite);
        response = CommonMethods.getSerializable(getIntent(), "user", UserResponse.class);
        init();

    }

    private void init() {
        LinearLayout llInvited = findViewById(R.id.ll_invited);
        LinearLayout llUnInvited = findViewById(R.id.ll_uninvited);
        if (response == null || response.getInvite() == null || response.getInvite().getInv() == null) {
            // Not invited yet
            llInvited.setVisibility(View.GONE);
            llUnInvited.setVisibility(View.VISIBLE);
            CustomButton btn = findViewById(R.id.btn_check_invite);
            btn.setOnClickListener(v -> {
                PermissionUtil.INSTANCE.validatePermission(this, Manifest.permission.READ_CONTACTS, granted -> {
                    if (granted) {
                        Intent intent = new Intent(RegisterInvitedActivity.this, AskFriendActivity.class);
                        intent.putExtra("phone", getIntent().getStringExtra("phone"));
                        CommonMethod.INSTANCE.switchActivity(RegisterInvitedActivity.this, intent, false);
                    }
                });
            });
        } else {
            llInvited.setVisibility(View.VISIBLE);
            llUnInvited.setVisibility(View.GONE);

            // Mixpanel
            try {
                MixpanelUtils.INSTANCE.onUserInvitationResponded(response.getInvite().getInv().getInvitee(),
                        response.getInvite().getInv().getInviter(),
                        response.getInvite().getName().getFirstName() + " " + response.getInvite().getName().getLastName());
            } catch (Exception ignored) {

            }

            TextView txt = findViewById(R.id.txt_invitee);
            if (response.getInvite().getName() != null)
                txt.setText(getString(R.string.invited, response.getInvite().getName().getFirstName()));

            RoundedImageView img = findViewById(R.id.img_invitee);
            ImageHelper.loadImage(this, R.drawable.ic_gallery_grey, img,
                    ImageHelper.getProfileImageWithAwsFromPath(response.getInvite().getInv().getDp()));

            CustomButton btn = findViewById(R.id.btn_setup);
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileCreateActivity.class);
                intent.putExtra("user", response);
                CommonMethod.INSTANCE.switchActivity(this, intent, false);
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodeConstants.REQUEST_CODE_CONTACT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, AskFriendActivity.class);
            CommonMethod.INSTANCE.switchActivity(this, intent, false);
        }
    }
}
