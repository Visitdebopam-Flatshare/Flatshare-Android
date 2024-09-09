package com.joinflatshare.ui.admin

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityAdminBinding
import com.joinflatshare.ui.base.BaseActivity

/**
 * Created by debopam on 23/08/24
 */
class AdminFeatureActivity : BaseActivity() {
    private lateinit var viewBind: ActivityAdminBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "Admin Features", 0, 0)
        init()
    }

    private fun init() {
        viewBind.rvAdminFeatures.layoutManager = LinearLayoutManager(this)
        val features = ArrayList<String>()
        features.add("Reload Sendbird Users")
        features.add("Reload Flatshare Users")
        features.add("Sendbird User Not Registered")
        features.add("Sendbird User Name Fix")
        features.add("Sendbird User Image Fix")
        features.add("Sendbird User Image HTTPS Fix")
        features.add("Sendbird Delete Unregistered Users")
        viewBind.rvAdminFeatures.adapter = AdminFeatureAdapter(this, features)
    }
}