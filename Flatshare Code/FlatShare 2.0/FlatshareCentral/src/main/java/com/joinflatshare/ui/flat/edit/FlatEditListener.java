package com.joinflatshare.ui.flat.edit;

import static com.joinflatshare.constants.RequestCodeConstants.REQUEST_CODE_LOCATION;
import static com.joinflatshare.utils.amazonaws.AmazonUploadFile.AWS_TYPE_FLAT_IMAGE;
import static com.joinflatshare.utils.amazonaws.AmazonUploadFile.REQUEST_CODE_SUCCESS;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.debopam.progressdialog.DialogCustomProgress;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityFlatEditBinding;
import com.joinflatshare.chat.SendBirdFlatChannel;
import com.joinflatshare.chat.SendBirdFlatmateChannel;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.AppData;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.customviews.dropdown.ShowDropDown;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.pojo.flat.CreateFlatRequest;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.user.ModelLocation;
import com.joinflatshare.ui.flat.flatoptions.FlatOptionActivity;
import com.joinflatshare.ui.flat.flatoptions.FlatOptionConstant;
import com.joinflatshare.ui.flat.flatoptions.view_roomsize.RoomSizeViewBind;
import com.joinflatshare.ui.flat.flatoptions.view_roomtype.RoomTypeViewBind;
import com.joinflatshare.utils.amazonaws.AmazonDeleteFile;
import com.joinflatshare.utils.amazonaws.AmazonUploadFile;
import com.joinflatshare.utils.google.AutoCompletePlaces;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FlatEditListener implements View.OnClickListener {
    private final FlatEditActivity activity;
    private final ActivityFlatEditBinding viewBind;
    private final FlatEditDataBind dataBind;

    public FlatEditListener(FlatEditActivity activity, ActivityFlatEditBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        dataBind = activity.dataBind;
        manageClicks();
    }

    private void manageClicks() {
        activity.baseViewBinder.btn_back.setOnClickListener(this);
        viewBind.rlFlatLocation.setOnClickListener(this);
        viewBind.edtFlatSociety.setOnClickListener(this);
        viewBind.llTotalBeds.setOnClickListener(this);
        viewBind.rlVacantBeds.setOnClickListener(this);
        viewBind.cardFlatsize.setOnClickListener(this);
        viewBind.cardRoomtype.setOnClickListener(this);
        viewBind.llShowless.setOnClickListener(this);
        viewBind.cardFurnishing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == viewBind.rlFlatLocation.getId()) {
            AutoCompletePlaces.INSTANCE.getPlaces(activity, (intent, requestCode) -> {
                if (requestCode == REQUEST_CODE_LOCATION) {
                    if (intent != null) {
                        activity.latFlatLocation = CommonMethod.INSTANCE.getSerializable(intent, "location", ModelLocation.class);
                        viewBind.txtFlatLocation.setText(activity.latFlatLocation.getName());
                        dataBind.setCompleteCount();
                    }
                }
            });
        } else if (view.getId() == viewBind.edtFlatSociety.getId()) {
            viewBind.imgSearch.setImageResource(R.drawable.ic_search);
            viewBind.imgSearch.setColorFilter(ContextCompat.getColor(activity, R.color.blue_dark));
            viewBind.view2.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_blue_light));
            AutoCompletePlaces.INSTANCE.getPlaces(activity, (intent, requestCode) -> {
                if (requestCode == REQUEST_CODE_LOCATION) {
                    viewBind.imgSearch.setImageResource(R.drawable.ic_search);
                    viewBind.view2.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_hint));
                    if (intent != null) {
                        activity.latSociety = CommonMethod.INSTANCE.getSerializable(intent, "location", ModelLocation.class);
                        viewBind.edtFlatSociety.setText(activity.latSociety.getName());
                        dataBind.setCompleteCount();
                    }
                }
            });
        } else if (view.getId() == viewBind.rlVacantBeds.getId()) {
            dataBind.vacantBedsViewBind.show(viewBind.txtVacantBeds.getText().toString());
        } else if (view.getId() == viewBind.llTotalBeds.getId()) {
            dataBind.totalBedsViewBind.show(viewBind.txtTotalBeds.getText().toString());
        } else if (view.getId() == viewBind.cardRoomtype.getId()) {
            activity.roomTypeViewBind = new RoomTypeViewBind(Objects.requireNonNull(activity.getDialogFlatOptions()), viewBind.txtRoomtype.getText().toString());
        } else if (view.getId() == viewBind.cardFlatsize.getId()) {
            activity.flatsizeViewBind = new RoomSizeViewBind(Objects.requireNonNull(activity.getDialogFlatOptions()), viewBind.txtFlatsize.getText().toString());
        } else if (activity.baseViewBinder.btn_back.getId() == view.getId()) {
            checkForUnsavedData();
        } else if (viewBind.cardFurnishing.getId() == view.getId()) {
            FlatOptionActivity flatOptionActivity = new FlatOptionActivity(activity, false, (intent, requestCode) -> {
                if (requestCode == 1) {
                    MyFlatData flat = CommonMethod.INSTANCE.getSerializable(intent, "flat", MyFlatData.class);
                    if (flat != null && flat.getFlatProperties() != null)
                        viewBind.txtFurnishing.setText(TextUtils.join("", flat.getFlatProperties().getFurnishing()));
                }
            });
            MyFlatData flatData = new MyFlatData();
            flatData.isMateSearch().setValue(true);
            flatData.getFlatProperties().setFurnishing(Collections.singletonList(viewBind.txtFurnishing.getText().toString()));
            flatOptionActivity.dialogFlatOptions.setFlat(flatData);
            flatOptionActivity.selectedViewConstant = FlatOptionConstant.VIEW_CONSTANT_FURNISHING;
            flatOptionActivity.switchView();
        } else if (viewBind.llShowless.getId() == view.getId()) {
            String text = viewBind.txtShowless.getText().toString();
            if (text.equals("Show Less")) {
                viewBind.txtShowless.setText("Show More");
                viewBind.imgArrowAmenities.setImageResource(R.drawable.arrow_down_black);
                dataBind.loadAmenities(true);
            } else {
                viewBind.txtShowless.setText("Show Less");
                viewBind.imgArrowAmenities.setImageResource(R.drawable.ic_arrow_up_black);
                dataBind.loadAmenities(false);
            }
        }
    }

    void alterQuantity(LinearLayout anchor) {
        ShowDropDown dropDown = new ShowDropDown(activity);
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
            items.add("" + i);
        dropDown.show(anchor, items, (view, dropDownPosition) -> {
            int selectedQuantity = Integer.parseInt(items.get(dropDownPosition));
            if (anchor.getId() == viewBind.llTotalBeds.getId())
                viewBind.txtTotalBeds.setText("" + selectedQuantity);
            else {
                int tquantity = Integer.parseInt(viewBind.txtTotalBeds.getText().toString());
                if (selectedQuantity > tquantity) {
                    AlertDialog.showAlert(activity, "Vacant beds cannot be more than total beds", "OK");
                } else {
                    viewBind.txtVacantBeds.setText("" + selectedQuantity);
                }
            }
            dataBind.setCompleteCount();
        });
    }

    private void validate() {
        if (viewBind.edtFlatName.getText().toString().trim().isEmpty()) {
            AlertDialog.showAlert(activity, "Please enter flat name");
            return;
        }
        int size = viewBind.edtFlatName.getText().toString().trim().length();
        if (size < 4 || size > 20) {
            AlertDialog.showAlert(activity, "Flat name must be 4-20 characters");
            return;
        }
        MixpanelUtils.INSTANCE.onButtonClicked("Edit Flat Profile");
        prepareImages();
    }

    private void prepareImages() {
        // Check for images to delete
        if (dataBind.deletedUserImages.size() > 0) {
            activity.apiManager.showProgress();
            AmazonDeleteFile deleteFile = new AmazonDeleteFile();
            deleteImages(deleteFile, (intent, requestCode) -> {
                if (requestCode == 1) {
                    prepareUpload();
                }
            });
        } else
            prepareUpload();
    }

    private void deleteImages(AmazonDeleteFile deleteFile, OnUiEventClick onUiEventClick) {
        final String path = dataBind.deletedUserImages.get(0);
        deleteFile.delete(path, (intent, requestCode) -> {
            if (requestCode == REQUEST_CODE_SUCCESS) {
                dataBind.deletedUserImages.remove(0);
                dataBind.apiUserImages.remove(path);
                if (dataBind.deletedUserImages.size() > 0)
                    deleteImages(deleteFile, onUiEventClick);
                else onUiEventClick.onClick(null, 1);
            } else {
                AlertDialog.showAlert(activity, "Failed to delete image");
                DialogCustomProgress.INSTANCE.hideProgress(activity);
            }
        });
    }

    private void prepareUpload() {
        MyFlatData flatData = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        if (flatData == null || flatData.getId().isEmpty()) {
            CreateFlatRequest req = new CreateFlatRequest();
            req.setName(viewBind.edtFlatName.getText().toString().trim());
            req.setId(AppConstants.loggedInUser.getId());
            req.getFlatProperties().setRentperPerson(AppData.INSTANCE.getFlatData().getRentRange().getStartRange());
            activity.apiManager.createFlat(req, response -> {
                if (dataBind.addedUserImages.size() > 0) {
                    activity.apiManager.showProgress();
                    uploadImages((intent, requestCode) -> {
                        if (requestCode == 1) {
                            callAPi();
                        }
                    });
                } else callAPi();
            });
        } else {
            if (dataBind.addedUserImages.size() > 0) {
                activity.apiManager.showProgress();
                uploadImages((intent, requestCode) -> {
                    if (requestCode == 1) {
                        callAPi();
                    }
                });
            } else callAPi();
        }
    }

    private void uploadImages(OnUiEventClick onUiEventClick) {
        final String path = dataBind.addedUserImages.get(0);
        new AmazonUploadFile().upload(new File(path),
                AWS_TYPE_FLAT_IMAGE, (intent, requestCode) -> {
                    if (requestCode == REQUEST_CODE_SUCCESS) {
                        String serverPath = intent.getStringExtra("localpath");
                        dataBind.apiUserImages.add(serverPath);
                        dataBind.addedUserImages.remove(0);
                        if (dataBind.addedUserImages.size() > 0)
                            uploadImages(onUiEventClick);
                        else onUiEventClick.onClick(null, 1);
                    } else {
                        AlertDialog.showAlert(activity, "Failed to upload image");
                        DialogCustomProgress.INSTANCE.hideProgress(activity);
                    }
                });
    }

    private void callAPi() {
        DialogCustomProgress.INSTANCE.hideProgress(activity);
        MyFlatData myFlat = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        String oldname = "";
        if (!myFlat.getName().equals(viewBind.edtFlatName.getText().toString().trim()))
            oldname = myFlat.getName();
        myFlat.setName(viewBind.edtFlatName.getText().toString().trim());
        myFlat.getFlatProperties().setLocation(activity.latFlatLocation);
        myFlat.getFlatProperties().setSociety(activity.latSociety);
        myFlat.setNorms(viewBind.edtEdtFlatNorms.getText().toString().trim());
        myFlat.getFlatProperties().setRoomType(viewBind.txtRoomtype.getText().toString());
        myFlat.getFlatProperties().setFlatsize(viewBind.txtFlatsize.getText().toString());
        myFlat.getFlatProperties().getBeds().setVacant(Integer.parseInt(viewBind.txtVacantBeds.getText().toString()));
        myFlat.getFlatProperties().getBeds().setTotal(Integer.parseInt(viewBind.txtTotalBeds.getText().toString()));
        myFlat.getFlatProperties().setAmenities(activity.selectedAmenityList);
        myFlat.getFlatProperties().setFurnishing(Collections.singletonList(viewBind.txtFurnishing.getText().toString()));
        myFlat.setImages(dataBind.apiUserImages);
        final String oldName = oldname;
        activity.apiManager.updateFlat(true, myFlat, response -> {
            if (!oldName.isEmpty())
                callSendBird(myFlat.getMongoId(), oldName, viewBind.edtFlatName.getText().toString().trim());
            CommonMethod.INSTANCE.makeToast("Flat profile updated");
            activity.setResult(Activity.RESULT_OK);
            AppConstants.isFeedReloadRequired = true;
            CommonMethod.INSTANCE.finishActivity(activity);
        });
    }

    private void callSendBird(String flatId, String oldName, String newName) {
        if (AppConstants.isSendbirdLive) {
            updateSendBirdFlatChannel(flatId, oldName, newName);
        }
    }

    private void updateSendBirdFlatChannel(String flatId, String oldName, String newName) {
        SendBirdFlatChannel flatChannel = new SendBirdFlatChannel(activity);
        flatChannel.updateFlatName(flatId, newName);

        SendBirdFlatmateChannel flatmateChannel = new SendBirdFlatmateChannel(activity);
        flatmateChannel.updateFlatName(oldName, newName);
    }

    private void checkForUnsavedData() {
        MyFlatData myFlat = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        activity.selectedAmenityList = dataBind.amenityAdapter.getSelectedAmenityList();
        String newName = viewBind.edtFlatName.getText().toString().trim();
        ModelLocation newFlatLocation = activity.latFlatLocation;
        ModelLocation newSociety = activity.latSociety;
        String newNorms = viewBind.edtEdtFlatNorms.getText().toString().trim();
        String newRoomType = viewBind.txtRoomtype.getText().toString();
        String newFlatSize = viewBind.txtFlatsize.getText().toString();
        int newVacantBeds = Integer.parseInt(viewBind.txtVacantBeds.getText().toString());
        int newTotalBeds = Integer.parseInt(viewBind.txtTotalBeds.getText().toString());
        String newFurnishing = viewBind.txtFurnishing.getText().toString();

        boolean hasChanged = false;
        if (myFlat == null || myFlat.getId().isEmpty()) {
            if (!newName.isEmpty() || newFlatLocation != null || newSociety != null || !newNorms.isEmpty()
                    || !newRoomType.isEmpty() || !newFlatSize.isEmpty() || newVacantBeds > 0
                    || newTotalBeds > 0 || dataBind.addedUserImages.size() > 0)
                hasChanged = true;
        } else {
            if (myFlat.getFlatProperties().getLocation() == null && newFlatLocation != null) {
                hasChanged = true;
                MixpanelUtils.INSTANCE.sendToMixPanel("Flat Location Entered");
            } else if ((myFlat.getFlatProperties().getLocation() != null && newFlatLocation != null)
                    && !myFlat.getFlatProperties().getLocation().getName().equals(newFlatLocation.getName())) {
                hasChanged = true;
                MixpanelUtils.INSTANCE.sendToMixPanel("Flat Location Entered");
            } else if (myFlat.getFlatProperties().getSociety() == null && newSociety != null)
                hasChanged = true;
            else if ((myFlat.getFlatProperties().getSociety() != null && newSociety != null)
                    && !myFlat.getFlatProperties().getSociety().getName().equals(newSociety.getName()))
                hasChanged = true;
            else if (!newName.equals(myFlat.getName() == null ? "" : myFlat.getName()))
                hasChanged = true;
            else if (!newNorms.equals(myFlat.getNorms() == null ? "" : myFlat.getNorms()))
                hasChanged = true;
            else if (!newRoomType.equals(myFlat.getFlatProperties().getRoomType() == null ? "" : myFlat.getFlatProperties().getRoomType()))
                hasChanged = true;
            else if (!newFlatSize.equals(myFlat.getFlatProperties().getFlatsize() == null ? "" : myFlat.getFlatProperties().getFlatsize()))
                hasChanged = true;
            else if (newVacantBeds != myFlat.getFlatProperties().getBeds().getVacant())
                hasChanged = true;
            else if (newTotalBeds != myFlat.getFlatProperties().getBeds().getTotal())
                hasChanged = true;
            else if (dataBind.addedUserImages.size() > 0)
                hasChanged = true;
            else if ((myFlat.getFlatProperties().getAmenities() == null && activity.selectedAmenityList.size() > 0)
                    || !myFlat.getFlatProperties().getAmenities().containsAll(activity.selectedAmenityList)
                    || !activity.selectedAmenityList.containsAll(myFlat.getFlatProperties().getAmenities())) {
                hasChanged = true;
            } else if ((myFlat.getFlatProperties().getFurnishing() == null && newFurnishing.isEmpty())
                    || !myFlat.getFlatProperties().getFurnishing().contains(newFurnishing)) {
                hasChanged = true;
            }
        }
        if (!hasChanged) {
            if (dataBind.deletedUserImages.size() > 0)
                hasChanged = true;
            else if (dataBind.addedUserImages.size() > 0)
                hasChanged = true;
        }
        if (hasChanged)
            validate();
        else CommonMethod.INSTANCE.finishActivity(activity);
    }
}
