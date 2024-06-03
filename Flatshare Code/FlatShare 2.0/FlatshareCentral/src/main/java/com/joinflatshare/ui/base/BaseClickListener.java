package com.joinflatshare.ui.base;

import android.content.Intent;

import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.ui.chat.list.ChatListActivity;
import com.joinflatshare.ui.explore.ExploreActivity;
import com.joinflatshare.ui.checks.ChecksActivity;
import com.joinflatshare.ui.profile.myprofile.ProfileActivity;
import com.joinflatshare.utils.helper.CommonMethod;

public class BaseClickListener {
    private final BaseActivity activity;
    private final BaseViewBinder viewBinder;

    public BaseClickListener(BaseActivity activity, BaseViewBinder viewBinder) {
        this.activity = activity;
        this.viewBinder = viewBinder;
    }

    protected void manageTopbarClicks() {
        viewBinder.btn_back.setOnClickListener(v -> activity.onBackPressed());
        viewBinder.img_topbar_profile.setOnClickListener(v -> activity.onBackPressed());
    }

    protected void manageBottomMenuClicks() {
        for (int i = 0; i < viewBinder.ll_menu.length; i++) {
            final int positon = i;
            viewBinder.ll_menu[i].setOnClickListener(v -> {
                if (positon > 0 && positon == AppConstants.menuSelected)
                    return;
                Intent intent = null;
                switch (positon) {
                    case 0:
                        if (!(activity instanceof ExploreActivity)) {
                            if (positon != AppConstants.menuSelected) {
                                activity.onBackPressed();
                            }
                            return;
                        }
                        ExploreActivity act = (ExploreActivity) activity;
//                        act.viewBind.rvExplore.smoothScrollToPosition(0);
                        break;
                    case 1:
                        if (AppConstants.isSendbirdLive)
                            intent = new Intent(activity, ChatListActivity.class);
                        break;
                    case 2:
                        intent = new Intent(activity, ChecksActivity.class);
                        break;
                    case 3:
                        intent = new Intent(activity, ProfileActivity.class);
                        break;
                }
                if (intent != null) {
                    CommonMethod.INSTANCE.switchActivity(activity, intent, AppConstants.menuSelected != 0);
                }
                AppConstants.menuSelected = positon;
                viewBinder.applyMenuClick();
            });
        }
    }
}
