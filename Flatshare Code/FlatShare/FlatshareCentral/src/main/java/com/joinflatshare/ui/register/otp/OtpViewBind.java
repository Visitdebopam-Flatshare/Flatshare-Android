package com.joinflatshare.ui.register.otp;

import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.card.MaterialCardView;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.customviews.CustomButton;

public class OtpViewBind {
    private OtpActivity activity;
    EditText[] edt_otp;
    MaterialCardView ll_otp_back, ll_otp_holder;
    LinearLayout otp_resend, ll_otp;
    CustomButton otp_confirm;

    public OtpViewBind(OtpActivity activity) {
        this.activity = activity;
        bind();
        setDemoCredential();
    }

    private void bind() {
        otp_resend = activity.findViewById(R.id.otp_resend);
        ll_otp_back = activity.findViewById(R.id.ll_otp_back);
        otp_confirm = activity.findViewById(R.id.otp_confirm);
        ll_otp = activity.findViewById(R.id.ll_otp);
        ll_otp_holder = activity.findViewById(R.id.ll_otp_holder);
        edt_otp = new EditText[6];
        edt_otp[0] = activity.findViewById(R.id.edt_otp_1);
        edt_otp[1] = activity.findViewById(R.id.edt_otp_2);
        edt_otp[2] = activity.findViewById(R.id.edt_otp_3);
        edt_otp[3] = activity.findViewById(R.id.edt_otp_4);
        edt_otp[4] = activity.findViewById(R.id.edt_otp_5);
        edt_otp[5] = activity.findViewById(R.id.edt_otp_6);
        ll_otp.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_otp.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = ll_otp.getWidth();
                ll_otp.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
                new Handler().postDelayed(() -> fadein(ll_otp_holder), 500);
            }
        });
    }

    private void setDemoCredential() {
        if (!AppConstants.isAppLive) {
            edt_otp[0].setText("2");
            edt_otp[1].setText("3");
            edt_otp[2].setText("6");
            edt_otp[3].setText("8");
            edt_otp[4].setText("5");
            edt_otp[5].setText("4");
        }
    }

    private void fadein(View contentView) {
        contentView.setVisibility(View.VISIBLE);
        Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.bubble_in);
        contentView.startAnimation(expandIn);
    }

}
