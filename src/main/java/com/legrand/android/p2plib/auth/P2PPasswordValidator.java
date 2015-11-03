/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionBadFormat;

/**
 * P2PPasswordValidator is an interface to be implemented by password validators
 */
public interface P2PPasswordValidator {

    /**
     * Check password strength against rules and throw an exception if not strong enough
     * @param password to be checked
     * @throws P2PExceptionBadFormat
     */
    void checkPasswordStrength(String password) throws P2PExceptionBadFormat;

    /**
     * Return a text explaining the expected format for the password
     * @return
     */
    String getExpectedFormatExplaination();
}
