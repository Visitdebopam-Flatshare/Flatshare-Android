package com.joinflatshare.ui.bottomsheet

import androidx.activity.ComponentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.joinflatshare.utils.logger.Logger
import com.joinflatshare.utils.logger.Logger.log

/**
 * Created by debopam on 21/09/24
 */
open class BottomSheetBaseView(private val activity: ComponentActivity) {

    fun showDialog(dialog: BottomSheetDialog) {
        try {
            if (!activity.isDestroyed && !activity.isFinishing && !dialog.isShowing) dialog.show()
        } catch (exception: Exception) {
            if (exception.message != null) log(exception.message, Logger.LOG_TYPE_ERROR)
            else log("Bottom Sheet Dialog show failed", Logger.LOG_TYPE_ERROR)
        }
    }

    fun dismissDialog(dialog: BottomSheetDialog) {
        try {
            if (!activity.isDestroyed && !activity.isFinishing && dialog.isShowing) dialog.dismiss()
        } catch (exception: Exception) {
            if (exception.message != null) log(exception.message, Logger.LOG_TYPE_ERROR)
            else log("Bottom Sheet Dialog hide failed", Logger.LOG_TYPE_ERROR)
        }
    }
}