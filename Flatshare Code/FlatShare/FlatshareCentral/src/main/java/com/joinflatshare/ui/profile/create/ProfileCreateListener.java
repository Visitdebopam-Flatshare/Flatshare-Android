package com.joinflatshare.ui.profile.create;

import android.app.DatePickerDialog;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding;
import com.joinflatshare.customviews.alert.AlertDialog;
import com.joinflatshare.customviews.interests.InterestsView;
import com.joinflatshare.pojo.config.RentRange;
import com.joinflatshare.pojo.user.Name;
import com.joinflatshare.utils.helper.CommonMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class ProfileCreateListener implements View.OnClickListener {
    private final ProfileCreateActivity activity;
    private final ActivityProfileCreateBinding viewBind;
    private String gender = "", dob = "";

    public ProfileCreateListener(ProfileCreateActivity activity, ActivityProfileCreateBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        manageClicks();
    }

    private void manageClicks() {
        viewBind.cardProfileMale.setOnClickListener(this);
        viewBind.cardProfileFemale.setOnClickListener(this);
        viewBind.txtProfileDob.setOnClickListener(this);
        viewBind.btnProfileCreate.setOnClickListener(this);
        viewBind.llProfileInterests.setOnClickListener(this);
        viewBind.llProfileLanguages.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.card_profile_female) {
            viewBind.cardProfileFemale.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_bg_black));
            viewBind.txtFemale.setTextColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
            viewBind.cardProfileMale.setCardBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
            viewBind.txtMale.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));
            viewBind.cardProfileMale.invalidate();
            viewBind.cardProfileFemale.invalidate();
            gender = "Female";
        } else if (id == R.id.card_profile_male) {
            viewBind.cardProfileMale.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_bg_black));
            viewBind.txtMale.setTextColor(ContextCompat.getColor(activity, R.color.color_text_secondary));
            viewBind.cardProfileFemale.setCardBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
            viewBind.txtFemale.setTextColor(ContextCompat.getColor(activity, R.color.color_text_primary));
            viewBind.cardProfileMale.invalidate();
            viewBind.cardProfileFemale.invalidate();
            gender = "Male";
        } else if (id == R.id.txt_profile_dob) {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(activity,
                    android.R.style.Theme_Holo_Dialog, (view12, year, month, dayOfMonth) -> {
                dob = String.format("%04d/%02d/%02d", year, (month + 1), dayOfMonth);
                String day = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.set(Calendar.YEAR, year);
                dobCalendar.set(Calendar.MONTH, month);
                dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.add(Calendar.YEAR, -15);
                if (dobCalendar.getTime().after(calendar.getTime())) {
                    CommonMethods.makeToast("You need to be above the age of fifteen to join "
                            + activity.getString(R.string.app_name));
                    return;
                }
                viewBind.txtProfileDob.setText(day);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            dpd.show();
        } else if (id == R.id.btn_profile_create) {
            if (viewBind.txtProfileFname.getText().toString().isEmpty())
                CommonMethods.makeToast("Please enter your first name");
            else if (viewBind.txtProfileFname.getText().toString().length() == 1)
                CommonMethods.makeToast("First name must be minimum 2 characters long");
            else if (viewBind.txtProfileLname.getText().toString().isEmpty())
                CommonMethods.makeToast("Please enter your last name");
            else if (viewBind.txtProfileLname.getText().toString().length() == 1)
                CommonMethods.makeToast("Last name must be minimum 2 characters long");
            else if (viewBind.txtProfileDob.getText().toString().isEmpty())
                CommonMethods.makeToast("Please enter your DOB");
            else if (gender.isEmpty())
                CommonMethods.makeToast("Please select your gender");
            else if (viewBind.txtProfileInterest.getText().toString().isEmpty())
                CommonMethods.makeToast("Please select your interests");
            else if (viewBind.txtProfileLanguages.getText().toString().isEmpty())
                CommonMethods.makeToast("Please select your languages");
            else {
                AlertDialog.showAlert(activity, "Heads Up", "You won't be able to edit your name, birthday, and gender. Is it confirmed?",
                        "Yes", "No", (intent, requestCode) -> {
                            if (requestCode == 1) {
                                Name name = new Name();
                                name.setFirstName(viewBind.txtProfileFname.getText().toString());
                                name.setLastName(viewBind.txtProfileLname.getText().toString());
                                activity.modelUser.setName(name);
                                activity.modelUser.setDob(dob);
                                activity.modelUser.setGender(gender);
                                activity.modelUser.getFlatProperties().setRentRange(new RentRange());
                                // interests
                                ArrayList<String> items = new ArrayList<>();
                                String text = viewBind.txtProfileInterest.getText().toString();
                                String[] split = text.split(Pattern.quote(", "));
                                if (split.length > 0) {
                                    for (String txt : split) {
                                        items.add(txt);
                                    }
                                }
                                activity.modelUser.getFlatProperties().setInterests(items);
                                // languages
                                items = new ArrayList<>();
                                text = viewBind.txtProfileLanguages.getText().toString();
                                split = text.split(Pattern.quote(", "));
                                if (split.length > 0) {
                                    for (String txt : split) {
                                        items.add(txt);
                                    }
                                }
                                activity.modelUser.getFlatProperties().setLanguages(items);
                                if (activity.getTemporaryLocation() == null)
                                    activity.checkLocation();
                                else {
                                    activity.modelUser.setLocation(activity.getTemporaryLocation());
                                    activity.setTemporaryLocation(null);
                                    activity.updateUser();
                                }
                            }
                        });
            }
        } else if (id == R.id.ll_profile_interests) {
            InterestsView interestsView = new InterestsView(activity, InterestsView.VIEW_TYPE_INTERESTS, viewBind.txtProfileInterest);
            interestsView.assignCallback(activity);
            interestsView.show();
        } else if (id == R.id.ll_profile_languages) {
            InterestsView interestsView;
            interestsView = new InterestsView(activity, InterestsView.VIEW_TYPE_LANGUAGES, viewBind.txtProfileLanguages);
            interestsView.assignCallback(activity);
            interestsView.show();
        }
    }
}
