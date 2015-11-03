/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

import android.os.Bundle;

import com.legrand.android.p2plib.core.P2PReason;

/**
 * The P2PServiceListener interface has to be implemented to be able to listen fo P2P Service
 */

public interface P2PServiceListener {

    /**
     * Handler called when credentials have been successfully received by the P2P service
     * @param username the P2P new username
     * @param password the P2P new password
     */
    void onCredsChangeSuccess(String username, String password);

    /**
     * Handler called when credentials change failed
     * @param username the P2P username that failed to be set
     * @param password the P2P password that failed to be set
     * @param reason the reason why the change failed
     */
    void onCredsChangeFailure(String username, String password, P2PReason reason);

    /**
     * Handler called as a response when creds where requested
     * @param username is the current P2P username
     * @param password is the current P2P password
     */
    void onCurrentCredsReceived(String username, String password);

    /**
     * Handler called when registering to the P2P service is done
     */
    void onServiceRegisterDone();

    /**
     * Handler called when P2PService conf changed
     * @param conf
     */
    void onReceivedServiceConf(Bundle conf);

}
