/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.legrand.android.p2plib.core.exceptions.P2PException;
import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Storage with stores creds as JSON into android preferences
 */
public class P2PJSONPrefsStorage extends P2PBaseStorage implements P2PStorageProvider {

    protected Context mContext;

    public P2PJSONPrefsStorage(Context context) {
        mContext = context;
    }

    protected String buildJSONStringFromCreds(String username, String password) throws P2PExceptionFailed {
        try {
            JSONObject creds = new JSONObject();
            creds.put("username", username);
            creds.put("password", password);
            return creds.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new P2PExceptionFailed("failed to encode JSON object");
        }
    }

    protected Bundle buildCredsBundleFromJSONString(String credsJSON) throws P2PExceptionFailed{
        try {
            JSONObject creds = new JSONObject(credsJSON);
            Bundle bundle = new Bundle();
            bundle.putString("username", creds.getString("username"));
            bundle.putString("password", creds.getString("password"));
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new P2PExceptionFailed("failed to decode JSON object");
        }
    }

    protected void checkNotNullData(String data, String errMessage) throws P2PExceptionFailed {
        if (data == null)
            throw new P2PExceptionFailed(errMessage);
    }

    protected void persistCreds(String creds) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREF_P2P_CREDS, creds);
        editor.commit();
    }

    protected String getPersistedCreds() throws P2PExceptionFailed {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(KEY_PREF_P2P_CREDS, buildJSONStringFromCreds("", ""));
    }

    @Override
    public void clearStoredCredentials() throws P2PExceptionFailed {
        storeCredentials("", "");
    }

    @Override
    public void storeCredentials(String username, String password) throws P2PExceptionFailed {
        checkNotNullData(username, "failed to store credentials: username is null");
        checkNotNullData(password, "failed to store credentials: password is null");
        String credsJSON = buildJSONStringFromCreds(username, password);
        persistCreds(credsJSON);
    }

    @Override
    public Bundle getStoredCredentials() throws P2PExceptionFailed {
        return buildCredsBundleFromJSONString(getPersistedCreds());
    }
}