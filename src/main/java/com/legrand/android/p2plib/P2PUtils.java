/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.os.Bundle;

import org.jivesoftware.smack.packet.Presence;

/**
 * Utility class for P2P related stuff
 */
public class P2PUtils {

    /**
     * Extract username from resource
     * @param res is the given resource string
     * @return the who (username)
     */
    public static String extractWhoFromResource(String res) {
        return res.split("@")[0];
    }

    /**
     * Extract JID from resource
     * @param res is the given resource string
     * @return the JID
     */
    public static String extractJIDFromResource(String res) {
        return res.split("/")[0];
    }

    /**
     * Create a presence bundle from a smack library one)
     * @param presence is the smack library object
     * @return a bundle containing the following keys:
     *          "from": contains the who (username)
     *          "jid": contains the JID
     *          "is_available": is a boolean
     */
    public static Bundle createPresenceBundle(Presence presence) {
        Bundle bundle =  new Bundle();
        bundle.putString("from", extractWhoFromResource(presence.getFrom()));
        bundle.putString("jid", extractJIDFromResource(presence.getFrom()));
        bundle.putBoolean("is_available", presence.isAvailable());
        return bundle;
    }
}
