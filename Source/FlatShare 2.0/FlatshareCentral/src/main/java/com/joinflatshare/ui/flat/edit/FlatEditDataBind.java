package com.joinflatshare.ui.flat.edit;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatEditBinding;
import com.joinflatshare.constants.AppData;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.ui.flat.edit.options.total_beds.TotalBedsViewBind;
import com.joinflatshare.ui.flat.edit.options.vacant_beds.VacantBedsViewBind;

import java.util.ArrayList;
import java.util.Collections;

public class FlatEditDataBind {
    private final FlatEditActivity activity;
    private final ActivityFlatEditBinding viewBind;
    protected FlatEditImageAdapter adapter;
    protected VacantBedsViewBind vacantBedsViewBind;
    protected TotalBedsViewBind totalBedsViewBind;
    protected FlatEditAmenityAdapter amenityAdapter;

    // Image Handlers
    protected ArrayList<String> apiUserImages = new ArrayList<>();
    protected ArrayList<String> adapterUserImages = new ArrayList<>();
    protected ArrayList<String> deletedUserImages = new ArrayList<>();
    protected ArrayList<String> addedUserImages = new ArrayList<>();

    public FlatEditDataBind(FlatEditActivity activity) {
        this.activity = activity;
        viewBind = activity.viewBind;
        bind();
        createProgressBar();
        setData();
    }

    private void bind() {
        activity.baseViewBinder.view_topbar_divider.setVisibility(View.GONE);
        viewBind.rvProfileInfoImage.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new FlatEditImageAdapter(activity);

        // Vacant Beds
        vacantBedsViewBind = new VacantBedsViewBind(activity);

        // Total Beds
        totalBedsViewBind = new TotalBedsViewBind(activity);
    }

    private void createProgressBar() {
        viewBind.llProgress.removeAllViews();
        int totalProgress = 8;
        for (int i = 0; i < totalProgress; i++) {
            TextView tv = new TextView(activity);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tv.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_hint));
            viewBind.llProgress.addView(tv);
        }
    }

    private void setData() {
        MyFlatData myFlat = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        if (myFlat != null && myFlat.getFlatProperties() != null) {
            if (myFlat.getFlatProperties().getLocation() != null) {
                activity.latFlatLocation = myFlat.getFlatProperties().getLocation();
                if (activity.latFlatLocation.getName().isEmpty()) {
                    viewBind.txtFlatLocation.setText("");
                } else {
                    viewBind.txtFlatLocation.setText(activity.latFlatLocation.getName());
                }
            }
            if (myFlat.getFlatProperties().getSociety() != null) {
                activity.latSociety = myFlat.getFlatProperties().getSociety();
                if (activity.latSociety.getName().isEmpty()) {
                    viewBind.edtFlatSociety.setText("");
                } else {
                    viewBind.edtFlatSociety.setText(activity.latSociety.getName());
                }
            }
            viewBind.edtFlatName.setText(myFlat.getName());
            if (myFlat.getFlatProperties().getRoomType() != null)
                viewBind.txtRoomtype.setText(myFlat.getFlatProperties().getRoomType());

            if (myFlat.getFlatProperties().getFlatsize() != null)
                viewBind.txtFlatsize.setText(myFlat.getFlatProperties().getFlatsize());


            // Beds
            viewBind.llVacantBeds.setVisibility(View.GONE);
            viewBind.txtVacantBeds.setText("0");
            if (myFlat.getFlatProperties().getBeds() != null) {
                int total = myFlat.getFlatProperties().getBeds().getTotal();
                if (total > 0) {
                    viewBind.txtVacantBeds.setText("" + myFlat.getFlatProperties().getBeds().getVacant());
                    viewBind.txtTotalBeds.setText("" + total);
                    viewBind.llVacantBeds.setVisibility(View.VISIBLE);
                }
            }

            viewBind.edtEdtFlatNorms.setText(myFlat.getNorms());

            activity.selectedAmenityList.clear();
            activity.selectedAmenityList.addAll(myFlat.getFlatProperties().getAmenities());


            if (myFlat.getFlatProperties().getFurnishing() != null) {
                viewBind.txtFurnishing.setText(TextUtils.join("", myFlat.getFlatProperties().getFurnishing()));
            }

            // Image
            loadImages(myFlat);
            viewBind.rvProfileInfoImage.setAdapter(adapter);
        }
        loadAmenities(true);
    }

    protected void loadAmenities(boolean showLess) {
        // Get selected value
        if (amenityAdapter != null) {
            activity.selectedAmenityList.clear();
            activity.selectedAmenityList.addAll(amenityAdapter.getSelectedAmenityList());
        }
        viewBind.rvFlatAmenities.setLayoutManager(new LinearLayoutManager(activity));
        ArrayList<String> allAmenities = new ArrayList<>();
        if (AppData.INSTANCE.getFlatData() != null) {
            if (showLess) {
                for (String amen : AppData.INSTANCE.getFlatData().getAmenities()) {
                    if (allAmenities.size() == 5)
                        break;
                    allAmenities.add(amen);
                }
            } else allAmenities.addAll(AppData.INSTANCE.getFlatData().getAmenities());
        }
        amenityAdapter = new FlatEditAmenityAdapter(allAmenities, activity.selectedAmenityList);
        viewBind.rvFlatAmenities.setAdapter(amenityAdapter);
    }

    private void loadImages(MyFlatData myFlatData) {
        adapterUserImages.clear();
        apiUserImages.clear();
        if (myFlatData != null) {
            apiUserImages.addAll(myFlatData.getImages());
            adapterUserImages.addAll(myFlatData.getImages());
        }
        Collections.reverse(adapterUserImages);
        if (adapterUserImages.size() < 10)
            adapterUserImages.add(0, "");
        adapter.setItems(adapterUserImages);
        setCompleteCount();
    }

    public void setCompleteCount() {
        int completeCount = 0;
        ArrayList<String> items = adapter.getItems();
        if (items.size() > 1)
            completeCount++;
        if (!viewBind.edtFlatName.getText().toString().isEmpty())
            completeCount++;
        if (!viewBind.txtFlatLocation.getText().toString().isEmpty())
            completeCount++;
        if (!viewBind.txtFlatsize.getText().toString().isEmpty())
            completeCount++;
        if (!viewBind.txtRoomtype.getText().toString().isEmpty())
            completeCount++;
        if (!viewBind.txtTotalBeds.getText().toString().equals("0"))
            completeCount++;
        if (!viewBind.txtVacantBeds.getText().toString().equals("0"))
            completeCount++;
        if (!viewBind.edtEdtFlatNorms.getText().toString().isEmpty())
            completeCount++;
        for (int i = 0; i < viewBind.llProgress.getChildCount(); i++) {
            if (viewBind.llProgress.getChildAt(i) instanceof TextView) {
                TextView txt = (TextView) viewBind.llProgress.getChildAt(i);
                if (i < completeCount) {
                    txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.blue));
                } else {
                    txt.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_hint));
                }
            }
        }
    }
}
