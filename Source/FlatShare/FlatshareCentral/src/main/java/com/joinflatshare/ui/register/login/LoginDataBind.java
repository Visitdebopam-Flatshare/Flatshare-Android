package com.joinflatshare.ui.register.login;

import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.utils.helper.CommonMethods;

public class LoginDataBind {
    private final LoginActivity activity;

    public LoginDataBind(LoginActivity activity) {
        this.activity = activity;
        bind();
    }

    private void bind() {
        if (!AppConstants.isAppLive)
//            activity.viewBind.edtLogin.setText("9832394089");
            activity.viewBind.edtLogin.setText("8169533929");
//            activity.viewBind.edtLogin.setText("6303546278");
//            activity.viewBind.edtLogin.setText("9263471358");

    }

    boolean validate() {
        if (activity.viewBind.edtLogin.getText().toString().trim().isEmpty()
                || activity.viewBind.edtLogin.getText().toString().length() != 10)
            CommonMethods.makeToast( "Please enter a valid mobile number");
        else return true;
        return false;
    }
}
