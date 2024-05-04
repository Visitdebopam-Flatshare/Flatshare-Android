package com.joinflatshare.ui.flat.flat_profile;

import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_AMENITY;
import static com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant.VIEW_CONSTANT_LOCATION;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.flat.flatoptions.FlatOptionActivity;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;
import com.joinflatshare.utils.system.Prefs;

public class MyFlatActivity extends BaseActivity {
    public MyFlatViewBind dataBind;
    public FlatOptionActivity flatOption;
    public ActivityFlatBinding viewBind;
    public MyFlatData myFlatData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityFlatBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        showTopBar(this, true, "Flat", 0, 0);
        dataBind = new MyFlatViewBind(this, viewBind);
        new MyFlatListener(this, viewBind, dataBind);
        flatOption = new FlatOptionActivity(this, true, null);
        getMyFlat();
        MixpanelUtils.INSTANCE.onScreenOpened("My Flat");
    }

    public void getMyFlat() {
        baseApiController.getFlat(true, AppConstants.loggedInUser.getId(), resp -> {
            setResponse();
            new Handler(Looper.getMainLooper()).postDelayed(() -> dataBind.setFlatmates(), 0);
        });
    }

    public void setResponse() {
        myFlatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        flatOption.dialogFlatOptions.setFlat(myFlatData);
        dataBind.setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getString(Prefs.PREFS_KEY_GET_FLAT_REQUIRED).equals("1"))
            getMyFlat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode >= VIEW_CONSTANT_AMENITY && requestCode <= VIEW_CONSTANT_LOCATION) {
                dataBind.setData();
            }
        }
    }
}
