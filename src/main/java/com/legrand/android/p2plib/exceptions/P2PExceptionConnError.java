/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.exceptions;

/**
 * P2PExceptionConnError is raised when there's something wrong with a connection
 */
public class P2PExceptionConnError extends P2PException {

    public P2PExceptionConnError(String message) {
        super("Connection error: " + message);
    }
}
