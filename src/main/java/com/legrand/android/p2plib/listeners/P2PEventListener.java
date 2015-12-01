/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

import com.legrand.android.p2plib.constants.P2PSubscriptionType;
import com.legrand.android.p2plib.core.P2PReason;

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
     * @param reason is an object transporting information about the reason of the disconnection
     */
    void onDisconnected(P2PReason reason);

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

    /**
     * Handler called when subscription changes for a given address
     * @param address is the JID
     * @type is the subscription type (to, from, both, etc...)
     */
    void onSubscription(String address, P2PSubscriptionType type);
}
