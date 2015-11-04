/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionFailed;

/**
 * P2PStorageProvider has to be implemented to provide username/password persistence
 */
public interface P2PStorageProvider {

    void clearCredentials() throws P2PExceptionFailed;
    void storeCredentials(String username, String password) throws P2PExceptionFailed;
    String getUsername() throws P2PExceptionFailed;
    String getPassword() throws P2PExceptionFailed;
}
