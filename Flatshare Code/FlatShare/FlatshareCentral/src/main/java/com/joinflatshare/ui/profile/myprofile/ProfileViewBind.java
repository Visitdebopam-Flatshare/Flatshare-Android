package com.joinflatshare.ui.profile.myprofile;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.utils.helper.ImageHelper;

public class ProfileViewBind {
    private final ProfileActivity activity;
    private final ActivityProfileBinding viewBind;

    public ProfileViewBind(ProfileActivity activity, ActivityProfileBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        attachBottomSheet();
    }

    private void attachBottomSheet() {
        activity.bottomSheetBehavior = BottomSheetBehavior.from(viewBind.includeProfileVerify.bottomSheetProfileVerify);
        activity.bottomSheetBehavior.setFitToContents(true);
        activity.bottomSheetBehavior.setHideable(false);
        activity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    void setData() {
        ImageHelper.loadImage(activity, R.drawable.ic_user, viewBind.imgProfile,
                ImageHelper.getProfileImageWithAwsFromPath(AppConstants.loggedInUser.getDp()));
        viewBind.txtProfileName.setText(AppConstants.loggedInUser.getName().getFirstName() + " " + AppConstants.loggedInUser.getName().getLastName());

        if (AppConstants.loggedInUser.getVerification() == null
                || !AppConstants.loggedInUser.getVerification().isVerified()) {
            viewBind.imgProfileVerified.setImageResource(R.drawable.ic_tick_unverified);
        } else {
            viewBind.imgProfileVerified.setImageResource(R.drawable.ic_tick_verified);
        }

        /*viewBind.imgProfile.setOnClickListener(view -> {
            ArrayList<String> images = new ArrayList<>();
            images.add(ImageHelper.getProfileImageWithAws(AppConstants.loggedInUser.getId()));
            ImageHelper.showImageSlider(activity, images, 0);
        });*/
    }
}
