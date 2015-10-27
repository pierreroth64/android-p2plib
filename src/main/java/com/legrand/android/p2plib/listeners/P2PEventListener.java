/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

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
     * Handler called when account creation failed
     */
    void onAccountCreationFailure(String username, String password);

    /**
     * Handler called when authenticated within the P2P server
     */
    void onAuthenticated(String username);

    /**
     * Handler called when authentication failed with the P2P server
     */
    void onAuthenticationFailure(String username, String password);

    /**
     * Handler called when data has been sent to the P2P server
     */
    void onDataSent(String message);

}
