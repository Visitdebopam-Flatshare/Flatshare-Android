package com.joinflatshare.ui.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.utils.helper.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

;

public class BaseViewBinder {

    // Top bar
    public FrameLayout btn_back;
    public ImageView btn_topbar_right;
    public ImageView img_topbar_logo, img_topbar_text_header;
    public TextView txt_topbar_header;
    public CircleImageView img_topbar_profile;
    public View view_topbar_divider, view_topbar_back_circle;

    // Bottom Menu
    public LinearLayout[] ll_menu;
    public CircleImageView img_menu_profile;
    public View view_menu_chat_circle;
    private TextView[] menu_textView;

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
        view_menu_chat_circle = activity.findViewById(R.id.view_menu_chat_circle);
        LinearLayout llHolder = activity.findViewById(R.id.ll_menu_holder);
        ll_menu = new LinearLayout[]{(LinearLayout) llHolder.getChildAt(0),
                (LinearLayout) llHolder.getChildAt(1), (LinearLayout) llHolder.getChildAt(2),
                (LinearLayout) llHolder.getChildAt(3)};
        menu_textView = new TextView[]{activity.findViewById(R.id.txt_menu_explore), activity.findViewById(R.id.txt_menu_checks),
                activity.findViewById(R.id.txt_menu_chats), activity.findViewById(R.id.txt_menu_profile)};

        img_menu_profile = (CircleImageView) ll_menu[3].getChildAt(0);
        TextView txt = activity.findViewById(R.id.txt_photo);

        ImageHelper.loadProfileImage(activity, img_menu_profile, txt, AppConstants.loggedInUser);
        applyMenuClick();
        activity.baseClickListener.manageBottomMenuClicks();
    }

    public void applyMenuClick() {
        for (int i = 0; i < ll_menu.length; i++) {
            if (i == ll_menu.length - 1) {
                if (i == AppConstants.menuSelected)
                    menu_textView[i].setTextColor(ContextCompat.getColor(FlatShareApplication.instance, R.color.blue));
                else
                    menu_textView[i].setTextColor(ContextCompat.getColor(FlatShareApplication.instance, R.color.grey2));
            } else {
                if (i == AppConstants.menuSelected) {
                    ((ImageView) ll_menu[i].getChildAt(0)).setColorFilter(ContextCompat.getColor(FlatShareApplication.instance, R.color.blue));
                    menu_textView[i].setTextColor(ContextCompat.getColor(FlatShareApplication.instance, R.color.blue));
                } else {
                    ((ImageView) ll_menu[i].getChildAt(0)).setColorFilter(0);
                    menu_textView[i].setTextColor(ContextCompat.getColor(FlatShareApplication.instance, R.color.grey2));
                }
            }
        }
        view_menu_chat_circle.setVisibility(SendBirdConstants.unreadChannelCount == 0 ? View.GONE : View.VISIBLE);
    }
}
