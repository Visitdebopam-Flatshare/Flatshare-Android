package com.joinflatshare.ui.flat.flat_profile;

import static androidx.recyclerview.widget.RecyclerView.GONE;
import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static androidx.recyclerview.widget.RecyclerView.VISIBLE;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.customviews.deal_breakers.DealBreakerCallback;
import com.joinflatshare.customviews.deal_breakers.DealBreakerView;
import com.joinflatshare.pojo.flat.DealBreakers;
import com.joinflatshare.pojo.flat.FlatProperties;
import com.joinflatshare.pojo.flat.FlatResponse;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.flat.edit.FlatMemberAdapter;
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator;
import com.joinflatshare.utils.helper.DateUtils;

import java.util.ArrayList;

public class MyFlatViewBind {
    private final MyFlatActivity activity;
    private final ActivityFlatBinding viewBind;
    private final DealBreakerView dealBreakerView;

    // Flatmate search
    protected RelativeLayout[] ll_flatmate_search_options;

    public MyFlatViewBind(MyFlatActivity activity, ActivityFlatBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        dealBreakerView = new DealBreakerView(activity, viewBind.rvFlatDeals);
        bind();
    }

    private void bind() {
        viewBind.rvFlatMember.setLayoutManager(new LinearLayoutManager(activity, HORIZONTAL, false));
        // Flatmate search
        ll_flatmate_search_options = new RelativeLayout[]{
                viewBind.llFlatmateSearchGender,
                viewBind.llFlatmateSearchDate,
                viewBind.llFlatmateSearchRent,
                viewBind.llFlatmateSearchDeposit,
                viewBind.llPreferencesLanguage,
                viewBind.llPreferencesInterest
        };
    }

    public void setData() {
        // initial
        viewBind.llFlatVerified.setVisibility(GONE);
        viewBind.llFlatMenu.setVisibility(GONE);
        viewBind.llFlatEditMain.setVisibility(GONE);
        viewBind.llFlatmateSearch.setVisibility(VISIBLE);
        viewBind.llMyflat.setVisibility(GONE);
        viewBind.btnFlatmateSearch.setVisibility(GONE);
        viewBind.txtPrefFlatmateCopyLink.setVisibility(GONE);
        viewBind.txtFlatmateCloseSearch.setVisibility(GONE);

        // Check if flat is not yet created
        if (activity.myFlatData == null || activity.myFlatData.getName().isEmpty()) {
            viewBind.btnLeaveFlat.setVisibility(GONE);
            viewBind.btnChatFlat.setVisibility(GONE);
            viewBind.txtFlatName.setText("Flatmates");
            viewBind.imgFlatVerified.setVisibility(GONE);
            viewBind.txtFlatmateSearch.setText("Add My Flat");

        } else {
            viewBind.llFlatVerified.setVisibility(VISIBLE);
            viewBind.llFlatMenu.setVisibility(VISIBLE);
            viewBind.txtFlatName.setText(activity.myFlatData.getName());
            viewBind.txtFlatmateSearch.setText("Flatmate Search");

            // Verified flat change
            viewBind.imgFlatVerified.setImageResource(activity.myFlatData.isVerified() ? R.drawable.ic_tick_verified : R.drawable.ic_tick_unverified);
            if (activity.myFlatData.isMateSearch().getValue()) {
                viewBind.llFlatmateSearch.setVisibility(GONE);
                viewBind.llMyflat.setVisibility(VISIBLE);
                viewBind.llFlatEditMain.setVisibility(VISIBLE);
                viewBind.llFlatmateSearchContent.setVisibility(VISIBLE);
                viewBind.btnFlatmateSearch.setVisibility(VISIBLE);
                if (FlatShareMessageGenerator.INSTANCE.isFlatDataAvailableToShare(activity.myFlatData))
                    viewBind.txtPrefFlatmateCopyLink.setVisibility(VISIBLE);
                viewBind.txtFlatmateCloseSearch.setVisibility(VISIBLE);
                viewBind.txtFlatmateGender.setText(activity.myFlatData.getFlatProperties().getGender());
                if (activity.myFlatData.getFlatProperties().getMoveinDate() != null &&
                        !activity.myFlatData.getFlatProperties().getMoveinDate().isEmpty())
                    viewBind.txtFlatmateDate.setText(DateUtils.INSTANCE.convertToAppFormat(activity.myFlatData.getFlatProperties().getMoveinDate()));
                viewBind.txtFlatmateRent.setText(activity.getResources().getString(R.string.currency) + " " + activity.myFlatData.getFlatProperties().getRentperPerson());
                viewBind.txtFlatmateDeposit.setText(activity.getResources().getString(R.string.currency) + " " + activity.myFlatData.getFlatProperties().getDepositperPerson());
                if (activity.myFlatData.getFlatProperties().getInterests() != null && activity.myFlatData.getFlatProperties().getInterests().size() > 0)
                    viewBind.txtFlatmateInterest.setText(TextUtils.join(", ", activity.myFlatData.getFlatProperties().getInterests()));
                else viewBind.txtFlatmateInterest.setText("");
                if (activity.myFlatData.getFlatProperties().getLanguages() != null && activity.myFlatData.getFlatProperties().getLanguages().size() > 0)
                    viewBind.txtFlatmateLanguage.setText(TextUtils.join(", ", activity.myFlatData.getFlatProperties().getLanguages()));
                else viewBind.txtFlatmateLanguage.setText("");

                setDealBreakers();
                calculateFlatmateCompletion();
            }
        }
    }

    private void setDealBreakers() {
        FlatProperties properties = activity.myFlatData.getFlatProperties();
        DealBreakerCallback callback = new DealBreakerCallback() {
            @Override
            public void onPetsSelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setPets(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }

            @Override
            public void onWorkoutSelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setWorkout(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }

            @Override
            public void onSmokingSelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setSmoking(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }

            @Override
            public void onNonVegSelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setNonveg(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }

            @Override
            public void onPartySelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setFlatparty(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }

            @Override
            public void onEggsSelected(int constant) {
                DealBreakers breakers = activity.myFlatData.getFlatProperties().getDealBreakers();
                if (breakers == null)
                    breakers = new DealBreakers();
                breakers.setEggs(constant);
                activity.myFlatData.getFlatProperties().setDealBreakers(breakers);
                activity.apiManager.updateFlat(false, activity.myFlatData, response -> {
                    activity.setResponse();
                    AppConstants.isFeedReloadRequired = true;
                });
            }
        };


        dealBreakerView.setDealValues(properties.getDealBreakers(), null);
        dealBreakerView.assignCallback(callback);
        dealBreakerView.show();
    }

    // Members
    public void setFlatmates() {
        viewBind.rvFlatMember.setVisibility(View.VISIBLE);
        ArrayList<User> mates = new ArrayList<>();
        FlatResponse response = FlatShareApplication.Companion.getDbInstance().userDao().getFlatResponse();
        if (response == null || response.getData() == null || response.getData().getName().isEmpty()) {
            mates = new ArrayList<>();
            mates.add(AppConstants.loggedInUser);
            viewBind.btnLeaveFlat.setVisibility(GONE);
        } else {
            mates.addAll(response.getFlatMates());
            if (mates.size() <= 1)
                viewBind.btnLeaveFlat.setVisibility(GONE);
            else viewBind.btnLeaveFlat.setVisibility(VISIBLE);
        }
        viewBind.rvFlatMember.setAdapter(new FlatMemberAdapter(activity,
                FlatShareApplication.Companion.getDbInstance().userDao().getFlatData(), mates));
    }

    public void calculateFlatmateCompletion() {
        MyFlatData item = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        if (item != null) {
            boolean allFilled = true;
            if (item.isMateSearch().getValue()) {
                FlatProperties properties = item.getFlatProperties();
                // Gender
                if (properties.getGender() == null || properties.getGender().isEmpty()) {
                    viewBind.imgFlatmateGenderArrow.setImageResource(R.drawable.arrow_down);
                    allFilled = false;
                } else
                    viewBind.imgFlatmateGenderArrow.setImageResource(R.drawable.ic_tick_blue_light);
                // Move In
                if (properties.getMoveinDate() != null && !properties.getMoveinDate().isEmpty())
                    viewBind.imgFlatmateDateArrow.setImageResource(R.drawable.ic_tick_blue_light);
                else {
                    viewBind.imgFlatmateDateArrow.setImageResource(R.drawable.arrow_down);
                    allFilled = false;
                }
                // Rent per person
                if (properties.getRentperPerson() > 0)
                    viewBind.imgFlatmateRentArrow.setImageResource(R.drawable.ic_tick_blue_light);
                else {
                    viewBind.imgFlatmateRentArrow.setImageResource(R.drawable.arrow_down);
                    allFilled = false;
                }
                // Deposit
                if (properties.getDepositperPerson() > 0)
                    viewBind.imgFlatmateDepositArrow.setImageResource(R.drawable.ic_tick_blue_light);
                else {
                    viewBind.imgFlatmateDepositArrow.setImageResource(R.drawable.arrow_down);
                    allFilled = false;
                }

                // Languages
                if (!properties.getLanguages().isEmpty())
                    viewBind.imgFlatmateLanguageArrow.setImageResource(R.drawable.ic_tick_blue_light);
                else {
                    viewBind.imgFlatmateLanguageArrow.setImageResource(
                            R.drawable.arrow_down
                    );
                    allFilled = false;
                }
                // Interests
                if (!properties.getInterests().isEmpty())
                    viewBind.imgFlatmateInterestsArrow.setImageResource(R.drawable.ic_tick_blue_light);
                else {
                    viewBind.imgFlatmateInterestsArrow.setImageResource(
                            R.drawable.arrow_down
                    );
                    allFilled = false;
                }
            }
            if (allFilled && item.isMateSearch().getValue()) {
                activity.setResult(Activity.RESULT_OK);
                activity.baseViewBinder.view_topbar_back_circle.setVisibility(VISIBLE);
            } else {
                activity.setResult(Activity.RESULT_CANCELED);
                activity.baseViewBinder.view_topbar_back_circle.setVisibility(GONE);
            }
        }
    }
}
