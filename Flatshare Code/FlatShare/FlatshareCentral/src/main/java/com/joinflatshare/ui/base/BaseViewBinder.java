package com.joinflatshare.ui.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.utils.helper.ImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;

;

public class BaseViewBinder {

    // Top bar
    public FrameLayout btn_back;
    public ImageView btn_topbar_right;
    public ImageView img_topbar_logo, img_topbar_text_header;
    public TextView txt_topbar_header;
    public RoundedImageView img_topbar_profile;
    public View view_topbar_divider, view_topbar_back_circle;

    // Bottom Menu
    public LinearLayout[] ll_menu;
    public RoundedImageView img_menu_profile;
    public View view_menu_explore_circle, view_menu_chat_circle;
    private final int[] icon = {R.drawable.ic_menu_explore, R.drawable.ic_menu_chat, R.drawable.ic_notification};
    private final int[] iconSelected = {R.drawable.ic_menu_explore_selected, R.drawable.ic_menu_chat_selected, R.drawable.ic_notification_black};
    protected TextView txt_count_notification;
    protected FrameLayout frame_count_notification;

    protected void showBack(Activity activity) {
        btn_back = activity.findViewById(R.id.img_topbar_back);
        btn_topbar_right = activity.findViewById(R.id.btn_topbar_right);
        txt_topbar_header = activity.findViewById(R.id.txt_topbar_header);
        img_topbar_logo = activity.findViewById(R.id.img_topbar_logo);
        img_topbar_text_header = activity.findViewById(R.id.img_topbar_text_header);
        img_topbar_profile = activity.findViewById(R.id.img_topbar_profile);
        view_topbar_divider = activity.findViewById(R.id.view_topbar_divider);
        view_topbar_back_circle = activity.findViewById(R.id.view_topbar_back_circle);
    }

    protected void initBottomMenu(BaseActivity activity) {
        view_menu_explore_circle = activity.findViewById(R.id.view_menu_explore_circle);
        view_menu_chat_circle = activity.findViewById(R.id.view_menu_chat_circle);
        txt_count_notification = activity.findViewById(R.id.txt_count_notification);
        frame_count_notification = activity.findViewById(R.id.frame_count_notification);
        LinearLayout llHolder = activity.findViewById(R.id.ll_menu_holder);
        ll_menu = new LinearLayout[]{(LinearLayout) llHolder.getChildAt(0),
                (LinearLayout) llHolder.getChildAt(1), (LinearLayout) llHolder.getChildAt(2),
                (LinearLayout) llHolder.getChildAt(3)};
        img_menu_profile = (RoundedImageView) ll_menu[3].getChildAt(0);

        img_menu_profile.setImageResource(R.drawable.ic_user);
        if (AppConstants.loggedInUser != null)
            ImageHelper.loadImage(activity, R.drawable.ic_user, img_menu_profile,
                    ImageHelper.getProfileImageWithAwsFromPath(AppConstants.loggedInUser.getDp()));
        applyMenuClick();
        activity.baseClickListener.manageBottomMenuClicks();
    }

    public void applyMenuClick() {
        for (int i = 0; i < ll_menu.length; i++) {
            if (i == ll_menu.length - 1) {
                if (i == AppConstants.menuSelected)
                    ((RoundedImageView) ll_menu[i].getChildAt(0)).setBorderWidth(4f);
                else
                    ((RoundedImageView) ll_menu[i].getChildAt(0)).setBorderWidth(2f);
            } else {
                if (i == AppConstants.menuSelected)
                    ((ImageView) ll_menu[i].getChildAt(0)).setImageResource(iconSelected[i]);
                else
                    ((ImageView) ll_menu[i].getChildAt(0)).setImageResource(icon[i]);
            }
        }
        view_menu_chat_circle.setVisibility(SendBirdConstants.unreadChannelCount == 0 ? View.GONE : View.VISIBLE);
    }
}
