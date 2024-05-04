package com.joinflatshare.ui.flat.flatoptions;

import android.content.Intent;
import android.widget.TextView;

import com.joinflatshare.constants.AppData;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.customviews.interests.InterestsView;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.dialogs.DialogFlatOptions;
import com.joinflatshare.ui.flat.flat_profile.MyFlatActivity;
import com.joinflatshare.ui.flat.flatoptions.view_amenity.AmenitiesViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_furnishing.FurnishingViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_gender.GenderViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_location.LocationViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_moveindate.MoveInListener;
import com.joinflatshare.ui.flat.flatoptions.view_moveindate.MoveInViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_preferred_location.PreferredLocationViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_rent.RentViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_rent_range.RentRangeViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_roomsize.RoomSizeViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_roomtype.RoomTypeViewBind;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;

import java.util.ArrayList;

public class FlatOptionActivity implements OnUiEventClick {
    public BaseActivity activity;
    public DialogFlatOptions dialogFlatOptions;
    public int selectedViewConstant;
    public FlatOptionListener listener;

    // View 1
    public AmenitiesViewBind amenitiesViewBind;
    // View 2
    public RoomSizeViewBind roomSizeViewBind;
    // View 3 and 4
    public RentViewBind rentViewBind;
    // View 5
    public RoomTypeViewBind roomTypeViewBind;
    // View 6
    public MoveInViewBind moveInViewBind;
    // View 7
    public GenderViewBind genderViewBind;
    // View 8
    public LocationViewBind locationViewBind;
    // View 9
    public RentRangeViewBind rentRangeViewBind;
    // View 10
    public PreferredLocationViewBind preferredLocationViewBind;
    // View 13
    public FurnishingViewBind furnishingViewBind;


    public FlatOptionActivity(BaseActivity activity, boolean shouldCallApi, OnUiEventClick onUiEventClick) {
        this.activity = activity;
        listener = new FlatOptionListener(this, shouldCallApi, onUiEventClick);
        dialogFlatOptions = new DialogFlatOptions(activity, this);
    }

    public void switchView() {
        switch (selectedViewConstant) {
            case FlatOptionConstant.VIEW_CONSTANT_AMENITY:
                if (AppData.INSTANCE.getFlatData() == null
                        || AppData.INSTANCE.getFlatData().getAmenities().size() == 0)
                    CommonMethod.INSTANCE.makeToast( "We did not find any flat amenities options");
                else {
                    amenitiesViewBind = new AmenitiesViewBind(dialogFlatOptions);
                }
                break;
            case FlatOptionConstant.VIEW_CONSTANT_FURNISHING:
                if (AppData.INSTANCE.getFlatData() == null
                        || AppData.INSTANCE.getFlatData().getFurnishing().size() == 0)
                    CommonMethod.INSTANCE.makeToast( "We did not find any flat furnishing options");
                else {
                    furnishingViewBind = new FurnishingViewBind(dialogFlatOptions);
                }
                break;
            case FlatOptionConstant.VIEW_CONSTANT_ROOMSIZE:
                if (AppData.INSTANCE.getFlatData() == null
                        || AppData.INSTANCE.getFlatData().getFlatSize().size() == 0)
                    CommonMethod.INSTANCE.makeToast( "We did not find any flat size");
                else {
                    roomSizeViewBind = new RoomSizeViewBind(dialogFlatOptions);
                }
                break;
            case FlatOptionConstant.VIEW_CONSTANT_RENT:
            case FlatOptionConstant.VIEW_CONSTANT_DEPOSIT:
                rentViewBind = new RentViewBind(dialogFlatOptions, selectedViewConstant);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_ROOMTYPE:
                if (AppData.INSTANCE.getFlatData() == null
                        || AppData.INSTANCE.getFlatData().getRoomType().size() == 0)
                    CommonMethod.INSTANCE.makeToast( "We did not find any room types");
                else {
                    roomTypeViewBind = new RoomTypeViewBind(dialogFlatOptions);
                    roomTypeViewBind.setRoomType("");
                }
                break;
            case FlatOptionConstant.VIEW_CONSTANT_MOVEINDATE:
                moveInViewBind = new MoveInViewBind(dialogFlatOptions);
                new MoveInListener(activity, moveInViewBind);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_GENDER:
                genderViewBind = new GenderViewBind(dialogFlatOptions);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_LOCATION:
                locationViewBind = new LocationViewBind(dialogFlatOptions);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_SHARED_PREFERRED_LOCATION:
                preferredLocationViewBind = new PreferredLocationViewBind(dialogFlatOptions);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_RENT_RANGE:
                if (AppData.INSTANCE.getFlatData() == null) {
                    AlertDialog.showAlert(activity, "We did not find any rent range");
                    return;
                }
                rentRangeViewBind = new RentRangeViewBind(dialogFlatOptions);
                break;
            case FlatOptionConstant.VIEW_CONSTANT_LANGUAGE:
                TextView view = null;
                if (activity instanceof MyFlatActivity) {
                    view = ((MyFlatActivity) activity).viewBind.txtFlatmateLanguage;
                }
                if (view != null) {
                    InterestsView interestsView = new InterestsView(activity, InterestsView.VIEW_TYPE_LANGUAGES, view);
                    interestsView.assignCallback((intent, requestCode) -> {
                        ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra("interestlist");
                        dialogFlatOptions.getFlat().getFlatProperties().setLanguages(list);
                        activity.apiManager.updateFlat(false, dialogFlatOptions.getFlat(), response -> {
                            if (activity instanceof MyFlatActivity) {
                                MyFlatActivity act = (MyFlatActivity) activity;
                                act.setResponse();
                                act.dataBind.calculateFlatmateCompletion();
                            }
                        });
                    });
                    interestsView.show();
                }
                break;
            case FlatOptionConstant.VIEW_CONSTANT_INTEREST:
                view = null;
                if (activity instanceof MyFlatActivity) {
                    view = ((MyFlatActivity) activity).viewBind.txtFlatmateInterest;
                }
                if (view != null) {
                    InterestsView interestsView = new InterestsView(activity, InterestsView.VIEW_TYPE_INTERESTS, view);
                    interestsView.assignCallback((intent, requestCode) -> {
                        ArrayList<String> list = (ArrayList<String>) intent.getSerializableExtra("interestlist");
                        dialogFlatOptions.getFlat().getFlatProperties().setInterests(list);
                        activity.apiManager.updateFlat(false, dialogFlatOptions.getFlat(), response -> {
                            if (activity instanceof MyFlatActivity) {
                                MyFlatActivity act = (MyFlatActivity) activity;
                                act.setResponse();
                                act.dataBind.calculateFlatmateCompletion();
                            }
                        });
                    });
                    interestsView.show();
                }
                break;

        }
    }

    @Override
    public void onClick(Intent intent, int requestCode) {
        listener.performClick(requestCode == 1);
    }
}
