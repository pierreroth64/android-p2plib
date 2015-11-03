/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Storage with stores data into android preferences
 */
public class P2PPrefsStorage implements P2PStorageProvider {

    final static private String KEY_PREF_P2P_USERNAME = "pref_p2p_username";
    final static private String KEY_PREF_P2P_PASSWORD = "pref_p2p_password";

    private Context mContext;

    public P2PPrefsStorage(Context context) {
        mContext = context;
    }

    @Override
    public void clearCredentials() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_P2P_USERNAME, "");
        editor.putString(KEY_PREF_P2P_PASSWORD, "");
        editor.commit();
    }

    @Override
    public void storeCredentials(String username, String password) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_P2P_USERNAME, username);
        editor.putString(KEY_PREF_P2P_PASSWORD, password);
        editor.commit();
    }

    @Override
    public String getUsername() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(KEY_PREF_P2P_USERNAME, "");
    }

    @Override
    public String getPassword() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(KEY_PREF_P2P_PASSWORD, "");

    }
}