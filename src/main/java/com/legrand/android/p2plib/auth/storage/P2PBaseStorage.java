/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth.storage;

import android.content.Context;

/**
 * Base class for storage
 */
public class P2PBaseStorage {

    final static protected String KEY_PREF_P2P_CREDS = "p2pcr";

    static public P2PStorageProvider getDefaultStorage(Context context) {
        return new P2PJSONEncryptedPrefsStorageProvider(context);
    }
}
