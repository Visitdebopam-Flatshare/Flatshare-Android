package com.joinflatshare.ui.date

import android.os.Bundle
import android.widget.TextView
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityDatePreferencesBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 14/07/23
 */
class DatePreferenceActivity : BaseActivity() {
    private lateinit var viewBind: ActivityDatePreferencesBinding
    private lateinit var listener: DatePreferenceListener
    lateinit var dataBind: DatePreferenceDataBind
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityDatePreferencesBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Vibe Check", 0, 0)
        init()
    }

    private fun init() {
        user = FlatShareApplication.getDbInstance().userDao().getUser()
        listener = DatePreferenceListener(this, viewBind)
        dataBind = DatePreferenceDataBind(this, viewBind, listener)
        bind()
    }

    private fun bind() {
        val dating = user?.dateProperties
        if (dating != null) {
            // Date Button
            listener.dateType = dating.dateType
            dataBind.setDateButton()
            // Gender
            if (!dating.gender.isNullOrBlank()) {
                when (dating.gender) {
                    "Both" -> {
                        listener.isMaleSelected = true
                        listener.isFemaleSelected = true
                    }

                    "Male" -> {
                        listener.isMaleSelected = true
                        listener.isFemaleSelected = false
                    }

                    "Female" -> {
                        listener.isMaleSelected = false
                        listener.isFemaleSelected = true
                    }
                }
            }
            dataBind.setGenderButton()
            // Plans

            dataBind.populatePlanList()
        }
    }

    fun updatePlanCount(count: String) {
        val txt1 = viewBind.llDatePlans.getChildAt(0) as TextView
        val txt2 = viewBind.llDatePlans.getChildAt(1) as TextView
        txt2.text = count
        when (listener.dateType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM -> {
                txt1.text = "Plans ("
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                txt1.text = "Activities ("
            }
        }
    }

    override fun onBackPressed() {
        viewBind.btnDateSearch.performClick()
    }
}