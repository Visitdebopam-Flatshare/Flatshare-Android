package com.joinflatshare.ui.register.about

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityRegisterAboutBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_LOCATION
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.Loc
import com.joinflatshare.pojo.user.ModelLocation
import com.joinflatshare.pojo.user.UserResponse
import com.joinflatshare.ui.register.preference.RegisterPreferenceActivity
import com.joinflatshare.utils.google.AutoCompletePlaces.getPlaces
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 04/02/24
 */
class RegisterAboutListener(
    private val activity: RegisterAboutActivity,
    private val viewBind: ActivityRegisterAboutBinding
) : OnClickListener {
    var edt_profile: Array<TextView> = arrayOf(
        viewBind.edtProfileCollege,
        viewBind.edtProfileHometown
    )
    var imgSearch: Array<ImageView> = arrayOf(
        viewBind.imgSearch1,
        viewBind.imgSearch2
    )
    var latProfile = arrayOfNulls<Loc>(2)

    init {
        viewBind.btnBack.setOnClickListener(this)
        viewBind.btnAboutNext.setOnClickListener(this)
        viewBind.btnSkip.setOnClickListener(this)
        countStatusLength()
        for (i in edt_profile.indices) {
            val position: Int = i
            edt_profile[i].setOnClickListener {
                imgSearch[position].setImageResource(R.drawable.ic_search)
                imgSearch[position].setColorFilter(
                    ContextCompat.getColor(
                        activity,
                        R.color.blue_dark
                    )
                )
                getPlaces(
                    activity
                ) { intent: Intent?, requestCode: Int ->
                    if (requestCode == REQUEST_CODE_LOCATION) {
                        imgSearch[position].setImageResource(R.drawable.ic_search)
                        if (intent != null) {
                            val location = CommonMethod.getSerializable(
                                intent,
                                "location",
                                ModelLocation::class.java
                            )
                            edt_profile[position].text = location.name
                            latProfile[position] = location.loc
                            checkButtonClickable()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBind.btnBack.id -> {
                CommonMethod.finishActivity(activity)
            }

            viewBind.btnAboutNext.id -> {
                if ((viewBind.edtProfileWork.visibility == View.VISIBLE &&
                            viewBind.edtProfileWork.text.toString().trim().isEmpty())
                    && latProfile[0] == null
                    && latProfile[1] == null
                )
                    return
                val user = AppConstants.loggedInUser
                // Mandatory Checks
                if (viewBind.edtProfileWork.visibility == View.VISIBLE &&
                    viewBind.edtProfileWork.text.toString().trim().isNotEmpty()
                ) {
                    user?.work = viewBind.edtProfileWork.text.toString().trim()
                }
                if (latProfile[0] != null) {
                    val loc = Loc()
                    loc.coordinates.add(latProfile[0]?.coordinates!![0])
                    loc.coordinates.add(latProfile[0]?.coordinates!![1])
                    user?.college = ModelLocation(edt_profile[0].getText().toString(), loc)
                } else if (latProfile[1] != null) {
                    val loc = Loc()
                    loc.coordinates.add(latProfile[1]?.coordinates!![0])
                    loc.coordinates.add(latProfile[1]?.coordinates!![1])
                    user?.hometown = ModelLocation(edt_profile[1].getText().toString(), loc)
                }
                activity.updateUser(user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        MixpanelUtils.onButtonClicked("Onboarding About Updated")
                        val intent = Intent(activity, RegisterPreferenceActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                })
            }

            viewBind.btnSkip.id -> {
                val user = AppConstants.loggedInUser
                user?.work = ""
                user?.college = ModelLocation()
                user?.hometown = ModelLocation()
                activity.updateUser(user, object : OnUserFetched {
                    override fun userFetched(resp: UserResponse?) {
                        MixpanelUtils.onButtonClicked("Onboarding About Skipped")
                        val intent = Intent(activity, RegisterPreferenceActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                })
            }
        }
    }

    private fun checkButtonClickable() {
        if ((viewBind.edtProfileWork.visibility == View.VISIBLE &&
                    viewBind.edtProfileWork.text.toString().trim().isEmpty())
            && latProfile[0] == null
            && latProfile[1] == null
        ) {
            viewBind.btnAboutNext.background = ContextCompat.getDrawable(
                activity,
                R.drawable.drawable_button_light_blue
            )
        } else {
            viewBind.btnAboutNext.background = ContextCompat.getDrawable(
                activity,
                R.drawable.drawable_button_blue
            )
        }
    }

    private fun countStatusLength() {
        viewBind.edtProfileWork.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                checkButtonClickable()
                activity.workLimit()
            }
        })
    }
}