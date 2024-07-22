package com.joinflatshare.services

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.provider.ContactsContract
import android.text.format.DateUtils
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.constants.ContactConstants
import com.joinflatshare.constants.IntentFilterConstants
import com.joinflatshare.customviews.alert.AlertImageDialog
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.ApplicationBaseActivity
import com.joinflatshare.utils.helper.CommonMethods
import java.util.Timer
import java.util.TimerTask

/**
 * Created by debopam on 24/04/23
 */
object MutualContactHandler {
    private const val DELAY = DateUtils.SECOND_IN_MILLIS * 5
    private val TAG = MutualContactHandler::class.java.simpleName
    private val timer = Timer()
    private var isJobRunning = false
    private lateinit var activity: ApplicationBaseActivity

    fun scheduleContactHandler(activity: ApplicationBaseActivity) {
        this.activity = activity
        if (ContactConstants.allContacts.isEmpty()) {
            if (activity.checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    timer.schedule(timerTask, DELAY)
                } catch (ex: Exception) {
                    AlertImageDialog.somethingWentWrong(activity)
                }
            }
        }
    }

    fun isJobRunning(): Boolean {
        if (isJobRunning)
            return true
        else {
            timer.cancel()
            return false
        }
    }

    private val timerTask = object : TimerTask() {
        override fun run() {
            CommonMethods.makeLog(TAG, "Fetching all contacts")
            Thread().run {
                isJobRunning = true
                ContactConstants.allContacts.clear()
                getContacts(activity)
            }
        }
    }

    @SuppressLint("Range")
    fun getContacts(activity: ApplicationBaseActivity) {
        object : AsyncTask<Any?, Any?, Any?>() {
            var cur: Cursor? = null
            override fun onPostExecute(result: Any?) {
                super.onPostExecute(result)
                cur?.close()
                isJobRunning = false
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(IntentFilterConstants.INTENT_FILTER_CONSTANT_CONTACTS_LOADED))
                CommonMethods.makeLog(TAG, "All Contacts Fetched")
            }

            override fun doInBackground(vararg p0: Any?): Any? {
                val cr = FlatShareApplication.instance.contentResolver
                cur = cr.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null
                )
                if (cur != null) {
                    val count = cur?.count!!

                    if (count > 0) {
                        while (cur!!.moveToNext()) {
                            val id = cur!!.getString(
                                cur!!.getColumnIndex(ContactsContract.Contacts._ID)
                            )
                            val name = cur!!.getString(
                                cur!!.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME
                                )
                            )
                            if (cur!!.getInt(
                                    cur!!.getColumnIndex(
                                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                                    )
                                ) > 0
                            ) {
                                val pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    arrayOf(id),
                                    null
                                )
                                while (pCur!!.moveToNext()) {
                                    val phoneNo = pCur.getString(
                                        pCur.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER
                                        )
                                    )
                                    if (phoneNo.length >= 10) {
                                        val ph: String = checkNumberDigits(trimNumber(phoneNo))
                                        val user = User()
                                        val name1 = Name()
                                        name1.firstName = name
                                        user.name = name1
                                        user.id = ph
                                        ContactConstants.allContacts.add(user)
                                    }
                                }
                                pCur.close()
                            }
                        }
                    }

                }
                return null
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)


    }

    private fun trimNumber(number: String): String {
        var w = ""
        for (i in number.indices) {
            if (number[i] != ' ') w += number[i]
        }
        return checkNumberDigits(w)
    }

    private fun checkNumberDigits(number: String): String {
        var number = number
        if (number.length > 10) {
            val extraDigits = number.length - 10
            number = number.substring(extraDigits)
        }
        return number
    }

}