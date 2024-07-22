package com.joinflatshare.ui.flat.details;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatDetailsBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.customviews.GridSpacingItemDecoration;
import com.joinflatshare.customviews.deal_breakers.DealBreakerView;
import com.joinflatshare.customviews.interests.InterestsView;
import com.joinflatshare.pojo.flat.FlatProperties;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.flat.edit.FlatMemberAdapter;
import com.joinflatshare.utils.helper.DateUtils;
import com.joinflatshare.utils.helper.DistanceCalculator;

import java.util.Collections;

public class FlatDetailsViewBind {
    private final FlatDetailsActivity activity;
    private ActivityFlatDetailsBinding viewBind;

    public FlatDetailsViewBind(FlatDetailsActivity activity, ActivityFlatDetailsBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
    }

    protected void setData() {
        MyFlatData flatData = activity.flatResponse.getData();
        String name = flatData.getName();
        if (name.length() > 25) {
            name = name.substring(0, 25) + "...";
        }
        MyFlatData myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        if (myFlatData == null || myFlatData.getMongoId().equals(flatData.getMongoId()))
            activity.showTopBar(activity, true, name, 0, 0);
        else activity.showTopBar(activity, true, name, 0, R.drawable.ic_threedots);
        setImages(flatData);

        // Score
        int score = flatData.getScore();
        if (score < 0) score = 0;
        viewBind.txtFlatScore.setText("" + score);

        // Distance
        showDistance(flatData);

        FlatProperties properties = flatData.getFlatProperties();
        // Map
//        viewBind.btnFlatMap.setVisibility(properties.getLocation() == null ? View.INVISIBLE : View.VISIBLE);

        if (properties.getRentperPerson() > 0)
            viewBind.txtRent.setText("   " + activity.getResources().getString(R.string.currency) + properties.getRentperPerson());
        if (properties.getDepositperPerson() > 0)
            viewBind.txtDeposit.setText("   " + activity.getResources().getString(R.string.currency) + properties.getDepositperPerson());

        viewBind.txtFlatsize.setText(properties.getFlatsize());
        viewBind.txtRoomtype.setText(properties.getRoomType());

        viewBind.txtGender.setText(properties.getGender().equals("Both") ? "Gender-Neutral" : properties.getGender());
        if (properties.getFurnishing().size() == 0)
            viewBind.txtFurnishing.setVisibility(View.GONE);
        else {
            StringBuilder sb = new StringBuilder("");
            for (String temp : properties.getFurnishing()) {
                sb.append(", " + temp);
            }
            viewBind.txtFurnishing.setText(sb.substring(2));
        }
        if (properties.getMoveinDate() != null && !properties.getMoveinDate().isEmpty()) {
            viewBind.txtMovein.setText(DateUtils.INSTANCE.convertToAppFormat(properties.getMoveinDate()));
        }

        // edit button
        viewBind.btnEdit.setVisibility(View.GONE);
        /*viewBind.btnFlatShare.setVisibility(DialogShare.Companion.isAllFlatDataAvailableToShare(activity.flatResponse.getData()) ? View.VISIBLE : View.GONE);*/
        if (flatData.getId().equals(AppConstants.loggedInUser.getId())) {
            viewBind.btnEdit.setVisibility(View.VISIBLE);
        } else {
            if (activity.flatResponse.getFlatMates().size() > 0) {
                for (User temp : activity.flatResponse.getFlatMates()) {
                    if (temp.getId().equals(AppConstants.loggedInUser.getId())) {
                        viewBind.btnEdit.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        // Interests
        setInterests();

        // Languages
        setLanguages();

        // Norms
        setNorms(flatData);

        //Members
        setMembers();

        // set amenities
        setAmenities(properties);

        // Deal breakers
        setDealBreakers(flatData);

        // Turning on the view at the end
        viewBind.pullToRefresh.setVisibility(View.VISIBLE);
    }

    // Distance
    private void showDistance(MyFlatData flatData) {
        try {
            String dist = DistanceCalculator.INSTANCE.calculateDistance(
                    AppConstants.loggedInUser.getLocation().getLoc().getCoordinates().get(0),
                    AppConstants.loggedInUser.getLocation().getLoc().getCoordinates().get(1),
                    flatData.getFlatProperties().getLocation().getLoc().getCoordinates().get(0),
                    flatData.getFlatProperties().getLocation().getLoc().getCoordinates().get(1));
            viewBind.txtDistance.setText(dist);
        } catch (Exception exception) {
            viewBind.txtDistance.setText("NA");
        }
    }

    private void setInterests() {
        if (activity.flatResponse.getData().getFlatProperties().getInterests() == null ||
                activity.flatResponse.getData().getFlatProperties().getInterests().size() == 0) {
            viewBind.llInterestHolder.setVisibility(View.GONE);
        } else {
            viewBind.llInterestHolder.setVisibility(View.VISIBLE);
            FlatProperties properties = activity.flatResponse.getData().getFlatProperties();
            /*InterestsView interestsView = new InterestsView(activity, viewBind.rvProfileInterests,
                    InterestsView.VIEW_TYPE_INTERESTS);
            interestsView.setContentValues(properties.getInterests());
            try {
                MyFlatData myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                if (!activity.flatResponse.getData().getMongoId().equals(myFlatData.getMongoId())) {
                    interestsView.calculateMatchingContent(AppConstants.loggedInUser.getFlatProperties().getInterests());
                }
            } catch (Exception ignored) {

            }
            interestsView.show();*/
        }
    }

    private void setLanguages() {
        if (activity.flatResponse.getData().getFlatProperties().getLanguages() == null ||
                activity.flatResponse.getData().getFlatProperties().getLanguages().size() == 0) {
            viewBind.llLanguageHolder.setVisibility(View.GONE);
        } else {
            viewBind.llLanguageHolder.setVisibility(View.VISIBLE);
            FlatProperties properties = activity.flatResponse.getData().getFlatProperties();
            /*InterestsView interestsView = new InterestsView(activity, viewBind.rvProfileLanguage,
                    InterestsView.VIEW_TYPE_LANGUAGES);
            interestsView.setContentValues(properties.getLanguages());
            try {
                MyFlatData myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
                if (!activity.flatResponse.getData().getMongoId().equals(myFlatData.getMongoId())) {
                    interestsView.calculateMatchingContent(AppConstants.loggedInUser.getFlatProperties().getLanguages());
                }
            } catch (Exception ignored) {

            }
            interestsView.show();*/
        }
    }

    private void setNorms(MyFlatData flatData) {
        String norms = flatData.getNorms();
        if (norms != null && !norms.isEmpty()) {
            /*norms = " \u2022  " + norms;
            norms = norms.replace("\n", "\n \u2022  ");*/
            viewBind.txtNorms.setText(norms);
        } else {
            viewBind.llNorms.setVisibility(View.GONE);
        }
    }

    private void setMembers() {
        if (activity.flatResponse.getFlatMates() != null && activity.flatResponse.getFlatMates().size() > 0) {
            viewBind.llFlatmates.setVisibility(View.VISIBLE);
            viewBind.rvFlatMember.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
            viewBind.rvFlatMember.setAdapter(new FlatMemberAdapter(activity, activity.flatResponse.getData(), activity.flatResponse.getFlatMates()));
        } else {
            viewBind.llFlatmates.setVisibility(View.GONE);
        }
    }

    private void setImages(MyFlatData flatData) {
        if (flatData.getImages().size() == 0)
            viewBind.rvFlatImages.setVisibility(View.GONE);
        else {
            Collections.reverse(flatData.getImages());
            viewBind.rvFlatImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            viewBind.rvFlatImages.setAdapter(new FlatDetailsImageAdapter(activity, flatData.getImages()));
        }
    }

    private void setAmenities(FlatProperties properties) {
        if (properties.getAmenities().size() == 0) {
            viewBind.rvFlatAmenities.setVisibility(View.GONE);
        } else {
            int spanCount = 4;
            int spacing = 25;
            boolean includeEdge = false;
            viewBind.rvFlatAmenities.setLayoutManager(new GridLayoutManager(activity, spanCount));
            viewBind.rvFlatAmenities.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            viewBind.rvFlatAmenities.setAdapter(new FlatDetailsAmenitiesAdapter(activity, properties.getAmenities()));
        }
    }

    private void setDealBreakers(MyFlatData flatData) {
        FlatProperties properties = flatData.getFlatProperties();
        DealBreakerView dealBreakerView = new DealBreakerView(activity, viewBind.rvFlatDeals);
        dealBreakerView.setDealValues(properties.getDealBreakers(), viewBind.llDealHolder);
        dealBreakerView.show();
    }
}
