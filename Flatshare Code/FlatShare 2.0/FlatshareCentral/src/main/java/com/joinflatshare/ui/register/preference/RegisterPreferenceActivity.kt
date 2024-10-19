package com.joinflatshare.ui.register.preference

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterPreferenceBinding
import com.joinflatshare.db.daos.AppDao
import com.joinflatshare.ui.register.RegisterBaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class RegisterPreferenceActivity : RegisterBaseActivity() {
    lateinit var viewBind: ActivityRegisterPreferenceBinding
    lateinit var listener: RegisterPreferenceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityRegisterPreferenceBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        MixpanelUtils.onScreenOpened("Onboarding Preference")
        FlatShareApplication.getDbInstance().appDao().insert(AppDao.ONBOARDING_SCREEN_PROGRESS, "6")
        listener = RegisterPreferenceListener(this, viewBind)
    }

    fun setRoomType() {
        viewBind.txtPrefFlatPrivateRoom.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.txtPrefFlatSharedRoom.background =
            ContextCompat.getDrawable(this, R.drawable.drawable_button_grey_stroke)
        viewBind.txtPrefFlatPrivateRoom.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        viewBind.txtPrefFlatSharedRoom.setTextColor(
            ContextCompat.getColor(this, R.color.black)
        )
        when (listener.roomType) {
            "Private Room" -> {
                viewBind.txtPrefFlatPrivateRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.txtPrefFlatPrivateRoom.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }

            "Shared Room" -> {
                viewBind.txtPrefFlatSharedRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_blue)
                viewBind.txtPrefFlatSharedRoom.setTextColor(
                    ContextCompat.getColor(this, R.color.white)
                )
            }
        }
    }
}