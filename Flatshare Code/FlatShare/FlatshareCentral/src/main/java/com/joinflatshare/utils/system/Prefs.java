package com.joinflatshare.utils.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Debopam on 17-10-2017.
 */

public class Prefs {
    /*------------------ALL SHARED MEMORY TAG NAMES HERE-----------------*/
    public static final String PREFS_KEY_GET_FLAT_REQUIRED = "GET_FLAT_REQUIRED";

    public static final String PREFS_KEY_APPEARANCE = "APPEARANCE";
    public static final String PREFS_KEY_API_TOKEN = "API_TOKEN";

    /*------------------ALL SHARED MEMORY TAG NAMES HERE-----------------*/


    static Context context;
    private static Prefs instance = null;

    /**
     * Initialize constructor
     *
     * @param context
     */
    public Prefs(Context context) {
        this.context = context;
    }

    /**
     * Initialize preference object
     *
     * @param _context Context of the current activity
     */
    public static void init(Context _context) {
        instance = new Prefs(_context);
        context = _context;
    }

    /**
     * Return Preference instance
     *
     * @return Current instance of preference
     */
    public static Prefs getInstance() {
        if (instance == null)
            throw new InstantiationError(
                    "Null instance. Should instantiate first with application context");
        return instance;
    }

    /**
     * Store some string in SharedPreferences using a key value and the data
     *
     * @param key
     * @param value
     */

    public void setString(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).commit();
    }

    /**
     * Get string value from SharedPreferences using key value
     *
     * @param key
     * @return a string
     */

    public String getString(String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString(key, "");
    }


    /**
     * Store some boolean value in SharedPreferences using a key value and the data
     *
     * @param key
     * @param value
     */

    public void setBoolean(String key, boolean value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).commit();
    }


    /**
     * Get boolean value from SharedPreferences using key value
     *
     * @param key
     * @param def
     * @return a boolean value
     */

    public boolean getBoolean(String key, boolean def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, def);
    }


    /**
     * Store some integer value in SharedPreferences using a key value and the data
     *
     * @param key
     * @param value
     */

    public void setInt(String key, int value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(key, Integer.toString(value));
    }

    /**
     * Get integer value from SharedPreferences using key value
     *
     * @param key
     * @param def
     * @return a integer value
     */

    public int getInt(String key, int def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(key, Integer.toString(def)));
    }

    /**
     * Store some Long value in SharedPreferences using a key value and the data
     *
     * @param key
     * @param value
     */

    public void setLon(String key, long value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(key, Long.toString(value));
    }

    /**
     * Get Long value from SharedPreferences using key value
     *
     * @param key
     * @param def
     * @return a Long value
     */

    public long getLon(String key, long def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Long.parseLong(prefs.getString(key, Long.toString(def)));
    }

    /**
     * Store some Double value in SharedPreferences using a key value and the data
     *
     * @param key
     * @param value
     */

    public void setDouble(String key, double value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(key, Double.toString(value));
    }

    /**
     * Get Double value from SharedPreferences using key value
     *
     * @param key
     * @return a Double value
     */

    public double getDouble(String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Double.parseDouble(prefs.getString(key, "0"));
    }

    /**
     * Clear all saved preferences
     */
    public void clearPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Clear a particular saved preference
     *
     * @param key Key name
     */
    public void clearPreferenceKey(String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (sp.contains(key)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.commit();
        }
    }
}
