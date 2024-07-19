package com.joinflatshare.ui.explore.holder

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.joinflatshare.FlatshareCentral.databinding.ItemExploreVpBinding
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity
import com.joinflatshare.utils.helper.CommonMethod

/**
 * Created by debopam on 14/11/22
 */
object AdapterUserVpHolder {
    fun bindVp(activity: BaseActivity, holder: ItemExploreVpBinding, user: User) {
        if (user.status.isNullOrEmpty()) holder.llVpAbout.visibility = View.GONE
        else {
            holder.llVpAbout.visibility = View.VISIBLE
            holder.txtAbout.text = user.status
        }

        if (user.flatProperties.interests.isNullOrEmpty()) holder.llVpInterest.visibility =
            View.GONE
        else {
            holder.llVpInterest.visibility = View.VISIBLE
            holder.txtInterest.text =
                TextUtils.join(", ", user.flatProperties.interests)
        }

        if (user.flatProperties.languages.isNullOrEmpty()) holder.llVpLanguage.visibility =
            View.GONE
        else {
            holder.llVpLanguage.visibility = View.VISIBLE
            holder.txtLanguage.text =
                TextUtils.join(", ", user.flatProperties.languages)
        }

        if (CommonMethod.isDealBreakerEmpty(user.flatProperties.dealBreakers)) {
            holder.llVpDeals.visibility = View.GONE
        } else {
            holder.llVpDeals.visibility = View.VISIBLE
            val dealBreakerView = DealBreakerView(activity, holder.rvVpDeals)
            dealBreakerView.setDealValues(user.flatProperties.dealBreakers, null)
            dealBreakerView.show()
        }

        if (user.work.isNullOrEmpty()) holder.llVpWork.visibility = View.GONE
        else {
            holder.llVpWork.visibility = View.VISIBLE
            holder.txtWork.text = user.work
        }

        if (user.college?.name.isNullOrEmpty()) holder.llVpEducation.visibility =
            View.GONE
        else {
            holder.llVpEducation.visibility = View.VISIBLE
            holder.txtEducation.text = user.college?.name
        }

        if (user.hometown?.name.isNullOrEmpty()) holder.llVpHometown.visibility =
            View.GONE
        else {
            holder.llVpHometown.visibility = View.VISIBLE
            holder.txtHometown.text = user.hometown?.name
        }

        if (user.score <= 0) holder.llVpScore.visibility = View.GONE
        else {
            holder.llVpScore.visibility = View.VISIBLE
            holder.txtScore.text = "" + user.score
        }

        holder.rlExploreVp.setOnClickListener {
            val intent = Intent(activity, ProfileDetailsActivity::class.java)
            intent.putExtra("phone", user.id)
            CommonMethod.switchActivity(activity, intent, false)
        }
    }


    private fun blurView(activity: BaseActivity, holder: ItemExploreVpBinding) {
        /* Blurry.with(activity).radius(25).sampling(4)ẇ
             .color(ContextCompat.getColor(activity, R.color.white)).capture(holder.e)
             .getAsync {
                 holder.imgBlur.setImageDrawable(BitmapDrawable(activity.resources, it))
             }*/
    }
}