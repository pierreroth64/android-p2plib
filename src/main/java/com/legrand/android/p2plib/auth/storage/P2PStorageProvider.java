/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.os.Bundle;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

/**
 * P2PStorageProvider has to be implemented to provide username/password persistence
 */
public interface P2PStorageProvider {

    /**
     * Clear stored credentials
     * @throws P2PExceptionFailed
     */
    void clearStoredCredentials() throws P2PExceptionFailed;

    /**
     * Store credentials
     * @param username is the P2P username
     * @param password is the P2P password
     * @throws P2PExceptionFailed
     */
    void storeCredentials(String username, String password) throws P2PExceptionFailed;

    /**
     * Get stored credentials
     * @return a Bundle with the folowwing keys: 'username' and 'password'
     * @throws P2PExceptionFailed
     */
    Bundle getStoredCredentials() throws P2PExceptionFailed;
}
