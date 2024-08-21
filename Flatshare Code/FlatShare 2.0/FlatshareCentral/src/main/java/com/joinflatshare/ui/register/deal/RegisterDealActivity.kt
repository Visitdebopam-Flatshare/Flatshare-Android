package com.joinflatshare.ui.register.deal

import android.os.Bundle
import android.view.View
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDealBinding
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.register.RegisterBaseActivity

class RegisterDealActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityProfileDealBinding
    lateinit var dealBreakerView: DealBreakerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityProfileDealBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        init()
        RegisterDealListener(this, viewBind)
    }

    private fun init() {
        val deals = DealBreakers()
        dealBreakerView = DealBreakerView(this, viewBind.rvFlatDeals)
        dealBreakerView.setDealValues(deals, viewBind.rvFlatDeals)
        dealBreakerView.isEditable(true)
        dealBreakerView.show()


        val response = FlatShareApplication.getDbInstance().appDao().getConfigResponse()
        if (response?.data?.allowedSkips?.isSkippingDealBreakersAllowed == false) {
            viewBind.btnSkip.visibility = View.INVISIBLE
        }
    }
}