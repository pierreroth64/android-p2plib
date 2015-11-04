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

import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

/**
 * Storage with stores data into android preferences
 */
public class P2PPrefsStorage extends P2PBaseStorage implements P2PStorageProvider {

    protected Context mContext;

    public P2PPrefsStorage(Context context) {
        mContext = context;
    }

    protected void checkNotNullData(String data, String errMessage) throws P2PExceptionFailed {
        if (data == null)
            throw new P2PExceptionFailed(errMessage);
    }

    @Override
    public void clearCredentials() throws P2PExceptionFailed {
        storeCredentials("", "");
    }

    @Override
    public void storeCredentials(String username, String password) throws P2PExceptionFailed {
        storeUsername(username);
        storePassword(password);
    }

    @Override
    public void storeUsername(String username) throws P2PExceptionFailed {
        checkNotNullData(username, "failed to store credentials: username is null");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_P2P_USERNAME, username);
        editor.commit();
    }

    @Override
    public void storePassword(String password) throws P2PExceptionFailed {
        checkNotNullData(password, "failed to store credentials: password is null");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_P2P_PASSWORD, password);
        editor.commit();
    }

    @Override
    public String getUsername() throws P2PExceptionFailed {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(KEY_PREF_P2P_USERNAME, "");
    }

    @Override
    public String getPassword() throws P2PExceptionFailed {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(KEY_PREF_P2P_PASSWORD, "");
    }
}