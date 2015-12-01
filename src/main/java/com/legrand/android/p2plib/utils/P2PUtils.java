/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.utils;

import android.os.Bundle;
import android.util.Log;

import com.legrand.android.p2plib.constants.P2PSubscriptionType;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.packet.RosterPacket;

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
     * Return availability from XMPP presence type field
     * @param type is the XMPP presence type field
     * @return true if available
     */
    private static Boolean isAvailableFromType(Presence.Type type) {
        Log.d("YAHA !", "presence type: " + type.toString());
        if ((type == Presence.Type.available) || (type == Presence.Type.subscribed) || (type == Presence.Type.subscribe))
            return true;
        else
            return false;
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
        bundle.putBoolean("is_available", isAvailableFromType(presence.getType()));
        return bundle;
    }

    public static P2PSubscriptionType encodeSubscriptionType(RosterPacket.ItemType type) {
        if (type == RosterPacket.ItemType.both) {
            return P2PSubscriptionType.BOTH;
        } else if (type == RosterPacket.ItemType.to) {
            return P2PSubscriptionType.TO;
        } else if (type == RosterPacket.ItemType.from) {
            return P2PSubscriptionType.FROM;
        } else if (type == RosterPacket.ItemType.none) {
            return P2PSubscriptionType.NONE;
        } else if (type == RosterPacket.ItemType.remove) {
            return P2PSubscriptionType.REMOVE;
        } else {
            return P2PSubscriptionType.UNSET;
        }
    }
}
