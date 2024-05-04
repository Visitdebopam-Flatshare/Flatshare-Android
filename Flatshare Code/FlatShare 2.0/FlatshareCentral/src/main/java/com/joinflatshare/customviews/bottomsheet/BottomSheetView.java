package com.joinflatshare.customviews.bottomsheet;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;

import java.util.ArrayList;

public class BottomSheetView {
    private Activity activity;
    private ArrayList<ModelBottomSheet> modelBottomSheets;
    private BottomSheetAdapter adapter;
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;

    public BottomSheetView(Activity activity, ArrayList<ModelBottomSheet> modelBottomSheets) {
        this.activity = activity;
        this.modelBottomSheets = modelBottomSheets;
        init();
    }

    private void init() {
        bottomSheetBehavior = BottomSheetBehavior.from(activity.findViewById(R.id.bottomSheet));
        RecyclerView rv = activity.findViewById(R.id.rv_bottomsheet);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new BottomSheetAdapter(activity, modelBottomSheets);
        rv.setAdapter(adapter);
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void show(OnitemClick onitemClick) {
        activity.findViewById(R.id.bottomSheet).setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED),500);
        adapter.setClickListener((view, position) -> {
            onitemClick.onitemclick(view, position);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }
}
