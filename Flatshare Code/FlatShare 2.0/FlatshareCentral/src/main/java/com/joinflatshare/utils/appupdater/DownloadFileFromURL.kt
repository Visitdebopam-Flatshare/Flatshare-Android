package com.joinflatshare.utils.appupdater

import android.os.AsyncTask
import android.os.Environment
import com.debopam.progressdialog.DialogCustomProgress
import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethod
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL

class DownloadFileFromURL(
    private val activity: ApplicationBaseActivity,
    private val url: String
) : AsyncTask<Void, Void, String>() {
    val apiManager = ApiManager(activity)
    override fun doInBackground(vararg p0: Void?): String {
        try {
            val url = URL(url)
            val connection = url.openConnection();
            connection.connect();
            val lenghtOfFile = connection.getContentLength();
            val input = BufferedInputStream(url.openStream(), 8192);
            val output = FileOutputStream(
                Environment.getExternalStorageDirectory().toString()
                        + "/flatshare.apk"
            )
            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (ex: Exception) {
            CommonMethod.makeLog("ex", ex.message)
        }
        return ""
    }


    override fun onPreExecute() {
        super.onPreExecute()
        apiManager.showProgress()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        DialogCustomProgress.hideProgress(activity);
    }
}