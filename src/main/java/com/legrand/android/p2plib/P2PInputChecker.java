/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.os.Bundle;

import com.legrand.android.p2plib.exceptions.P2PExceptionBadFormat;

/**
 * The P2PInputChecker checks for the format of P2P items
 */
public class P2PInputChecker {

    /**
     * Check that the passed bundle has the expected keys
     * @param bundle to be checked
     * @throws P2PExceptionBadFormat
     */
    public void checkBundle(Bundle bundle) throws  P2PExceptionBadFormat{

        if (bundle.getString("message").equals(""))
            throw new P2PExceptionBadFormat("passed bundle should have a non-empty 'message' entry");
    }

    /**
     * Check that the passed JID has the correct format
     * @param jid is the JID to be checked
     */
    public void checkJID(String jid) {
        // TODO
    }
}
