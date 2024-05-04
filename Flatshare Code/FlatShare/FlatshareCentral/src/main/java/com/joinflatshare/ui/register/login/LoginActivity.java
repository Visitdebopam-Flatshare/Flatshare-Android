package com.joinflatshare.ui.register.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.joinflatshare.FlatshareCentral.databinding.ActivityLoginBinding;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.ui.register.RegisterBaseActivity;
import com.joinflatshare.ui.register.otp.OtpActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.system.ConfigManager;

public class LoginActivity extends RegisterBaseActivity {
    protected ActivityLoginBinding viewBind;
    private LoginDataBind dataBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        init();
    }

    private void init() {
        dataBind = new LoginDataBind(this);
        viewBind.txtLoginTerms.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.joinflatshare.com/terms.php"))));
        viewBind.txtLoginPrivacy.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.joinflatshare.com/privacy.php"))));
        viewBind.btnLogin.setOnClickListener(view -> {
            if (dataBind.validate()) {
                ConfigManager configManager = new ConfigManager(this);
                if (configManager.isUserBlocked(viewBind.edtLogin.getText().toString().trim())) {
                    AlertDialog.showAlert(
                            this,
                            "Your account is restricted. If this looks like a mistake, please reach out to us at\nhello@flatshare.club.",
                            "OK");
                } else {
                    Intent intent = new Intent(this, OtpActivity.class);
                    intent.putExtra("phone", viewBind.edtLogin.getText().toString().trim());
                    CommonMethod.INSTANCE.switchActivity(this, intent, false);
                    MixpanelUtils.INSTANCE.numberEntered(viewBind.edtLogin.getText().toString().trim());
                }
            }
        });
    }
}