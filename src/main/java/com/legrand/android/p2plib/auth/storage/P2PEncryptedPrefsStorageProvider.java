/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

import se.simbio.encryption.Encryption;

/**
 * Storage with stores encrypted data into android preferences
 */

public class P2PEncryptedPrefsStorageProvider extends P2PPrefsStorage implements P2PStorageProvider {

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
        String encryptedUsername = mEncryption.encryptOrNull(username);
        String encryptedPassword = mEncryption.encryptOrNull(password);
        super.storeCredentials(encryptedUsername, encryptedPassword);
    }

    @Override
    public String getUsername() throws P2PExceptionFailed {
        String encrypted = super.getUsername();
        checkNotNullData(encrypted, "could not get username (null)");
        String clearUsername = mEncryption.decryptOrNull(encrypted);
        checkNotNullData(clearUsername, "could not get username (null, decryption failed)");
        return clearUsername;
    }

    @Override
    public String getPassword() throws P2PExceptionFailed {
        String encrypted = super.getPassword();
        checkNotNullData(encrypted, "could not get password (null)");
        String clearPassword = mEncryption.decryptOrNull(encrypted);
        checkNotNullData(clearPassword, "could not get password (null, decryption failed)");
        return clearPassword;
    }
}
