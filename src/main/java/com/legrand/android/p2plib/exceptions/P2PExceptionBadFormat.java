/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.exceptions;

/**
 * P2PExceptionBadFormat is raised when a bad format is found in given args or data
 */
public class P2PExceptionBadFormat extends P2PException {

    public P2PExceptionBadFormat(String message) {
        super("Bad format: " + message);
    }
}
