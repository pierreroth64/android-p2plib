/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth;

import android.content.Context;

import com.legrand.android.p2plib.auth.storage.P2PBaseStorage;
import com.legrand.android.p2plib.auth.storage.P2PStorageProvider;
import com.legrand.android.p2plib.auth.validators.P2PPasswordValidator;
import com.legrand.android.p2plib.auth.validators.P2PStandardUsernameValidator;
import com.legrand.android.p2plib.auth.validators.P2PStrongPasswordValidator;
import com.legrand.android.p2plib.auth.validators.P2PUsernameValidator;
import com.legrand.android.p2plib.core.exceptions.P2PExceptionBadFormat;
import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

/**
 * P2PCredentialsManager in charge of passwords, obviously ;)
 */
public class P2PCredentialsManager {

    private P2PPasswordValidator mPasswordValidator;
    private P2PUsernameValidator mUsernameValidator = null;
    private P2PStorageProvider mCredsStorage;

    public P2PCredentialsManager(Context context) {
        setPasswordValidator(getDefaultPasswordValidator());
        setUsernameValidator(getDefaultUsernameValidator());
        mCredsStorage = P2PBaseStorage.getDefaultStorage(context);
    }

    /**
     * Get the default password validator instance
     * @return a P2PPasswordValidator
     */
    public static P2PPasswordValidator getDefaultPasswordValidator() {
        return new P2PStrongPasswordValidator();
    }

    /**
     * Get the default password validator instance
     * @return a P2PPasswordValidator
     */
    public static P2PUsernameValidator getDefaultUsernameValidator() {
        return new P2PStandardUsernameValidator();
    }

    /**
     * Set current password validator
     * @param validator
     */
    public void setPasswordValidator(P2PPasswordValidator validator) {
        mPasswordValidator = validator;
    }

    /**
     * Set current username validator
     * @param validator
     */
    public void setUsernameValidator(P2PUsernameValidator validator) {
        mUsernameValidator = validator;
    }

    /**
     * Check Credentials format
     * @param username to be checked
     * @param password to be cheched
     * @throws P2PExceptionBadFormat
     */
    public void checkCredentialsFormat(String username, String password) throws P2PExceptionBadFormat {
        mUsernameValidator.checkUsernameFormat(username);
        mPasswordValidator.checkPasswordStrength(password);
    }

    public void storeCredentials(String username, String password) throws P2PExceptionFailed {
        mCredsStorage.storeCredentials(username, password);
    }

    public void clearStoredCredentials() throws P2PExceptionFailed {
        mCredsStorage.clearCredentials();
    }

    public String getStoredUsername() throws P2PExceptionFailed {
        return mCredsStorage.getUsername();
    }

    public String getStoredPassword() throws P2PExceptionFailed {
        return mCredsStorage.getPassword();
    }

    public Boolean hasStoredCredentials() {
        try {
            if (!getStoredUsername().isEmpty() && !getStoredPassword().isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (P2PExceptionFailed e) {
            e.printStackTrace();
            return false;
        }
    }

}
