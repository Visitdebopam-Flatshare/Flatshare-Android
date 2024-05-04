package com.joinflatshare.ui.flat.edit.options.total_beds;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.constants.AppData;
import com.joinflatshare.pojo.amenities.AmenitiesItem;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;
import com.joinflatshare.ui.flat.edit.options.FlatOptionViewBind;

import java.util.ArrayList;

public class TotalBedsViewBind extends FlatOptionViewBind {
    // View 2
    public RecyclerView rv_flat_amenities;
    public TotalBedsAdapter totalBedsAdapter;
    private final FlatEditActivity activity;


    public TotalBedsViewBind(FlatEditActivity activity) {
        super(activity);
        this.activity = activity;
    }

    public void show(String selectedRoomSize) {
        txt_option_header.setText("Total Beds");
        rv_flat_amenities = activity.findViewById(R.id.rv_roomsize);
        setRoomSize(selectedRoomSize);
        click();
    }

    public void setRoomSize(String selectedRoomSize) {
        activity.viewBind.rlMyflatOption.setVisibility(View.VISIBLE);
        rv_flat_amenities.setVisibility(View.VISIBLE);
        ArrayList<AmenitiesItem> items = new ArrayList<>();
        for (int i = 2; i <= AppData.INSTANCE.getFlatData().getTotalBeds(); i++) {
            AmenitiesItem temp = new AmenitiesItem();
            temp.setSelected(selectedRoomSize.equals("" + i));
            temp.setId(i);
            temp.setName(i == AppData.INSTANCE.getFlatData().getTotalBeds() ? "" + i + "+" : "" + i);
            items.add(temp);
        }
        setAdapter(items);
    }

    private void setAdapter(ArrayList<AmenitiesItem> items) {
        rv_flat_amenities.setLayoutManager(new LinearLayoutManager(activity));
        totalBedsAdapter = new TotalBedsAdapter(activity, rv_flat_amenities, items);
        rv_flat_amenities.setAdapter(totalBedsAdapter);
    }

    private void click() {
        img_amenitites_cross.setOnClickListener(view -> {
            int selectedQuantity = 0;
            String adapterQuantity = totalBedsAdapter.getBeds();
            if (adapterQuantity.equals("10+"))
                adapterQuantity = "10";
            if (!adapterQuantity.isEmpty())
                selectedQuantity = Integer.parseInt(adapterQuantity);
            rv_flat_amenities.setVisibility(View.GONE);
            activity.viewBind.rlMyflatOption.setVisibility(View.GONE);
            activity.viewBind.txtTotalBeds.setText("" + selectedQuantity);

            // Vacant Beds calculation
            int vacant = Integer.parseInt(activity.viewBind.txtVacantBeds.getText().toString());
            if (selectedQuantity > 1) {
                activity.viewBind.llVacantBeds.setVisibility(View.VISIBLE);
                if (vacant > selectedQuantity)
                    vacant = selectedQuantity - 1;
                else vacant = 1;
                activity.viewBind.txtVacantBeds.setText("" + vacant);
            } else {
                activity.viewBind.llVacantBeds.setVisibility(View.GONE);
                activity.viewBind.txtVacantBeds.setText("0");
            }
            activity.dataBind.setCompleteCount();
        });
    }
}
