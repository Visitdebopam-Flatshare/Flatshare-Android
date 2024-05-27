package com.joinflatshare.ui.settings;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatshareCentral.databinding.ActivityFaqBinding;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.faq.FaqAdapter;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {
    private ActivityFaqBinding viewBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        showTopBar(this, true, "Settings", 0, 0);
        new SettingsViewBind(this, viewBind);
        new SettingsListener(this, viewBind);
        MixpanelUtils.INSTANCE.onButtonClicked("FAQ");
    }
}
