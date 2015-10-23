/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

import android.os.Bundle;

/**
 * The P2PServiceListener interface has to be implemented to be able to listen fo P2P Service
 */

public interface P2PServiceListener {

    /**
     * Handler called when credentials have been received by the P2P service
     * @param username the P2P current username
     * @param password the P2P current password
     */
    void onReceivedCreds(String username, String password);

    /**
     * Handler called when credentials have changed
     * @param username the P2P current username
     * @param password the P2P current password
     */
    void onCredsChanged(String username, String password);

    /**
     * Handler called when registering to the P2P service is done
     */
    void onServiceRegisterDone();

    /**
     * Handler called when P2PService conf changed
     * @param conf
     */
    void onConfChanged(Bundle conf);

}
