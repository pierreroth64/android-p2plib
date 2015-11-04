/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;
import android.util.Log;

import com.legrand.android.p2plib.constants.P2PGlobals;
import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

import se.simbio.encryption.Encryption;

/**
 * Storage with stores encrypted data into android preferences
 * Important Note: empty username and password are not encrypted
 */

public class P2PEncryptedPrefsStorageProvider extends P2PPrefsStorage implements P2PStorageProvider {

    public static final String TAG = P2PGlobals.P2P_TAG + ".Storage";
    private String mKey = "løeЯg€Rµa%n!dþZµµ";
    private String mSalt = "µµ=^'!*mM&ic";
    private byte[] mIV = {79, 71, 80, 61, 52, -127, 20, 31, -42, 99, 4, 59, 98, -8, -92, -67};
    Encryption mEncryption;

    public P2PEncryptedPrefsStorageProvider(Context context) {
        super(context);
        mEncryption = Encryption.getDefault(mKey, mSalt, mIV);
    }

    @Override
    public void storeCredentials(String username, String password) throws P2PExceptionFailed {

        if (!username.isEmpty()) {
            mEncryption.encryptAsync(username, new Encryption.Callback() {
                @Override
                public void onSuccess(String encryptedUsername) {
                    try {
                        storeUsername(encryptedUsername);
                    } catch (P2PExceptionFailed e) {
                        Log.e(TAG, "could not store username (" + e.getMessage() + ")");
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } else {
            storeUsername(username);
        }

        if (!password.isEmpty()) {
            mEncryption.encryptAsync(password, new Encryption.Callback() {
                @Override
                public void onSuccess(String encryptedPassword) {
                    try {
                        storePassword(encryptedPassword);
                    } catch (P2PExceptionFailed e) {
                        Log.e(TAG, "could not store password (" + e.getMessage() + ")");
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } else {
            storePassword(password);
        }
    }

    @Override
    public String getUsername() throws P2PExceptionFailed {
        String rawUsername = super.getUsername();
        checkNotNullData(rawUsername, "could not get username (null)");
        if (rawUsername.isEmpty())
            return rawUsername;
        else {
            String clearUsername = mEncryption.decryptOrNull(rawUsername);
            checkNotNullData(clearUsername, "could not get username (null, decryption failed)");
            return clearUsername;
        }
    }

    @Override
    public String getPassword() throws P2PExceptionFailed {
        String rawPassword = super.getPassword();
        checkNotNullData(rawPassword, "could not get password (null)");
        if (rawPassword.isEmpty())
            return rawPassword;
        else {
            String clearPassword = mEncryption.decryptOrNull(rawPassword);
            checkNotNullData(clearPassword, "could not get password (null, decryption failed)");
            return clearPassword;
        }
    }
}
