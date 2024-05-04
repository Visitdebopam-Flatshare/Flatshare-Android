package com.joinflatshare.ui.profile.details;

import static com.joinflatshare.ui.base.BaseActivity.TYPE_USER;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.debopam.flatshareprogress.DialogCustomProgress;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding;
import com.joinflatshare.chat.SendBirdUserChannel;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.ChatRequestConstants;
import com.joinflatshare.constants.ConfigConstants;
import com.joinflatshare.constants.RouteConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.customviews.bottomsheet.BottomSheetView;
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.dialogs.DialogIncompleteProfile;
import com.joinflatshare.ui.dialogs.report.DialogReport;
import com.joinflatshare.ui.dialogs.share.DialogShare;
import com.joinflatshare.ui.profile.edit.ProfileEditActivity;
import com.joinflatshare.utils.deeplink.DeepLinkHandler;
import com.joinflatshare.utils.deeplink.UserShareMessageGenerator;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;

import java.util.ArrayList;

public class ProfileDetailsListener implements View.OnClickListener {
    private final ProfileDetailsActivity activity;
    private final ActivityProfileDetailsBinding viewBind;


    public ProfileDetailsListener(ProfileDetailsActivity activity, ActivityProfileDetailsBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        manageClicks();
    }

    private void manageClicks() {
        viewBind.cardEditProfile.setOnClickListener(this);
        viewBind.llProfileWebsite.setOnClickListener(this);
        viewBind.txtChatRequest.setOnClickListener(this);
        viewBind.cardLike.setOnClickListener(this);
        viewBind.btnProfileShare.setOnClickListener(this);
        viewBind.btnProfileMessage.setOnClickListener(this);
        activity.baseViewBinder.btn_topbar_right.setOnClickListener(this);
        viewBind.llRequestAccept.setOnClickListener(this);
        viewBind.llRequestDecline.setOnClickListener(this);
        viewBind.cardNotInterested.setOnClickListener(this);
        viewBind.pullToRefresh.setOnRefreshListener(() -> {
            activity.apiController.getProfile();
            viewBind.pullToRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == viewBind.cardEditProfile.getId()) {
            Intent intent = new Intent(activity, ProfileEditActivity.class);
            CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        } else if (id == viewBind.llProfileWebsite.getId()) {
            String url = viewBind.txtProfileWebsite.getText().toString();
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
        } else if (id == viewBind.txtChatRequest.getId()) {
            if (viewBind.txtChatRequest.getText().toString().equals("Chat Request")) {
                if (isFromDeeplinkIntent()) {
                    return;
                }
                String searchType = "";
                String mixpanelSearchType = "";
                // Check if FHT or FMS
                if (activity.isFHTSearch) {
                    searchType = BaseActivity.TYPE_FHT;
                    mixpanelSearchType = "FHT Search";
                    if (AppConstants.loggedInUser.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                        new DialogIncompleteProfile(activity, BaseActivity.TYPE_FHT);
                        return;
                    }
                } else {
                    searchType = TYPE_USER;
                    mixpanelSearchType = "Flatmate Search";
                    MyFlatData myFlat =
                            FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                    if (myFlat == null || myFlat.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                        new DialogIncompleteProfile(activity, BaseActivity.TYPE_USER);
                        return;
                    }
                }
                activity.apiController.sendChatRequest(searchType, mixpanelSearchType);
            }
        } else if (id == viewBind.cardLike.getId()) {
            String searchType = "";
            String mixpanelSearchType = "";
            if (!activity.userResponse.isLiked()) {
                // Check if FHT or FMS
                if (activity.isFHTSearch) {
                    searchType = BaseActivity.TYPE_FHT;
                    mixpanelSearchType = "FHT Search";
                    if (AppConstants.loggedInUser.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                        new DialogIncompleteProfile(activity, BaseActivity.TYPE_FHT);
                        return;
                    }
                } else {
                    searchType = TYPE_USER;
                    mixpanelSearchType = "Flatmate Search";
                    MyFlatData myFlat =
                            FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                    if (myFlat == null || myFlat.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_FLATS) {
                        new DialogIncompleteProfile(activity, BaseActivity.TYPE_USER);
                        return;
                    }
                }
                activity.apiController.likeUser(searchType, mixpanelSearchType);
            }
        } else if (id == viewBind.llRequestAccept.getId()) {
            activity.apiController.handleRequest(activity.userId, true, isCalledBack ->
            {
                switch (activity.getIntent().getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM)) {
                    case RouteConstants.ROUTE_CONSTANT_FRIEND_REQUEST:
                    case RouteConstants.ROUTE_CONSTANT_CHAT_REQUEST:
                        viewBind.rlProfileBottomRequest.setVisibility(View.GONE);
                        viewBind.btnProfileMessage.setVisibility(View.VISIBLE);
                    default:
                        activity.profileBottomViewHandler.handleBottomView();
                }
                activity.getIntent().removeExtra(RouteConstants.ROUTE_CONSTANT_FROM);
                return null;
            });
        } else if (id == viewBind.llRequestDecline.getId()) {
            activity.apiController.handleRequest(activity.userId, false, isCalledBack ->
            {
                activity.getIntent().removeExtra(RouteConstants.ROUTE_CONSTANT_FROM);
                activity.profileBottomViewHandler.handleBottomView();
                return null;
            });
        } else if (id == viewBind.btnProfileShare.getId()) {
            new DialogShare(
                    activity,
                    "Share User Profile",
                    TYPE_USER,
                    null,
                    activity.user
            );
        } else if (id == viewBind.btnProfileMessage.getId()) {
            activity.apiManager.showProgress();
            SendBirdUserChannel channel = new SendBirdUserChannel(activity);
            channel.joinUserChannel(activity.user.getId(), SendBirdConstants.CHANNEL_TYPE_FRIEND, text -> {
                DialogCustomProgress.INSTANCE.hideProgress(activity);
                if (!text.equals("0")) {
                    Intent intent = new Intent(activity, ChatDetailsActivity.class);
                    intent.putExtra("channel", text);
                    CommonMethod.INSTANCE.switchActivity(activity, intent, false);
                }
            });
        } else if (id == activity.baseViewBinder.btn_topbar_right.getId()) {
            ArrayList<ModelBottomSheet> list = new ArrayList<>();

            if (!activity.user.getId().equals(AppConstants.loggedInUser.getId())
                    && UserShareMessageGenerator.INSTANCE.isUserDataAvailableToShare(activity.user)
                    && (activity.chatConnectionType.equals(ChatRequestConstants.CHAT_REQUEST_CONSTANT_F2U) || activity.isFHTSearch)
            )
                list.add(new ModelBottomSheet("Copy Link", 2));
            list.add(new ModelBottomSheet("Report", 3));
            new BottomSheetView(activity, list).show((view, position) -> {
                switch (list.get(position).getName()) {
                    case "Copy Link" -> {
                        activity.apiManager.showProgress();
                        if (activity.isFHTSearch) {
                            DeepLinkHandler.INSTANCE.createUserFHTDynamicLink(activity.user, text -> {
                                        DialogCustomProgress.INSTANCE.hideProgress(activity);
                                        if (text != null && !text.isEmpty()) {
                                            if (!text.equals("0")) {
                                                String shareMessage =
                                                        UserShareMessageGenerator.INSTANCE.generateFHTMessage(activity.user) + "\n\n" + text;
                                                CommonMethods.copyToClipboard(activity, shareMessage);
                                            }
                                        }
                                    }
                            );
                        } else {
                            DeepLinkHandler.INSTANCE.createUserSFSDynamicLink(activity.user, text -> {
                                        DialogCustomProgress.INSTANCE.hideProgress(activity);
                                        if (text != null && !text.isEmpty()) {
                                            if (!text.equals("0")) {
                                                String shareMessage =
                                                        UserShareMessageGenerator.INSTANCE.generateUserMessage(activity.user) + "\n\n" + text;
                                                CommonMethods.copyToClipboard(activity, shareMessage);
                                            }
                                        }
                                    }
                            );
                        }
                    }
                    case "Report" ->
                            new DialogReport(activity, TYPE_USER, activity.userId, null, text -> {
                                if (text.equals("1")) {
                                    if (activity.apiController.getCallbackIntent() == null)
                                        activity.apiController.setCallbackIntent(new Intent());
                                    activity.apiController.getCallbackIntent().putExtra("report", true);
                                    CommonMethod.INSTANCE.finishActivity(activity);
                                }
                            });
                }
            });
        } else if (id == viewBind.cardNotInterested.getId()) {
            activity.apiController.reportNotInterested();
        }
    }

    private boolean isFromDeeplinkIntent() {
        String fromIntent = activity.getIntent().getStringExtra(RouteConstants.ROUTE_CONSTANT_FROM);

        if (fromIntent != null && fromIntent.equals(RouteConstants.ROUTE_CONSTANT_DEEPLINK)) {
            if (activity.isFHTSearch) {
                if (!AppConstants.loggedInUser.isFHTSearch().getValue()) {
                    AlertDialog.showAlert(activity, "Turn on Flathunt Together to send Checks and Chat Requests");
                    return true;
                }
            } else {
                MyFlatData flatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                if (flatData == null || !flatData.isMateSearch().getValue()) {
                    AlertDialog.showAlert(activity, "Turn on your flatmate search to send Checks and Chat Requests");
                    return true;
                }
            }
        }
        return false;
    }
}
