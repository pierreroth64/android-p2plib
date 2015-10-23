/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

/**
 * The P2PServiceErrorListener interface has to be implemented to be able to listen fo P2P Service errors
 */

public interface P2PServiceErrorListener {

    /**
     * Handler called on P2P Service error
     * @param message is the error message
     * @param detailedMessage is an optional detailed message
     */
    void onError(String message, String detailedMessage);

    /**
     * Handler called on P2P Service warning
     * @param message is the warning message
     * @param detailedMessage is an optional detailed message
     */
    void onWarning(String message, String detailedMessage);
}
