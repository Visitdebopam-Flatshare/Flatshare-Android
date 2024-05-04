package com.joinflatshare.ui.flat.details;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatDetailsBinding;
import com.joinflatshare.pojo.flat.FlatResponse;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.util.ArrayList;

public class FlatDetailsActivity extends BaseActivity {
    private ActivityFlatDetailsBinding viewBind;
    protected FlatDetailsViewBind dataBinder;
    protected FlatDetailsListener listener;
    protected FlatDetailsApiController apiController;
    protected FlatBottomViewHandler flatBottomViewHandler;
    protected ArrayList<String> image = new ArrayList<>();
    protected String phone = "";
    public FlatResponse flatResponse;
    protected String chatConnectionType = "";
    protected String chatConnectionForDialogMatch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityFlatDetailsBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        init();
        MixpanelUtils.INSTANCE.onScreenOpened("View Flat");
    }

    private void init() {
        dataBinder = new FlatDetailsViewBind(this, viewBind);
        apiController = new FlatDetailsApiController(this, viewBind);
        flatBottomViewHandler = new FlatBottomViewHandler(this, viewBind);
        phone = "632a7a7c7223132de0462c63";//getIntent().getStringExtra("phone");
        apiController.getFlat();
    }

    @Override
    public void onBackPressed() {
        if (apiController.getCallbackIntent() != null)
            setResult(RESULT_OK, apiController.getCallbackIntent());
        super.onBackPressed();
    }
}
