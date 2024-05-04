package com.joinflatshare.customviews.alert;

import android.view.View;

import androidx.activity.ComponentActivity;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.DialogAlertBinding;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;

public class AlertDialog {
    public static void showAlert(ComponentActivity activity, String title,
                                 String message, String blueButton, String blackButton, OnUiEventClick onUiEventClick) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.DialogTheme);
        DialogAlertBinding viewBind = DialogAlertBinding.inflate(activity.getLayoutInflater());
        builder.setView(viewBind.getRoot());
        builder.setCancelable(false);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Header
        if (title.isEmpty())
            viewBind.txtAlertHeader.setVisibility(View.GONE);
        else {
            viewBind.txtAlertHeader.setVisibility(View.VISIBLE);
            viewBind.txtAlertHeader.setText(title);
        }

        // DESC
        if (message.isEmpty())
            viewBind.txtAlertDesc.setVisibility(View.GONE);
        else {
            viewBind.txtAlertDesc.setVisibility(View.VISIBLE);
            viewBind.txtAlertDesc.setText(message);
        }

        // Button
        viewBind.btnAlertBlue.setText(blueButton);
        viewBind.btnAlertBlue.setOnClickListener(view -> {
            CommonMethods.dismissDialog(activity, dialog);
            if (onUiEventClick != null)
                onUiEventClick.onClick(null, 1);

        });

        if (blackButton.isEmpty())
            viewBind.btnAlertBlack.setVisibility(View.GONE);
        else {
            viewBind.btnAlertBlack.setVisibility(View.VISIBLE);
            viewBind.btnAlertBlack.setText(blackButton);
            viewBind.btnAlertBlack.setOnClickListener(view -> {
                CommonMethods.dismissDialog(activity, dialog);
                if (onUiEventClick != null)
                    onUiEventClick.onClick(null, 0);
            });
        }
        CommonMethods.showDialog(activity, dialog);
    }

    public static void showAlert(ComponentActivity activity, String message, String blueButton) {
        showAlert(activity, "", message, blueButton, "", null);
    }

    public static void showAlert(ComponentActivity activity, String message) {
        showAlert(activity, "", message, "OK", "", null);
    }

    public static void showAlert(ComponentActivity activity, String message, String blueButton, OnUiEventClick onUiEventClick) {
        showAlert(activity, "", message, blueButton, "", onUiEventClick);
    }
}
