package com.joinflatshare.ui.profile.create;

import android.text.InputFilter;

import com.joinflatshare.FlatshareCentral.databinding.ActivityProfileCreateBinding;
import com.joinflatshare.utils.helper.DateUtils;

public class ProfileCreateDataBinder {
    protected ProfileCreateActivity activity;
    protected ActivityProfileCreateBinding viewBind;

    public ProfileCreateDataBinder(ProfileCreateActivity activity, ActivityProfileCreateBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        setFilter();
    }

    void setData() {
        if (!activity.isNewUser) {
            viewBind.btnProfileCreate.setText("Save");
            viewBind.txtProfileFname.setText(activity.modelUser.getName().getFirstName());
            viewBind.txtProfileLname.setText(activity.modelUser.getName().getLastName());
            viewBind.txtProfileDob.setText(DateUtils.INSTANCE.convertToAppFormat(activity.modelUser.getDob()));
            if (activity.modelUser.getGender().equalsIgnoreCase("male"))
                viewBind.cardProfileMale.performClick();
            else if (activity.modelUser.getGender().equalsIgnoreCase("female"))
                viewBind.cardProfileFemale.performClick();
        }
    }

    private void setFilter() {
        InputFilter[] filtertxt = new InputFilter[1];
        filtertxt[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                char[] acceptedChars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
                for (int index = start; index < end; index++) {
                    if (!new String(acceptedChars).contains(String.valueOf(source.charAt(index)))) {
                        return source.length() == 0 ? "" : source.subSequence(0, source.length() - 1);
                    }
                }
            }
            return null;
        };

        viewBind.txtProfileFname.setFilters(new InputFilter[]{filtertxt[0]});
        viewBind.txtProfileLname.setFilters(new InputFilter[]{filtertxt[0]});
    }
}
