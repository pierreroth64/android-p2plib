/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

/**
 * P2PStorageProvider has to be implemented to provide username/password persistence
 */
public interface P2PStorageProvider {

    void clearCredentials();
    void storeCredentials(String username, String password);
    String getUsername();
    String getPassword();
}
