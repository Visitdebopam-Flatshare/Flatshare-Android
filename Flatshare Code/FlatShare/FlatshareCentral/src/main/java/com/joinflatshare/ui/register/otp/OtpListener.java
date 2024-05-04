package com.joinflatshare.ui.register.otp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;

public class OtpListener implements View.OnClickListener {
    private final OtpActivity activity;
    private final OtpViewBind viewBind;

    public OtpListener(OtpActivity activity, OtpViewBind viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        manageClicks();
        addTextChangedListener();
        addKeyEventListener();
    }

    private void manageClicks() {
        viewBind.otp_resend.setOnClickListener(this);
        viewBind.otp_confirm.setOnClickListener(this);
        viewBind.ll_otp_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.otp_resend) {
            CommonMethods.makeToast("OTP Resent");
        } else if (id == R.id.otp_confirm) {
            if (viewBind.otp_confirm.getText().toString().equals("Check Location"))
                activity.apiController.checkLocation(activity.phone);
            else
                validate();
        } else if (id == R.id.ll_otp_back) {
            CommonMethod.INSTANCE.finishActivity(activity);
        }
    }

    private void addKeyEventListener() {
        for (int i = 0; i < viewBind.edt_otp.length; i++) {
            final int position = i;
            viewBind.edt_otp[i].setOnKeyListener((view, i1, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i1 == KeyEvent.KEYCODE_DEL) {
                    viewBind.edt_otp[position].setText("");
                    if (position > 0) {
                        viewBind.edt_otp[position - 1].requestFocus();
                        viewBind.edt_otp[position - 1].append("");
                    }
                }
                return false;
            });
        }

    }

    private void addTextChangedListener() {
        for (int i = 0; i < viewBind.edt_otp.length; i++) {
            final int position = i;
            viewBind.edt_otp[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (position < 5) {
                        if (viewBind.edt_otp[position].length() > 0)
                            viewBind.edt_otp[position + 1].requestFocus();
                    } else {
                        if (viewBind.edt_otp[position].length() > 0) {
                            CommonMethods.hideSoftKeyboard(activity);
                        }
                    }
                }
            });
        }
    }

    private void validate() {
        StringBuilder w = new StringBuilder();
        for (EditText imEdittext : viewBind.edt_otp) {
            w.append(imEdittext.getText().toString());
        }
        if (w.toString().isEmpty())
            CommonMethods.makeToast( "Please enter OTP");
        else if (w.length() != 6)
            CommonMethods.makeToast( "Invalid OTP");
        else {
            activity.apiController.login(w.toString());
        }
    }
}
