package com.joinflatshare.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFaqBinding;
import com.joinflatshare.ui.dialogs.theme.DialogTheme;
import com.joinflatshare.utils.helper.CommonMethod;

public class SettingsListener implements View.OnClickListener {
    private SettingsActivity activity;
    private ActivityFaqBinding viewBind;

    public SettingsListener(SettingsActivity activity, ActivityFaqBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        manageClicks();
    }

    private void manageClicks() {
        activity.baseViewBinder.img_topbar_profile.setOnClickListener(this);
        /*viewBind.txtProfilePrivacy.setOnClickListener(this);
        viewBind.txtProfileTerms.setOnClickListener(this);
        viewBind.txtProfileLogout.setOnClickListener(this);
        viewBind.rlProfileTheme.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View view) {
        /*int id = view.getId();
        if (id == R.id.txt_profile_logout) {
            CommonMethod.INSTANCE.logout(activity);
        } else if (id == activity.baseViewBinder.img_topbar_profile.getId())
            activity.onBackPressed();
        else if (id == viewBind.rlProfileTheme.getId()) {
            new DialogTheme(activity);
        } else if (id == viewBind.txtProfileTerms.getId()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.joinflatshare.com/terms.php"));
            activity.startActivity(browserIntent);
        } else if (id == viewBind.txtProfilePrivacy.getId()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.joinflatshare.com/privacy.php"));
            activity.startActivity(browserIntent);
        }*/
    }
}
