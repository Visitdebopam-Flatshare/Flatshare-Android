package com.debopam.flatshareprogress

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.joinflatshare.FlatshareProgress.R
import com.joinflatshare.FlatshareProgress.databinding.DialogProgressBinding
import kotlinx.coroutines.launch

/**
 * Created by debopam on 30/05/23
 */
object DialogCustomProgress {
    var dialog: AlertDialog? = null
    private fun initialiseProgress(activity: ComponentActivity?) {
        activity?.lifecycleScope?.launch {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.DialogTheme)
            val viewBind = DialogProgressBinding.inflate(activity.layoutInflater)
            builder.setView(viewBind.root)
            builder.setCancelable(false)
            dialog = builder.create()
        }
    }

    fun showProgress(activity: ComponentActivity) {
        if (dialog == null)
            initialiseProgress(activity)
        dialog?.let {
            if (!activity.isDestroyed && !activity.isFinishing && !dialog!!.isShowing) dialog!!.show()
        }
    }

    fun hideProgress(activity: ComponentActivity) {
        dialog?.let {
            if (!activity.isDestroyed && !activity.isFinishing && dialog!!.isShowing) dialog!!.dismiss()
        }
    }
}

