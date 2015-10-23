/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.listeners;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

/**
 * The P2PRosterListener interface has to be implemented to be able to listen fo P2P roster changes
 */

public class P2PRosterListener implements RosterListener {

    /**
     * Handler called when entries are added to the roster
     * @param addresses
     */
    @Override
    public void entriesAdded(Collection<String> addresses) {

    }

    /**
     * Handler called when entries are updated in the roster
     * @param addresses
     */
    @Override
    public void entriesUpdated(Collection<String> addresses) {

    }

    /**
     * Handler called when entries are deleted from the roster
     * @param addresses
     */
    @Override
    public void entriesDeleted(Collection<String> addresses) {

    }

    /**
     * Handler called when presence changed
     * @param presence
     */
    @Override
    public void presenceChanged(Presence presence) {

    }
}
