package com.joinflatshare.utils.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.constants.ContactConstants;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.interfaces.OnUiEventClick;
import com.joinflatshare.pojo.user.Name;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.services.MutualContactHandler;
import com.joinflatshare.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContactsLookup {
    private final BaseActivity activity;
    private Dialog progress;
    private static final String[] PROJECTION =
            {
                    ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Data.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts.LOOKUP_KEY
            };
    // Defines the selection clause
    private static final String SELECTION = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " LIKE ?";
    // Defines the array to hold the search criteria
    private String[] selectionArgs;
    /*
     * Defines a string that specifies a sort order of MIME type
     */
    private static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME_PRIMARY;

    // Get the current user phone number to avoid inviting self

    public ContactsLookup(BaseActivity activity) {
        this.activity = activity;
    }

    public String getNameFromNumber(String id) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(id));
        Cursor cursor = activity.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return id;
    }

    public void getContacts(String query, final OnUiEventClick onUiEventClick) {
        selectionArgs = new String[]{"%" + query + "%"};
        loadCursor(onUiEventClick);
    }

    @SuppressLint("Range")
    private void loadCursor(final OnUiEventClick onUiEventClick) {
        ArrayList<User> foundContacts = new ArrayList<>();
        Cursor cursor = activity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<String> foundNames = new ArrayList<>();
            String loggedInUserId = FlatShareApplication.Companion.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID);
            do {
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                String number = "";
                if (!foundNames.contains(name) && hasPhone.equals("1")) {
                    foundNames.add(name);
                    Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
                    if (phones != null && phones.moveToFirst()) {
                        number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        number = trimNumber(number);
                        if (!number.equals(loggedInUserId)) {
                            User user = new User();
                            user.setId(number);
                            Name name1 = new Name();
                            name1.setFirstName(name);
                            user.setName(name1);
                            foundContacts.add(user);
                        }
                        phones.close();
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        Intent intent = new Intent();
        intent.putExtra("contact", foundContacts);
        onUiEventClick.onClick(intent, 10);
    }

    @SuppressLint("Range")
    public void getContactList(final OnUiEventClick onUiEventClick) {
        if (!activity.isFinishing() && !activity.isDestroyed())
            activity.apiManager.showProgress();
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            if (ContactConstants.allContacts.size() == 0) {
                MutualContactHandler.INSTANCE.getContacts(activity);
            }
            handler.post(() -> {
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                onUiEventClick.onClick(null, 10);
            });
        });
    }

    private String trimNumber(String number) {
        String w = "";
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) != ' ')
                w += number.charAt(i);
        }
        return checkNumberDigits(w);
    }

    private String checkNumberDigits(String number) {
        if (number.length() > 10) {
            int extraDigits = number.length() - 10;
            number = number.substring(extraDigits);
        }
        return number;
    }
}