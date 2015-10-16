/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

/**
 * The P2PEventListener interface has to be implemented to be able to listen fo P2P events
 */

public interface P2PEventListener {

    /**
     * Handler called when connected to the P2P server
     */
    void onConnected();

    /**
     * Handler called when disconnected from the P2P server
     */
    void onDisconnected();

    /**
     * Handler called when account is created
     */
    void onAccountCreated(String username, String password);

    /**
     * Handler called when authenticated within the P2P server
     */
    void onAuthenticated(String username);

    /**
     * Handler called when data has been sent to the P2P server
     */
    void onDataSent(String message);

    /**
     * Handler called when credentials have been received by the P2P service
     */
    void onReceivedCreds(String username, String password);

    /**
     * Handler called when credentials have changed
     */
    void onCredsChanged(String username, String password);

    /**
     * Handler called when registering to the PéP service is done
     */
    void onServiceRegisterDone();
}
