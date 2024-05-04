package com.joinflatshare.utils.helper;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.interfaces.OnSnackbarDismissed;
import com.joinflatshare.pojo.flat.FlatProperties;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.utils.logger.Logger;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class CommonMethods {

    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception ignored) {
        }
        return version;
    }

    public static void makeLog(String tag, String message) {
        if (!AppConstants.isAppLive) {
            Log.e(tag, message);
        }
    }

    public static void hideSoftKeyboard(Activity context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ex) {
        }
    }

    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static void clickDisable(View view) {
        view.setClickable(false);
        view.setEnabled(false);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                clickDisable(group.getChildAt(idx));
            }
        }
    }

    public static void registerUser(UserResponse userResponse) {
        FlatShareApplication.Companion.getDbInstance().userDao().updateUserResponse(userResponse);
        AppConstants.loggedInUser = userResponse.getData();
        FirebaseCrashlytics.getInstance().setUserId(AppConstants.loggedInUser.getId());
        // Mixpanel
        MixpanelUtils.INSTANCE.identity(AppConstants.loggedInUser);
    }

    public static void copyToClipboard(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getPackageName(), textToCopy);
        clipboard.setPrimaryClip(clip);
        CommonMethod.INSTANCE.makeToast("Copied");
    }

    /**
     * To show toast message
     *
     * @param activity            object of activity
     * @param message             The message to show in toast
     * @param onSnackbarDismissed The callback on snackbar dismissal
     */
    public static void makeSnack(Activity activity, boolean disableViewClick,
                                 String message, final OnSnackbarDismissed onSnackbarDismissed) {
        if (disableViewClick)
            clickDisable(activity.findViewById(R.id.drawer_layout));
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_snack, null);

        TextView text = layout.findViewById(R.id.txt_toast);
        text.setText(message);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
        new Handler().postDelayed(() -> onSnackbarDismissed.onDismissed(), 2000);
    }


    public static void makeSnack(Activity activity, boolean disableViewClick, String message) {
        if (disableViewClick)
            clickDisable(activity.findViewById(R.id.drawer_layout));
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_snack, null);

        TextView text = layout.findViewById(R.id.txt_toast);
        text.setText(message);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        return (Patterns.WEB_URL.matcher(url).find());
    }

    public static boolean isFlatComplete(MyFlatData flatData) {
        if (!AppConstants.isAppLive)
            return true;
        if (flatData.isVerified())
            //Verify screen should be accessible when at least 2 images,
            // flat type, room type, flat location, rentpp, gender, move-in date is filled.
            if (flatData.getImages().size() >= 2 && flatData.getFlatProperties() != null) {
                FlatProperties properties = flatData.getFlatProperties();
                if (properties.getFlatsize() != null && !properties.getFlatsize().isEmpty()
                        && properties.getRoomType() != null && !properties.getRoomType().isEmpty()
                        && properties.getLocation() != null && properties.getLocation().getLoc() != null
                        && properties.getLocation().getLoc().getCoordinates() != null && properties.getLocation().getLoc().getCoordinates().size() == 2
                        && properties.getRentperPerson() > 0
                        && properties.getGender() != null && !properties.getGender().isEmpty()
                        && properties.getMoveinDate() != null && !properties.getMoveinDate().isEmpty()) {
                    return true;
                }
            }
        return false;
    }

    public static void showDialog(ComponentActivity activity, AlertDialog dialog) {
        try {
            if (!activity.isDestroyed() && !activity.isFinishing() && !dialog.isShowing())
                dialog.show();
        } catch (Exception exception) {
            if (exception.getMessage() != null)
                Logger.log(exception.getMessage(), Logger.LOG_TYPE_ERROR);
            else Logger.log("Dialog show failed", Logger.LOG_TYPE_ERROR);
        }
    }

    public static void dismissDialog(ComponentActivity activity, AlertDialog dialog) {
        try {
            if (!activity.isDestroyed() && !activity.isFinishing() && dialog.isShowing())
                dialog.dismiss();
        } catch (Exception exception) {
            if (exception.getMessage() != null)
                Logger.log(exception.getMessage(), Logger.LOG_TYPE_ERROR);
            else Logger.log("Dialog dismiss failed", Logger.LOG_TYPE_ERROR);
        }
    }
}
