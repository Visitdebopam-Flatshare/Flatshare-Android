package com.joinflatshare.ui.flat.flat_profile;

import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_DEPOSIT;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_GENDER;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_INTEREST;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_LANGUAGE;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_MOVEINDATE;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_RENT;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.debopam.progressdialog.DialogCustomProgress;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatBinding;
import com.joinflatshare.chat.SendBirdFlatChannel;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.constants.UrlConstants;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.dialogs.DialogFlatname;
import com.joinflatshare.ui.flat.details.FlatDetailsActivity;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;
import com.joinflatshare.ui.flat.verify.FlatVerifyActivity;
import com.joinflatshare.utils.amazonaws.AmazonFileChecker;
import com.joinflatshare.utils.amazonaws.AmazonUploadFile;
import com.joinflatshare.utils.deeplink.DeepLinkHandler;
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.system.Prefs;

public class MyFlatListener implements View.OnClickListener {
    private final MyFlatActivity activity;
    private final ActivityFlatBinding viewBind;
    private final MyFlatViewBind dataBind;

    public MyFlatListener(MyFlatActivity activity, ActivityFlatBinding viewBind,
                          MyFlatViewBind dataBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        this.dataBind = dataBind;
        manageClicks();
    }

    private void manageClicks() {
        activity.baseViewBinder.img_topbar_profile.setOnClickListener(this);
        viewBind.llFlatmateSearch.setOnClickListener(this);
        viewBind.llFlatEdit.setOnClickListener(this);
        viewBind.llFlatView.setOnClickListener(this);
        viewBind.btnLeaveFlat.setOnClickListener(this);
        viewBind.llFlatVerified.setOnClickListener(this);
        viewBind.btnChatFlat.setOnClickListener(this);
        viewBind.btnFlatmateSearch.setOnClickListener(this);
        viewBind.txtPrefFlatmateCopyLink.setOnClickListener(this);
        viewBind.txtFlatmateCloseSearch.setOnClickListener(this);
        for (int i = 0; i < dataBind.ll_flatmate_search_options.length; i++) {
            int j = 0;
            switch (i) {
                case 0:
                    j = VIEW_CONSTANT_GENDER;
                    break;
                case 1:
                    j = VIEW_CONSTANT_MOVEINDATE;
                    break;
                case 2:
                    j = VIEW_CONSTANT_RENT;
                    break;
                case 3:
                    j = VIEW_CONSTANT_DEPOSIT;
                    break;
                case 4:
                    j = VIEW_CONSTANT_LANGUAGE;
                    break;
                case 5:
                    j = VIEW_CONSTANT_INTEREST;
                    break;
            }
            final int position = j;
            dataBind.ll_flatmate_search_options[i].setOnClickListener(view -> {
                activity.flatOption.selectedViewConstant = position;
                activity.flatOption.switchView();
            });
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ll_flat_edit) {
            Intent intent = new Intent(activity, FlatEditActivity.class);
            CommonMethod.INSTANCE.switchActivity(activity, intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    activity.setResponse();
                }
            });
            MixpanelUtils.INSTANCE.onButtonClicked("Edit Flat Profile");
        } else if (viewId == R.id.ll_flat_view) {
            Intent intent;
            intent = new Intent(activity, FlatDetailsActivity.class);
            intent.putExtra("phone", AppConstants.loggedInUser.getId());
            CommonMethod.INSTANCE.switchActivity(activity, intent, false);
        } else if (viewId == R.id.ll_flatmate_search) {
            if (activity.myFlatData == null || activity.myFlatData.getName().isEmpty()) {
                new DialogFlatname(activity, text -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                    MixpanelUtils.INSTANCE.sendToMixPanel("Flat Created");
                    viewBind.llFlatmateSearch.performClick();
                });
            } else {
                activity.myFlatData.isMateSearch().setValue(true);
                activity.apiManager.updateFlat(true, activity.myFlatData, response -> {
                    CommonMethod.INSTANCE.makeToast("Your flatmate search is turned on");
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                    dataBind.calculateFlatmateCompletion();
                    MixpanelUtils.INSTANCE.onButtonClicked("Flatmate Search");
                });
            }
        } else if (viewId == R.id.ll_flat_verified) {
            if (activity.myFlatData == null) {
                CommonMethod.INSTANCE.makeToast("Please create your flat.");
                return;
            }
            if (activity.myFlatData.isVerified()) {
                AlertDialog.showAlert(activity, "Your flat is already verified.");
                return;
            }
            String id = activity.myFlatData.getMongoId();
            String url = UrlConstants.INSTANCE.getUSER_IMAGE_URL() + id + "/Verification.mp4";
            new AmazonFileChecker(activity).checkObject(url, (intent12, requestCode) -> {
                if (requestCode == AmazonUploadFile.REQUEST_CODE_SUCCESS) {
                    AlertDialog.showAlert(activity, "Your flat verification is in progress.");
                } else {
                    MixpanelUtils.INSTANCE.onButtonClicked("Verify Flat - Flat screen");
                    Intent intent1 = new Intent(activity, FlatVerifyActivity.class);
                    CommonMethod.INSTANCE.switchActivity(activity, intent1, false);
                }
            });
        } else if (viewId == R.id.img_topbar_profile || viewId == R.id.btn_flatmate_search) {
            CommonMethod.INSTANCE.finishActivity(activity);
        } else if (viewId == R.id.btn_chat_flat) {
            if (activity.myFlatData == null) {
                CommonMethod.INSTANCE.makeToast("Please create your flat");
                return;
            }
            if (AppConstants.isSendbirdLive) {
                activity.apiManager.showProgress();
                SendBirdFlatChannel channel = new SendBirdFlatChannel(activity);
                channel.addFlatMember(activity.myFlatData.getMongoId(),
                        AppConstants.loggedInUser.getId(), SendBirdConstants.CHANNEL_TYPE_FLAT, text -> {
                            if (!text.equals("0")) {
                                DialogCustomProgress.INSTANCE.hideProgress(activity);
                                Intent intent2 = new Intent(activity, ChatDetailsActivity.class);
                                intent2.putExtra("channel", text);
                                CommonMethod.INSTANCE.switchActivity(activity, intent2, false);
                            }
                        });
            }
        } else if (viewId == R.id.btn_leave_flat) {
            AlertDialog.showAlert(activity, "", "Are you sure you want to\nexit this shared flat?",
                    "Yes", "No", (intent3, requestCode) -> {
                        if (requestCode == 1) {
                            final String phone = AppConstants.loggedInUser.getId();
                            activity.apiManager.removeFlatmate(phone, response -> {
                                CommonMethod.INSTANCE.clearFlat();
                                activity.prefs.setString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED, "1");
                                CommonMethod.INSTANCE.finishActivity(activity);
                            });
                        }
                    });
        } else if (viewId == R.id.txt_flatmate_close_search) {
            activity.myFlatData.isMateSearch().setValue(false);
            activity.apiManager.updateFlat(true, activity.myFlatData, response -> {
                CommonMethod.INSTANCE.makeToast("Your flatmate search is closed");
                MixpanelUtils.INSTANCE.onButtonClicked("Close Flatmate Search");
                AppConstants.isFeedReloadRequired = true;
                activity.setResult(Activity.RESULT_OK);
                CommonMethod.INSTANCE.finishActivity(activity);
            });
        } else if (viewId == R.id.txt_pref_flatmate_copy_link) {
            activity.apiManager.showProgress();
            DeepLinkHandler.INSTANCE.createFlatDynamicLink(
                    activity.myFlatData, text -> {
                        DialogCustomProgress.INSTANCE.hideProgress(activity);
                        if (text != null && !text.isEmpty())
                            if (!text.equals("0")) {
                                String shareMessage = FlatShareMessageGenerator.INSTANCE.generateFlatShareMessage(activity.myFlatData) +
                                        "\n\n" + text;
                                CommonMethods.copyToClipboard(activity, shareMessage);
                            }
                    });
        }
    }
}
