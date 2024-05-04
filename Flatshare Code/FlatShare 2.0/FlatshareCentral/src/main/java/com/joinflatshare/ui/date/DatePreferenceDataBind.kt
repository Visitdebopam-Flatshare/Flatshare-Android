package com.joinflatshare.ui.date

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityDatePreferencesBinding
import com.joinflatshare.constants.ChatRequestConstants
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.deal_breakers.DealBreakerCallback
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.flat.DealBreakers

/**
 * Created by debopam on 14/07/23
 */
class DatePreferenceDataBind(
    private val activity: DatePreferenceActivity,
    private val viewBind: ActivityDatePreferencesBinding,
    private val listener: DatePreferenceListener
) {
    private val planList = ArrayList<String>()
    var adapter: DatePlansAdapter

    init {
        viewBind.rvDatePlans.layoutManager = GridLayoutManager(activity, 2)
        viewBind.rvDatePlans.addItemDecoration(
            GridSpacingItemDecoration(
                2, 30, false
            )
        )
        adapter = DatePlansAdapter(activity, planList, viewBind.rvDatePlans)
        viewBind.rvDatePlans.adapter = adapter
        setDealBreakers()
    }

    fun setGenderButton() {
        // Male
        viewBind.cardProfileMale.setCardBackgroundColor(
            ContextCompat.getColor(activity, android.R.color.transparent)
        )
        viewBind.txtMale.setTextColor(
            ContextCompat.getColor(activity, R.color.black)
        )
        // Female
        viewBind.cardProfileFemale.setCardBackgroundColor(
            ContextCompat.getColor(activity, android.R.color.transparent)
        )
        viewBind.txtFemale.setTextColor(
            ContextCompat.getColor(activity, R.color.black)
        )
        activity.user?.dateProperties?.gender = ""
        if (listener.isMaleSelected && listener.isFemaleSelected) {
            viewBind.cardProfileMale.setCardBackgroundColor(
                ContextCompat.getColor(activity, R.color.button_bg_black)
            )
            viewBind.txtMale.setTextColor(
                ContextCompat.getColor(activity, R.color.color_text_secondary)
            )
            viewBind.cardProfileFemale.setCardBackgroundColor(
                ContextCompat.getColor(activity, R.color.button_bg_black)
            )
            viewBind.txtFemale.setTextColor(
                ContextCompat.getColor(activity, R.color.color_text_secondary)
            )
            activity.user?.dateProperties?.gender = "Both"
        } else {
            if (listener.isMaleSelected) {
                viewBind.cardProfileMale.setCardBackgroundColor(
                    ContextCompat.getColor(activity, R.color.button_bg_black)
                )
                viewBind.txtMale.setTextColor(
                    ContextCompat.getColor(activity, R.color.color_text_secondary)
                )
                activity.user?.dateProperties?.gender = "Male"
            }
            if (listener.isFemaleSelected) {
                viewBind.cardProfileFemale.setCardBackgroundColor(
                    ContextCompat.getColor(activity, R.color.button_bg_black)
                )
                viewBind.txtFemale.setTextColor(
                    ContextCompat.getColor(activity, R.color.color_text_secondary)
                )
                activity.user?.dateProperties?.gender = "Female"
            }
        }
        viewBind.cardProfileMale.invalidate()
        viewBind.cardProfileFemale.invalidate()
    }

    fun setDateButton() {
        viewBind.cardDateCasual.strokeWidth = 0
        viewBind.cardDateLongTerm.strokeWidth = 0
        viewBind.cardDatePartners.strokeWidth = 0
        viewBind.cardDateCasual.strokeColor =
            ContextCompat.getColor(activity, android.R.color.transparent)
        viewBind.cardDateLongTerm.strokeColor =
            ContextCompat.getColor(activity, android.R.color.transparent)
        viewBind.cardDatePartners.strokeColor =
            ContextCompat.getColor(activity, android.R.color.transparent)

        val txt1 = viewBind.llDatePlans.getChildAt(0) as TextView
        val txt2 = viewBind.llDatePlans.getChildAt(1) as TextView

        when (listener.dateType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL -> {
                viewBind.cardDateCasual.strokeWidth = 8
                viewBind.cardDateCasual.strokeColor =
                    ContextCompat.getColor(activity, R.color.color_date_casual)
                txt1.text = "Plans ("
                val plans = activity.user?.dateProperties?.plans
                if (plans == null)
                    txt2.text = "0"
                else txt2.text = "" + plans.size
                adapter.setPlansUrl()
                populatePlanList()
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM -> {
                viewBind.cardDateLongTerm.strokeWidth = 8
                viewBind.cardDateLongTerm.strokeColor =
                    ContextCompat.getColor(activity, R.color.color_date_long_term)
                txt1.text = "Plans ("
                val plans = activity.user?.dateProperties?.plans
                if (plans == null)
                    txt2.text = "0"
                else txt2.text = "" + plans.size
                adapter.setPlansUrl()
                populatePlanList()
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                viewBind.cardDatePartners.strokeWidth = 8
                viewBind.cardDatePartners.strokeColor =
                    ContextCompat.getColor(activity, R.color.color_date_activity_partners)
                txt1.text = "Activities ("
                val activities = activity.user?.dateProperties?.activities
                if (activities == null)
                    txt2.text = "0"
                else txt2.text = "" + activities.size
                adapter.setActivityUrl()
                populatePlanList()
            }
        }
        activity.user?.dateProperties?.dateType = listener.dateType
    }

    private fun setDealBreakers() {
        val dealBreakerView = DealBreakerView(activity, viewBind.rvDateDeals)
        val callback = object : DealBreakerCallback {
            override fun onSmokingSelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.smoking = constant
            }

            override fun onNonVegSelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.nonveg = constant
            }

            override fun onPartySelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.flatparty = constant
            }

            override fun onEggsSelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.eggs = constant
            }

            override fun onWorkoutSelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.workout = constant
            }

            override fun onPetsSelected(constant: Int) {
                if (activity.user?.dateProperties?.dealBreakers == null)
                    activity.user?.dateProperties?.dealBreakers = DealBreakers()
                activity.user?.dateProperties?.dealBreakers?.pets = constant
            }
        }

        dealBreakerView.setDealValues(activity.user?.dateProperties, null)
        dealBreakerView.assignCallback(callback)
        dealBreakerView.show()
    }

    private fun getPlansData() {
        planList.clear()
        val configResponse = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        val plans = configResponse?.data?.dating?.plans
        if (!plans.isNullOrEmpty()) {
            planList.addAll(plans)
            adapter.setMatchedItems(activity.user?.dateProperties?.plans)
        }

    }

    private fun getActivitiesData() {
        planList.clear()
        val configResponse = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        val activities = configResponse?.data?.dating?.activities
        if (!activities.isNullOrEmpty()) {
            planList.addAll(activities)
            adapter.setMatchedItems(activity.user?.dateProperties?.activities)
        }
    }

    fun populatePlanList() {
        when (listener.dateType) {
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_CASUAL,
            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_LONG_TERM -> {
                getPlansData()
            }

            ChatRequestConstants.CHAT_REQUEST_CONSTANT_DATE_ACTIVITY_PARTNERS -> {
                getActivitiesData()
            }
        }
        adapter.notifyDataSetChanged()
    }
}