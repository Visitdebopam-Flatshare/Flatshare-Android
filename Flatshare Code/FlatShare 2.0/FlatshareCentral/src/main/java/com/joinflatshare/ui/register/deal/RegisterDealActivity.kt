package com.joinflatshare.ui.register.deal

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterDealBinding
import com.joinflatshare.customviews.deal_breakers.DealBreakerCallback
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.helper.CommonMethod

class RegisterDealActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityRegisterDealBinding
    lateinit var dealBreakerView: DealBreakerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRegisterDealBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "4")
        init()
        RegisterDealListener(this, viewBind)
    }

    private fun init() {
        val deals = DealBreakers()
        dealBreakerView = DealBreakerView(this, viewBind.rvFlatDeals)
        dealBreakerView.setDealValues(deals, viewBind.rvFlatDeals)
        dealBreakerView.isEditable(true)
        dealBreakerView.assignCallback(object : DealBreakerCallback {
            override fun onSmokingSelected(constant: Int) {
                isButtonEnabled()
            }

            override fun onNonVegSelected(constant: Int) {
                isButtonEnabled()
            }

            override fun onPartySelected(constant: Int) {
                isButtonEnabled()
            }

            override fun onEggsSelected(constant: Int) {
                isButtonEnabled()
            }

            override fun onWorkoutSelected(constant: Int) {
                isButtonEnabled()
            }

            override fun onPetsSelected(constant: Int) {
                isButtonEnabled()
            }

        })
        dealBreakerView.show()


        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        if (response?.data?.allowedSkips?.isSkippingDealBreakersAllowed == false) {
            viewBind.btnSkip.visibility = View.INVISIBLE
        }
    }

    private fun isButtonEnabled() {
        val selectedDeals = dealBreakerView.getDealBreakers()
        if (selectedDeals.smoking == 0 && selectedDeals.pets == 0 && selectedDeals.eggs == 0
            && selectedDeals.flatparty == 0 && selectedDeals.nonveg == 0
            && selectedDeals.workout == 0
        ) {
            viewBind.btnDealNext.background = ContextCompat.getDrawable(
                this,
                R.drawable.drawable_button_light_blue
            )
        } else {
            viewBind.btnDealNext.background = ContextCompat.getDrawable(
                this,
                R.drawable.drawable_button_blue
            )
        }
    }
}