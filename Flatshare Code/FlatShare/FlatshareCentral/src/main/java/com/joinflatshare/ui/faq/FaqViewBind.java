package com.joinflatshare.ui.faq;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joinflatshare.FlatshareCentral.databinding.ActivityFaqBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.utils.helper.DateUtils;

;

public class FaqViewBind {
    private FaqActivity activity;
    private ActivityFaqBinding viewBind;
    protected RelativeLayout[] rl_profile_details;
    protected TextView[] txt_profile_details;

    public FaqViewBind(FaqActivity activity, ActivityFaqBinding viewBind) {
        this.activity=activity;
        this.viewBind = viewBind;
        bind();
        setData();
    }

    private void bind() {
        rl_profile_details = new RelativeLayout[]{viewBind.rlProfileName,
                viewBind.rlProfileMobile, viewBind.rlProfileBirthday,
                viewBind.rlProfileGender};
        txt_profile_details = new TextView[]{viewBind.txtProfileName,
                viewBind.txtProfileMobile, viewBind.txtProfileBirthday, viewBind.txtProfileGender};
    }

    private void setData() {
        txt_profile_details[0].setText(AppConstants.loggedInUser.getName().getFirstName());
        txt_profile_details[1].setText(AppConstants.loggedInUser.getId());
        txt_profile_details[2].setText(DateUtils.INSTANCE.getDOB(AppConstants.loggedInUser.getDob()));
        txt_profile_details[3].setText(AppConstants.loggedInUser.getGender());
    }
}
