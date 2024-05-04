package com.joinflatshare.utils.system

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.joinflatshare.constants.ThemeConstants

class ThemeUtils {

    companion object {
        fun getTheme(activity: Activity): Int {
            val prefs = Prefs.getInstance()
            var theme = prefs.getString(Prefs.PREFS_KEY_APPEARANCE)
            var mode = 0
            if (theme.isEmpty()) {
                theme =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ThemeConstants.THEME_SYSTEM else ThemeConstants.THEME_LIGHT
            }
            if (theme == ThemeConstants.THEME_SYSTEM) {
                prefs.setString(Prefs.PREFS_KEY_APPEARANCE, ThemeConstants.THEME_SYSTEM)
                when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> mode = AppCompatDelegate.MODE_NIGHT_YES
                    Configuration.UI_MODE_NIGHT_NO -> mode = AppCompatDelegate.MODE_NIGHT_NO
                }
            } else {
                mode =
                    if (theme == ThemeConstants.THEME_LIGHT) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            }
            return mode
        }

        fun isNowInDarkMode(activity: Activity): Boolean {
            return getTheme(activity) == AppCompatDelegate.MODE_NIGHT_YES
        }

        fun isNowInLightMode(activity: Activity): Boolean {
            return getTheme(activity) == AppCompatDelegate.MODE_NIGHT_NO
        }

        fun getCurrentTheme(activity: Activity): String {
            return if (getTheme(activity) == AppCompatDelegate.MODE_NIGHT_NO) ThemeConstants.THEME_LIGHT else ThemeConstants.THEME_DARK
        }
    }

}