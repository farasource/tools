package com.android.bahaar;

/**
 * on 04/14/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;


public class TinyDB {
    private SharedPreferences preferences;
    private static TinyDB tinyDB;

    public static TinyDB getInstance(Context context){
        if(tinyDB == null){
            tinyDB = new TinyDB(context);
        }
        return tinyDB;
    }

    public TinyDB(Context appContext) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public int getInt(String key,int defValue) {
        return this.preferences.getInt(key, defValue);
    }

    public long getLong(String key, long defaultValue) {
        return this.preferences.getLong(key, defaultValue);
    }

    public String getString(String key) {
        return this.preferences.getString(key, "");
    }


    public boolean getBoolean(String key,boolean defValue) {
        return this.preferences.getBoolean(key, defValue);
    }

    public void putInt(String key, int value) {
        this.checkForNullKey(key);
        this.preferences.edit().putInt(key, value).apply();
    }


    public void putLong(String key, long value) {
        this.checkForNullKey(key);
        this.preferences.edit().putLong(key, value).apply();
    }

    public void putString(String key, String value) {
        this.checkForNullKey(key);
        this.checkForNullValue(value);
        this.preferences.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        this.checkForNullKey(key);
        this.preferences.edit().putBoolean(key, value).apply();
    }

    public void checkForNullKey(String key) {
        if(key == null) {
            throw new NullPointerException();
        }
    }

    public void checkForNullValue(String value) {
        if(value == null) {
            throw new NullPointerException();
        }
    }

}
