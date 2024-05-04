package com.joinflatshare.ui.profile.details;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.customviews.deal_breakers.DealBreakerView;
import com.joinflatshare.customviews.interests.InterestsView;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.user.FlatProperties;
import com.joinflatshare.pojo.user.Name;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.flat.edit.FlatMemberAdapter;
import com.joinflatshare.ui.profile.edit.ProfileEditImageAdapter;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.DateUtils;
import com.joinflatshare.utils.helper.ImageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Pattern;

;

public class ProfileDetailsDataBinder {
    private final ProfileDetailsActivity activity;
    protected ProfileEditImageAdapter adapter;
    private final ActivityProfileDetailsBinding viewBinding;

    private TextView[] txt_profile;
    private LinearLayout[] ll_profile;

    protected ProfileDetailsDataBinder(ProfileDetailsActivity activity, ActivityProfileDetailsBinding viewBinding) {
        this.activity = activity;
        this.viewBinding = viewBinding;
        bind();
    }

    private void bind() {
        viewBinding.rvProfileInfoImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ProfileEditImageAdapter(activity);
        viewBinding.rvProfileInfoImage.setAdapter(adapter);

        txt_profile = new TextView[]{
                viewBinding.txtProfileWork,
                viewBinding.txtProfileHometown,
                viewBinding.txtProfileCollege,
                viewBinding.txtProfileHangout
        };
        ll_profile = new LinearLayout[]{
                viewBinding.llProfileWork,
                viewBinding.llProfileHometown,
                viewBinding.llProfileCollege,
                viewBinding.llProfileHangout
        };
    }

    protected void setData() {
        User userData = activity.user;

        // Verified
        if (userData.getVerification() != null && userData.getVerification().isVerified()) {
            activity.baseViewBinder.img_topbar_text_header.setVisibility(View.VISIBLE);
        }

        // status
        if (userData.getStatus() == null || userData.getStatus().isEmpty())
            viewBinding.llStatus.setVisibility(View.GONE);
        else {
            viewBinding.llStatus.setVisibility(View.VISIBLE);
            viewBinding.txtProfileStatusName.setText("About " + userData.getName().getFirstName() + " and Looking for");
            viewBinding.txtProfileStatus.setText(userData.getStatus());
        }

        // IMAGE
        activity.image.clear();
        loadImages(userData);

        // Age
        setAge(userData);

        // Distance
        setDistance(userData);

        // Score
        int score = userData.getScore();
        if (score < 0) score = 0;
        viewBinding.txtProfileScore.setText("" + score);

        // Website
        if (userData.getWebsite().isEmpty()) {
            viewBinding.llProfileWebsite.setVisibility(View.GONE);
        } else {
            viewBinding.llProfileWebsite.setVisibility(View.VISIBLE);
            viewBinding.txtProfileWebsite.setText(userData.getWebsite());
        }

        if (userData.getWork() != null && userData.getWork().isEmpty()
                && userData.getHometown() != null && userData.getHometown().getName().isEmpty()
                && userData.getCollege() != null && userData.getCollege().getName().isEmpty()
                && userData.getHangout() != null && userData.getHangout().getName().isEmpty()) {
            viewBinding.llProfileHolder.setVisibility(View.GONE);
        } else {
            viewBinding.llProfileHolder.setVisibility(View.VISIBLE);
            viewBinding.txtProfileAboutName.setText("About " + userData.getName().getFirstName());

            // Work
            if (userData.getWork().isEmpty())
                ll_profile[0].setVisibility(View.GONE);
            else {
                ll_profile[0].setVisibility(View.VISIBLE);
                txt_profile[0].setText(userData.getWork());
            }

            // Hometown
            if (userData.getHometown() == null || userData.getHometown().getName().isEmpty())
                ll_profile[1].setVisibility(View.GONE);
            else {
                ll_profile[1].setVisibility(View.VISIBLE);
                txt_profile[1].setText(userData.getHometown().getName());
            }

            // College
            if (userData.getCollege().getName().isEmpty())
                ll_profile[2].setVisibility(View.GONE);
            else {
                ll_profile[2].setVisibility(View.VISIBLE);
                txt_profile[2].setText(userData.getCollege().getName());
            }

            // Hangout
            if (userData.getHangout() == null || userData.getHangout().getName().isEmpty())
                ll_profile[3].setVisibility(View.GONE);
            else {
                ll_profile[3].setVisibility(View.VISIBLE);
                txt_profile[3].setText(userData.getHangout().getName());
            }
        }

        // Friends
        setFriends();

        // Interest
        setInterests(userData);

        // Languages
        setLanguages(userData);

        // Deal Breakers
        setDealBreakers(userData);

        // Inviter
        if (activity.userResponse.getInvite() != null && activity.userResponse.getInvite().getInv() != null) {
            viewBinding.llInviter.setVisibility(View.VISIBLE);
            ImageHelper.loadImage(activity, R.drawable.ic_user, viewBinding.imgInviter, ImageHelper.getProfileImageWithAwsFromPath(activity.userResponse.getData().getDp()));
            Name name = activity.userResponse.getInvite().getName();
            if (name != null) {
                String fName = activity.userResponse.getInvite().getName().getFirstName() + " " +
                        activity.userResponse.getInvite().getName().getLastName();
                if (!(fName == null || fName.isEmpty() || fName.toLowerCase().contains(activity.getResources().getString(R.string.app_name)))) {
                    viewBinding.txtInviter.setVisibility(View.VISIBLE);
                    viewBinding.txtInviter.setText(activity.getResources().getString(R.string.invited_text, fName));
                    viewBinding.llInviter.setOnClickListener(view -> {
                        Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                        intent.putExtra("phone", activity.userResponse.getInvite().getInv().getInviter());
                        CommonMethod.INSTANCE.switchActivity(activity, intent, false);
                    });
                }
            }
            if (!activity.userResponse.getData().getCreatedAt().isEmpty())
                viewBinding.txtJoinDate.setText("Joined " + DateUtils.INSTANCE.getInviterDate(activity.userResponse.getData().getCreatedAt()));
        }

        // Bottom buttons
        if (AppConstants.loggedInUser.getId().equals(userData.getId())) {
            viewBinding.cardEditProfile.setVisibility(View.VISIBLE);
        }

        // Turning on the main view at the end
        viewBinding.pullToRefresh.setVisibility(View.VISIBLE);
    }

    private void setInterests(User userData) {
        if (userData.getFlatProperties().getInterests() == null || userData.getFlatProperties().getInterests().size() == 0)
            viewBinding.llProfileInterestHolder.setVisibility(View.GONE);
        else {
            viewBinding.llProfileInterestHolder.setVisibility(View.VISIBLE);
            FlatProperties properties = userData.getFlatProperties();
            InterestsView interestsView = new InterestsView(activity, viewBinding.rvProfileInterests,
                    InterestsView.VIEW_TYPE_INTERESTS);
            interestsView.setContentValues(properties.getInterests());
            try {
                MyFlatData myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                if (!userData.getId().equals(AppConstants.loggedInUser.getId())) {
                    interestsView.calculateMatchingContent(myFlatData.getFlatProperties().getInterests());
                }
            } catch (Exception ignored) {

            }
            interestsView.show();
        }
    }

    private void setLanguages(User userData) {
        if (userData.getFlatProperties().getLanguages() == null || userData.getFlatProperties().getLanguages().size() == 0)
            viewBinding.llProfileLanguageHolder.setVisibility(View.GONE);
        else {
            viewBinding.llProfileLanguageHolder.setVisibility(View.VISIBLE);
            FlatProperties properties = userData.getFlatProperties();
            InterestsView interestsView = new InterestsView(activity, viewBinding.rvProfileLanguage,
                    InterestsView.VIEW_TYPE_LANGUAGES);
            interestsView.setContentValues(properties.getLanguages());
            try {
                MyFlatData myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                if (!userData.getId().equals(AppConstants.loggedInUser.getId())) {
                    interestsView.calculateMatchingContent(myFlatData.getFlatProperties().getLanguages());
                }
            } catch (Exception ignored) {

            }
            interestsView.show();
        }
    }

    private void setDealBreakers(User userData) {
        DealBreakerView dealBreakerView = new DealBreakerView(activity, viewBinding.rvProfileDeals);
        dealBreakerView.setDealValues(userData.getFlatProperties(), viewBinding.llProfileDealsHolder);
        dealBreakerView.show();
    }

    private void setDistance(User userData) {
        viewBinding.llProfileLocation.setVisibility(View.GONE);
        /*try {
            if (userData.getId().equals(AppConstants.loggedInUser.getId()))
                viewBinding.txtProfileLocation.setText("NA");
            else {
                viewBinding.txtProfileLocation.setText(DistanceCalculator.INSTANCE.calculateDistance(
                        userData.getLocation().getLoc().getCoordinates().get(0),
                        userData.getLocation().getLoc().getCoordinates().get(1),
                        AppConstants.loggedInUser.getLocation().getLoc().getCoordinates().get(0),
                        AppConstants.loggedInUser.getLocation().getLoc().getCoordinates().get(1)
                ));
            }
        } catch (Exception exception) {
            viewBinding.txtProfileLocation.setText("NA");
        }*/
    }

    private void setAge(User userData) {
        // Age calculator
        String dob = userData.getDob();
        if (!dob.isEmpty()) {
            int bYear = Integer.parseInt(dob.split(Pattern.quote("/"))[0]);
            int cYear = Calendar.getInstance().get(Calendar.YEAR);
            int bMonth = Integer.parseInt(dob.split(Pattern.quote("/"))[1]);
            int cMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            if (bMonth > cMonth)
                viewBinding.txtProfileAge.setText("" + (cYear - bYear - 1));
            else viewBinding.txtProfileAge.setText("" + (cYear - bYear));
        }
    }

    private void loadImages(User userData) {
        if (userData.getImages().size() > 0) {
            activity.image.addAll(userData.getImages());
            Collections.reverse(activity.image);
            viewBinding.rvProfileInfoImage.setVisibility(View.VISIBLE);
            adapter.setItems(activity.image);
        } else viewBinding.rvProfileInfoImage.setVisibility(View.GONE);
    }

    private void setFriends() {
        if (activity.userResponse.getFriends() == null || activity.userResponse.getFriends().size() == 0) {
            viewBinding.llProfileFriends.setVisibility(View.GONE);
        } else {
            viewBinding.llProfileFriends.setVisibility(View.VISIBLE);
            viewBinding.rvFlatMember.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            ArrayList<User> friends = activity.userResponse.getFriends();
            friends.sort((lhs, rhs) -> rhs.getScore() - lhs.getScore());
            viewBinding.rvFlatMember.setAdapter(new FlatMemberAdapter(activity, null, friends));
        }
    }
}
