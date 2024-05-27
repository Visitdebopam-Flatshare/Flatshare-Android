package com.joinflatshare.ui.invite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.debopam.progressdialog.DialogCustomProgress;
import com.joinflatshare.FlatshareCentral.databinding.ActivityInviteBinding;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.ContactConstants;
import com.joinflatshare.constants.IntentFilterConstants;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.services.MutualContactHandler;
import com.joinflatshare.utils.helper.CommonMethod;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InviteListener {
    private final InviteActivity activity;
    private final ActivityInviteBinding viewBind;
    private final String TAG = InviteListener.class.getSimpleName();

    public InviteListener(InviteActivity activity, ActivityInviteBinding viewBind) {
        this.activity = activity;
        this.viewBind = viewBind;
        init();
    }

    private void init() {
        getContacts();
        setTextListener();
    }

    private void getContacts() {
        boolean isContactFetchJobRunning = MutualContactHandler.INSTANCE.isJobRunning();
        CommonMethod.INSTANCE.makeLog(TAG, "Has Fetched Contacts " + isContactFetchJobRunning);
        if (!isContactFetchJobRunning) {
            if (ContactConstants.allContacts.size() == 0) {
                activity.apiManager.showProgress();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    CommonMethod.INSTANCE.makeLog(TAG, "Fetching Contacts");
                    LocalBroadcastManager.getInstance(activity).registerReceiver(contactJobCompleteReceiver, new IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_CONTACTS_LOADED));
                    MutualContactHandler.INSTANCE.getContacts(activity);
                }, 2000);
            } else {
                if (ContactConstants.mutualContacts.size() == 0) {
                    CommonMethod.INSTANCE.makeLog(TAG, "Sending Contacts");
                    activity.contactsApiController.sendContacts();
                } else {
                    activity.contactsApiController.createMutualUserList();
                }
            }
            viewBind.edtInvite.requestFocus();
            viewBind.edtInvite.requestFocusFromTouch();
        } else {
            activity.apiManager.showProgress();
            LocalBroadcastManager.getInstance(activity).registerReceiver(contactJobCompleteReceiver, new IntentFilter(IntentFilterConstants.INTENT_FILTER_CONSTANT_CONTACTS_LOADED));
        }
    }

    private void setTextListener() {
        viewBind.edtInvite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                if (name.length() > 2) {
                    filterUsers(name);
                } else {
                    if (activity.adapter.getItemCount() > 0) {
                        activity.getDisplayUsers().clear();
                        activity.adapter.reload();
                    }
//                    activity.contactsApiController.displayMutualList();
                }
            }
        });
    }

    private void filterUsers(String query1) {
        String myId = AppConstants.loggedInUser.getId();
        if (query1.length() == 10 && query1.equals(myId))
            return;
        final String query = query1.toLowerCase(Locale.ROOT);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            ArrayList<String> onlyNumbers = new ArrayList<>();
            for (User contact : ContactConstants.allContacts) {
                if (!onlyNumbers.contains(contact.getId()) &&
                        (contact.getId().equals(query) || contact.getName().getFirstName().toLowerCase(Locale.ROOT).contains(query)
                                || contact.getName().getLastName().toLowerCase(Locale.ROOT).contains(query))) {
                    if (!activity.getNameHashMap().containsKey(contact.getId()))
                        activity.getNameHashMap().put(contact.getId(), contact.getName());
                    onlyNumbers.add(contact.getId());
                }
            }
            if (query.length() == 10 && !onlyNumbers.contains(query))
                onlyNumbers.add(query);
            if (onlyNumbers.size() > 0)
                handler.post(() -> activity.apiController.invitedUsers(onlyNumbers));
        });
    }

    final BroadcastReceiver contactJobCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(contactJobCompleteReceiver);
            DialogCustomProgress.INSTANCE.hideProgress(activity);
            CommonMethod.INSTANCE.makeLog(TAG, "Sending Contacts");
            activity.contactsApiController.sendContacts();
            viewBind.edtInvite.requestFocus();
            viewBind.edtInvite.requestFocusFromTouch();
        }
    };

}
