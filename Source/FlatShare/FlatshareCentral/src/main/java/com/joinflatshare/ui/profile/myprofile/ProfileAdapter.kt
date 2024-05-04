package com.joinflatshare.ui.profile.myprofile

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ItemProfileButtonBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.date.DatePreferenceActivity
import com.joinflatshare.ui.explore.ExploreActivity
import com.joinflatshare.ui.faq.FaqActivity
import com.joinflatshare.ui.flat.edit.FlatEditActivity
import com.joinflatshare.ui.flat.flat_profile.MyFlatActivity
import com.joinflatshare.ui.friends.FriendListActivity
import com.joinflatshare.ui.liked.LikedActivity
import com.joinflatshare.ui.notifications.request_chat.ChatRequestActivity
import com.joinflatshare.ui.preferences.flat.PrefFlatActivity
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.utils.helper.CommonMethods
import com.joinflatshare.utils.mixpanel.MixpanelUtils

class ProfileAdapter(
    private val activity: BaseActivity,
    private val items: ArrayList<ModelBottomSheet>,
    private val viewWidth: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewBind =
            ItemProfileButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(activity, viewBind)
    }

    override fun onBindViewHolder(mainHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = mainHolder as ViewHolder
        holder.bind(mainHolder.bindingAdapterPosition, this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        private val activity: BaseActivity,
        private val view: ItemProfileButtonBinding,
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            position: Int, adapter: ProfileAdapter
        ) {
            val item = adapter.items[position]
            view.imgProfileButton.setImageResource(item.icon)
            view.txtProfileButton.text = item.name
            if (item.type == 1 || item.type == 4) {
                view.txtProfileButton.gravity = Gravity.CENTER
            } else view.txtProfileButton.gravity = Gravity.NO_GRAVITY

            // Gap
            view.v1.visibility = View.GONE

            if (activity is ExploreActivity) {
                var buttonWidth = (adapter.viewWidth / 2)
                var gapWidth = 30
                if (adapter.itemCount == 1) {
                    gapWidth = 0
                } else if (adapter.itemCount == 2) {
                    buttonWidth -= gapWidth
                    if (position == 0)
                        view.v1.visibility = View.VISIBLE
                } else {
                    buttonWidth += gapWidth
                    if (position != adapter.itemCount - 1) {
                        view.v1.visibility = View.VISIBLE
                    }
                }
                val params =
                    LinearLayout.LayoutParams(
                        gapWidth,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                view.v1.layoutParams = params
                view.llProfileSharedFlat.layoutParams.width = buttonWidth
            } else
                view.llProfileButtonHolder.layoutParams.width =
                    LinearLayout.LayoutParams.MATCH_PARENT
            view.llProfileSharedFlat.requestLayout()

            // Stroke management
            view.llProfileSharedFlat.strokeColor =
                ContextCompat.getColor(activity, R.color.flat_buttons)
            if (activity is ExploreActivity) {
                when (activity.SEARCH_TYPE) {
                    BaseActivity.TYPE_USER -> {
                        if (item.type == 2) {
                            view.llProfileSharedFlat.strokeColor =
                                ContextCompat.getColor(activity, R.color.color_text_primary)
                            if (activity.errorMessage.isNotEmpty()) {
                                if (activity.errorMessage.startsWith("Add"))
                                    view.txtProfileButton.text = "Edit Flat\nProfile"
                            }
                            activity.binder.resetAllViews()
                            activity.binder.animateTextButtonForSFS()
                        }
                    }

                    BaseActivity.TYPE_FLAT -> {
                        if (item.type == 1) {
                            view.llProfileSharedFlat.strokeColor =
                                ContextCompat.getColor(activity, R.color.color_text_primary)
                            activity.binder.resetAllViews()
                            activity.binder.animateTextButtonForSFS()
                        }
                    }

                    BaseActivity.TYPE_FHT -> {
                        if (item.type == 7) {
                            view.llProfileSharedFlat.strokeColor =
                                ContextCompat.getColor(activity, R.color.color_text_primary)
                            activity.binder.resetAllViews()
                            activity.binder.animateTextButtonForFHT()
                        }
                    }

                    BaseActivity.TYPE_DATE -> {
                        if (item.type == 8) {
                            view.llProfileSharedFlat.strokeColor =
                                ContextCompat.getColor(activity, R.color.color_text_primary)
                            activity.binder.resetAllViews()
                            activity.binder.animateTextButtonForDate()
                        }
                    }
                }
            }


            view.llProfileSharedFlat.setOnClickListener {
                when (item.type) {
                    1 -> {
                        if (activity is ExploreActivity) {
                            val intent = Intent(activity, PrefFlatActivity::class.java)
                            intent.putExtra("isFhtView", false)
                            if (AppConstants.loggedInUser?.isFlatSearch?.value == false) {
                                AppConstants.loggedInUser?.isFlatSearch?.value = true
                                activity.apiManager.updateProfile(
                                    true, AppConstants.loggedInUser
                                ) {
                                    CommonMethods.makeToast(
                                        "Your flat search is turned on"
                                    )
                                    MixpanelUtils.onButtonClicked("Shared Flat Search")
                                    activity.SEARCH_TYPE = BaseActivity.TYPE_FLAT
                                    AppConstants.isFeedReloadRequired = true
                                    CommonMethod.switchActivity(activity, intent, false)
                                }
                            } else {
                                CommonMethod.switchActivity(
                                    activity, intent
                                ) { results ->
                                    if (results?.resultCode == Activity.RESULT_OK) {
                                        AppConstants.isFeedReloadRequired = true
                                    }
                                }
                            }
                        }
                    }

                    7 -> {
                        if (activity is ExploreActivity) {
                            val intent = Intent(activity, PrefFlatActivity::class.java)
                            intent.putExtra("isFhtView", true)
                            if (AppConstants.loggedInUser?.isFHTSearch?.value == false) {
                                AppConstants.loggedInUser?.isFHTSearch?.value = true
                                activity.apiManager.updateProfile(
                                    true, AppConstants.loggedInUser
                                ) {
                                    CommonMethods.makeToast(
                                        "Flathunt Together is turned on"
                                    )
                                    MixpanelUtils.onButtonClicked("Flathunt Together Search")
                                    AppConstants.isFeedReloadRequired = true
                                    activity.SEARCH_TYPE = BaseActivity.TYPE_FHT
                                    CommonMethod.switchActivity(activity, intent, false)
                                }
                            } else {
                                CommonMethod.switchActivity(
                                    activity, intent
                                ) { results ->
                                    if (results?.resultCode == Activity.RESULT_OK) {
                                        AppConstants.isFeedReloadRequired = true
                                    }
                                }
                            }
                        }
                    }

                    8 -> {
                        if (activity is ExploreActivity) {
                            val intent = Intent(activity, DatePreferenceActivity::class.java)
                            if (AppConstants.loggedInUser?.isDateSearch?.value == false) {
                                AppConstants.loggedInUser?.isDateSearch?.value = true
                                activity.apiManager.updateProfile(
                                    true, AppConstants.loggedInUser
                                ) {
                                    CommonMethods.makeToast(
                                        "Vibe Check is turned on"
                                    )
                                    MixpanelUtils.onButtonClicked("Vibe Check Search")
                                    AppConstants.isFeedReloadRequired = true
                                    activity.SEARCH_TYPE = BaseActivity.TYPE_DATE
                                    CommonMethod.switchActivity(activity, intent, false)
                                }
                            } else {
                                CommonMethod.switchActivity(
                                    activity, intent
                                ) { results ->
                                    if (results?.resultCode == Activity.RESULT_OK) {
                                        AppConstants.isFeedReloadRequired = true
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        if (activity is ExploreActivity) {
                            if (activity.errorMessage.isNotEmpty()) {
                                if (activity.errorMessage.startsWith("Add")) {
                                    val intent = Intent(activity, FlatEditActivity::class.java)
                                    CommonMethod.switchActivity(
                                        activity, intent
                                    ) { results ->
                                        if (results?.resultCode == Activity.RESULT_OK) {
                                            AppConstants.isFeedReloadRequired = true
                                        }
                                    }
                                    return@setOnClickListener
                                }
                            }
                            val intent = Intent(activity, MyFlatActivity::class.java)
                            CommonMethod.switchActivity(activity, intent, false)
                        }
                    }

                    3 -> {
                        val intent = Intent(activity, LikedActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }

                    4 -> {
                        val intent = Intent(activity, ChatRequestActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }

                    5 -> {
                        val intent = Intent(activity, FriendListActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }

                    6 -> {
                        val intent = Intent(activity, FaqActivity::class.java)
                        CommonMethod.switchActivity(activity, intent, false)
                    }
                }
            }

        }
    }
}