/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.core.exceptions;

/**
 * P2PExceptionFailed is raised when requested operation failed
 */
public class P2PExceptionFailed extends P2PException {

    public P2PExceptionFailed(String message) {
        super(message);
    }
}