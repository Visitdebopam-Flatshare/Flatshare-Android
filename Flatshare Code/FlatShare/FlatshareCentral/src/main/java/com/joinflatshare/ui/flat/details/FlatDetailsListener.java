package com.joinflatshare.ui.flat.details;

import static com.joinflatshare.ui.base.BaseActivity.TYPE_FLAT;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatDetailsBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.ConfigConstants;
import com.joinflatshare.constants.RouteConstants;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.customviews.bottomsheet.BottomSheetView;
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.ui.dialogs.DialogIncompleteProfile;
import com.joinflatshare.ui.dialogs.report.DialogReport;
import com.joinflatshare.ui.dialogs.share.DialogShare;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;
import com.joinflatshare.utils.deeplink.DeepLinkHandler;
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;

import java.util.ArrayList;

public class FlatDetailsListener implements View.OnClickListener {
    private final FlatDetailsActivity activity;
    private final ActivityFlatDetailsBinding viewBind;
    private final MyFlatData flatData;

    public FlatDetailsListener(FlatDetailsActivity activity, ActivityFlatDetailsBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        this.flatData = activity.flatResponse.getData();
        manageClicks();
    }

    private void manageClicks() {
        viewBind.btnFlatMap.setOnClickListener(this);
        viewBind.btnEdit.setOnClickListener(this);
        viewBind.btnFlatShare.setOnClickListener(this);
        viewBind.cardLike.setOnClickListener(this);
        viewBind.txtChatRequest.setOnClickListener(this);
        viewBind.llRequestAccept.setOnClickListener(this);
        viewBind.llRequestDecline.setOnClickListener(this);
        viewBind.cardNotInterested.setOnClickListener(this);
        activity.baseViewBinder.btn_topbar_right.setOnClickListener(this);
        viewBind.pullToRefresh.setOnRefreshListener(() -> {
            activity.apiController.getFlat();
            viewBind.pullToRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == viewBind.btnFlatMap.getId()) {
            String location = flatData.getFlatProperties().getLocation().getLoc().getCoordinates().get(0) + "," + flatData.getFlatProperties().getLocation().getLoc().getCoordinates().get(1);
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location + "(" + flatData.getName() + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            activity.startActivity(mapIntent);
        } else if (id == viewBind.btnEdit.getId()) {
            Intent intent = new Intent(activity, FlatEditActivity.class);
            CommonMethod.INSTANCE.switchActivity(activity, intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    activity.apiController.getFlat();
                }
            });
        } else if (id == viewBind.txtChatRequest.getId()) {
            if (viewBind.txtChatRequest.getText().toString().equals("Chat Request")) {
                if (isFromDeeplinkIntent())
                    return;
                if (AppConstants.loggedInUser.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                    new DialogIncompleteProfile(activity, TYPE_FLAT);
                } else activity.apiController.sendConnectionRequest();
            }
        } else if (id == viewBind.cardLike.getId()) {
            if (!activity.flatResponse.isLiked()) {
                if (AppConstants.loggedInUser.getCompleted() < ConfigConstants.COMPLETION_MINIMUM_FOR_USERS) {
                    new DialogIncompleteProfile(activity, TYPE_FLAT);
                } else activity.apiController.likeFlat();
            }
        } else if (id == viewBind.llRequestAccept.getId()) {
            activity.apiController.handleRequest(activity.phone, true, isCalledBack -> {
                activity.flatBottomViewHandler.handleBottomView();
            });
        } else if (id == viewBind.llRequestDecline.getId()) {
            activity.apiController.handleRequest(activity.phone, false, isCalledBack -> {
                activity.flatBottomViewHandler.handleBottomView();
            });
        } else if (id == viewBind.btnFlatShare.getId()) {
            new DialogShare(activity, "Share Flat Profile", TYPE_FLAT, flatData, null);
        } else if (id == activity.baseViewBinder.btn_topbar_right.getId()) {
            ArrayList<ModelBottomSheet> list = new ArrayList<>();

            MyFlatData myFlat = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
            if ((myFlat == null || !myFlat.getMongoId().equals(flatData.getMongoId()))
                    && FlatShareMessageGenerator.INSTANCE.isFlatDataAvailableToShare(flatData))
                list.add(new ModelBottomSheet("Copy Link", 2));
            list.add(new ModelBottomSheet("Report", 3));

            new BottomSheetView(activity, list).show((view, position) -> {
                switch (list.get(position).getName()) {
                    case "Copy Link" -> {
                        activity.apiManager.showProgress();
                        DeepLinkHandler.INSTANCE.createFlatDynamicLink(flatData, text -> {
                                    activity.apiManager.hideProgress();
                                    if (text != null && !text.isEmpty()) {
                                        if (!text.equals("0")) {
                                            String shareMessage =
                                                    FlatShareMessageGenerator.INSTANCE.generateFlatShareMessage(flatData) + "\n\n" + text;
                                            CommonMethods.copyToClipboard(activity, shareMessage);
                                        }
                                    }
                                }
                        );
                    }
                    case "Report" ->
                            new DialogReport(activity, TYPE_FLAT, "", activity.flatResponse.getData(), text -> {
                                if (text.equals("1")) {
                                    if (activity.apiController.getCallbackIntent() == null)
                                        activity.apiController.setCallBackIntent(new Intent());
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
            if (!AppConstants.loggedInUser.isFlatSearch().getValue()) {
                AlertDialog.showAlert(activity, "Turn on your Shared Flat Search to send Checks and Chat Requests");
                return true;
            }
        }
        return false;
    }
}
