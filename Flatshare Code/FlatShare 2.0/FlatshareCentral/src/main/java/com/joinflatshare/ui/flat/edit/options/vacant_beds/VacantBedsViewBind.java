package com.joinflatshare.ui.flat.edit.options.vacant_beds;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.amenities.AmenitiesItem;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;
import com.joinflatshare.ui.flat.edit.options.FlatOptionViewBind;

import java.util.ArrayList;

public class VacantBedsViewBind extends FlatOptionViewBind {
    // View 2
    public RecyclerView rv_flat_amenities;
    public VacantBedsAdapter vacantBedsAdapter;
    private final FlatEditActivity activity;


    public VacantBedsViewBind(FlatEditActivity activity) {
        super(activity);
        this.activity = activity;
    }

    public void show(String selectedRoomSize) {
        txt_option_header.setText("Vacant Beds");
        rv_flat_amenities = activity.findViewById(R.id.rv_roomsize);
        setRoomSize(selectedRoomSize);
        click();
    }

    public void setRoomSize(String selectedRoomSize) {
        activity.viewBind.rlMyflatOption.setVisibility(View.VISIBLE);
        rv_flat_amenities.setVisibility(View.VISIBLE);
        ArrayList<AmenitiesItem> items = new ArrayList<>();
        int total = Integer.parseInt(activity.viewBind.txtTotalBeds.getText().toString());
        for (int i = 1; i < total; i++) {
            AmenitiesItem temp = new AmenitiesItem();
            temp.setSelected(selectedRoomSize.equals("" + i));
            temp.setId(i);
            temp.setName("" + i);
            items.add(temp);
        }
        setAdapter(items);
    }

    private void setAdapter(ArrayList<AmenitiesItem> items) {
        rv_flat_amenities.setLayoutManager(new LinearLayoutManager(activity));
        vacantBedsAdapter = new VacantBedsAdapter(activity, rv_flat_amenities, items);
        rv_flat_amenities.setAdapter(vacantBedsAdapter);
    }

    private void click() {
        img_amenitites_cross.setOnClickListener(view -> {
            int selectedQuantity = 0;
            String adapterQuantity = vacantBedsAdapter.getBeds();
            if (!adapterQuantity.isEmpty())
                selectedQuantity = Integer.parseInt(adapterQuantity);
            rv_flat_amenities.setVisibility(View.GONE);
            activity.viewBind.rlMyflatOption.setVisibility(View.GONE);
            activity.viewBind.txtVacantBeds.setText("" + selectedQuantity);
            activity.dataBind.setCompleteCount();
        });
    }
}
