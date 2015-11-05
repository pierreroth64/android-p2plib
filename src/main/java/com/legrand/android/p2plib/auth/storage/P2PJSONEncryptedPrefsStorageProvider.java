/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;
import android.os.Bundle;

import com.legrand.android.p2plib.constants.P2PGlobals;
import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

import se.simbio.encryption.Encryption;

/**
 * Storage with stores encrypted JSON creds into android preferences
 * Important Note: empty username and password are not encrypted
 */

public class P2PJSONEncryptedPrefsStorageProvider extends P2PJSONPrefsStorage implements P2PStorageProvider {

    public static final String TAG = P2PGlobals.P2P_TAG + ".Storage";
    private String mKey = "løeЯg€Rµa%n!dþZµµ";
    private String mSalt = "µµ=^'!*mM&ic";
    private byte[] mIV = {79, 71, 80, 61, 52, -127, 20, 31, -42, 99, 4, 59, 98, -8, -92, -67};
    Encryption mEncryption = null;

    public P2PJSONEncryptedPrefsStorageProvider(Context context) {
        super(context);
    }

    /**
     * Get Encryption instance
     * Note: lazy load the instance enables not to overload the CPU generating the object on constructor call
     * @return the Encryption instance
     */
    private Encryption getEncryptionInstance() {
        if (mEncryption == null) {
            mEncryption = Encryption.getDefault(mKey, mSalt, mIV);;
        }
        return mEncryption;
    }

    @Override
    public void storeCredentials(String username, String password) throws P2PExceptionFailed {

        String credsJSON = buildJSONStringFromCreds(username, password);
        if ((!username.isEmpty()) && (!password.isEmpty())) {
            // Only encrypt if not empty
            getEncryptionInstance().encryptAsync(credsJSON, new Encryption.Callback() {
                @Override
                public void onSuccess(String encryptedCreds) {
                    persistCreds(encryptedCreds);
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } else {
            persistCreds(credsJSON);
        }
    }

    @Override
    public Bundle getStoredCredentials() throws P2PExceptionFailed {
        String creds = getPersistedCreds();
        try {
            // try to get creds as JSON format.
            // if fails, may be encrypted
            return buildCredsBundleFromJSONString(creds);
        } catch (P2PExceptionFailed e) {
            String decryptedCredsJSON = getEncryptionInstance().decryptOrNull(creds);
            return buildCredsBundleFromJSONString(decryptedCredsJSON);
        }
    }
}
