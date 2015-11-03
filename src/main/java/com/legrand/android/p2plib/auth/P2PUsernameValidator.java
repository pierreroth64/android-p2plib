/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */
package com.legrand.android.p2plib.auth;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionBadFormat;

/**
 * P2PUsernameValidator is an interface to be implemented by username validators
 */
public interface P2PUsernameValidator {

    /**
     * Check username format against rules and throw an exception on incorrect format
     * @param username to be checked
     * @throws P2PExceptionBadFormat
     */
    void checkUsernameFormat(String username) throws P2PExceptionBadFormat;

    /**
     * Return a text explaining the expected format for the username
     * @return
     */
    String getExpectedFormatExplaination();

}
