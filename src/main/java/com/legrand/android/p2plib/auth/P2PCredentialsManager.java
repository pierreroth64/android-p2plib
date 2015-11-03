/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionBadFormat;

/**
 * P2PCredentialsManager in charge of passwords, obviously ;)
 */
public class P2PCredentialsManager {

    private P2PPasswordValidator mPasswordValidator;
    private P2PUsernameValidator mUsernameValidator = null;

    public P2PCredentialsManager() {
        setPasswordValidator(getDefaultPasswordValidator());
        setUsernameValidator(getDefaultUsernameValidator());
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
}
