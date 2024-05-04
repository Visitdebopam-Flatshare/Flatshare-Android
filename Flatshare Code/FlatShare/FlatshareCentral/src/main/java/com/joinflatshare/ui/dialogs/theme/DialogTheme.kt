package com.joinflatshare.ui.dialogs.theme

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.DialogThemeBinding
import com.joinflatshare.FlatshareCentral.databinding.IncludePopupHeaderWhiteBinding
import com.joinflatshare.constants.ThemeConstants
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.system.Prefs


class DialogTheme(
    private val activity: BaseActivity
) {
    val dialog: AlertDialog

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        val holder = IncludePopupHeaderWhiteBinding.inflate(activity.layoutInflater)
        val viewBind = DialogThemeBinding.inflate(activity.layoutInflater)
        holder.llPopupHolder.addView(viewBind.root)
        builder.setView(holder.root)
        builder.setCancelable(true)
        dialog = builder.create()

        // Header
        holder.txtDialogHeader.text = "Theme"
        holder.imgHeader.visibility = View.GONE
        holder.imgCross.setOnClickListener {
            CommonMethods.dismissDialog(activity, dialog)
        }

        // content
        viewBind.llThemeDefault.visibility =
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) View.VISIBLE else View.GONE)
        holder.viewBg.setOnClickListener {}
        viewBind.llThemeDefault.setOnClickListener {
            switchTheme(ThemeConstants.THEME_SYSTEM)
        }
        viewBind.llThemeLight.setOnClickListener {
            switchTheme(ThemeConstants.THEME_LIGHT)
        }
        viewBind.llThemeDark.setOnClickListener {
            switchTheme(ThemeConstants.THEME_DARK)
        }
        CommonMethods.showDialog(activity, dialog)
    }

    private fun switchTheme(theme: String) {
        if (theme == ThemeConstants.THEME_SYSTEM) {
            when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> activity.prefs.setString(
                    Prefs.PREFS_KEY_APPEARANCE,
                    ThemeConstants.THEME_DARK
                )
                Configuration.UI_MODE_NIGHT_NO -> activity.prefs.setString(
                    Prefs.PREFS_KEY_APPEARANCE,
                    ThemeConstants.THEME_LIGHT
                )
            }
        } else activity.prefs.setString(Prefs.PREFS_KEY_APPEARANCE, theme)
        CommonMethods.dismissDialog(activity, dialog)
        val intnt = Intent(activity, ExploreActivity::class.java)
        CommonMethod.switchActivity(activity, intnt, false)
        activity.finishAffinity()
    }
}