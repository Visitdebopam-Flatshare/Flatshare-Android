package com.joinflatshare.ui.register.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.register.RegisterBaseActivity;
import com.joinflatshare.utils.helper.CommonMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpActivity extends RegisterBaseActivity {
    private OtpViewBind viewBind;
    String phone;
    protected OtpApiController apiController;
    protected UserResponse userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        phone = getIntent().getStringExtra("phone");
        init();
    }

    private void init() {
        viewBind = new OtpViewBind(this);
        new OtpListener(this, viewBind);
        apiController = new OtpApiController(this);
        if (AppConstants.isAppLive)
            apiController.sendOtp();
    }

    void initReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("valid", false)) {
                    String msgBody = intent.getStringExtra("message");
                    CommonMethod.INSTANCE.makeLog("Message Broadcast", msgBody);
                    Pattern pattern = Pattern.compile("(|^)\\d{6}");
                    if (msgBody != null) {
                        Matcher matcher = pattern.matcher(msgBody);
                        if (matcher.find()) {
                            String otp = matcher.group(0);
                            CommonMethod.INSTANCE.makeLog("OTP", otp);
                            if (viewBind != null) {
                                viewBind.edt_otp[0].setText("" + otp.charAt(0));
                                viewBind.edt_otp[1].setText("" + otp.charAt(1));
                                viewBind.edt_otp[2].setText("" + otp.charAt(2));
                                viewBind.edt_otp[3].setText("" + otp.charAt(3));
                                viewBind.edt_otp[4].setText("" + otp.charAt(4));
                                viewBind.edt_otp[5].setText("" + otp.charAt(5));
                                viewBind.otp_confirm.performClick();
                            }
                        }
                    }
                }
            }
        };
    }

    BroadcastReceiver smsReceiver;
}