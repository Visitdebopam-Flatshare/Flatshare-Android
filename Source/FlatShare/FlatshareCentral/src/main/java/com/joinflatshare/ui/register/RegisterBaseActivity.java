package com.joinflatshare.ui.register;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.joinflatshare.ui.base.gpsfetcher.GpsHandler;
import com.joinflatshare.utils.system.ThemeUtils;

public class RegisterBaseActivity extends GpsHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.Companion.getTheme(this));
    }
}
