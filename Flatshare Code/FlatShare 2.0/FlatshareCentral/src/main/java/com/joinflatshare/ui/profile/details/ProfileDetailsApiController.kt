package com.joinflatshare.ui.profile.details

import android.content.Intent
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileDetailsBinding
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.interfaces.OnUserFetched
import com.joinflatshare.pojo.user.UserResponse

/**
 * Created by debopam on 04/01/23
 */
class ProfileDetailsApiController(
    private val activity: ProfileDetailsActivity,
    private val viewBinding: ActivityProfileDetailsBinding
) {
    var callbackIntent: Intent? = null

    fun getProfile() {
        if (activity.userId == FlatShareApplication.getDbInstance().userDao()
                .get(UserDao.USER_CONSTANT_USERID)
        ) {
            activity.initUserData(FlatShareApplication.getDbInstance().userDao().getUserResponse())
        } else {
            activity.baseApiController.getUser(true, activity.userId, object : OnUserFetched {
                override fun userFetched(resp: UserResponse?) {
                    activity.userResponse = resp
                    activity.user = activity.userResponse?.data
                }
            })
        }
    }

    fun reportNotInterested() {
        /*DialogLottieViewer(activity, R.raw.lottie_not_interested, null)
        activity.apiManager.report(
            false, BaseActivity.TYPE_USER, 0, activity.userId
        ) { response ->
            val resp = response as com.joinflatshare.pojo.BaseResponse
            if (resp.status == 200) {
                viewBinding.cardLike.visibility = View.GONE
                viewBinding.cardNotInterested.visibility = View.GONE
                if (callbackIntent == null)
                    callbackIntent = Intent()
                callbackIntent?.putExtra("report", true)
                activity.onBackPressed()
            }
        }*/
    }
}