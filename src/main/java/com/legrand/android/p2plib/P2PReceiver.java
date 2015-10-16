/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.os.Bundle;

/**
 * The P2PReceiver interface has to be implemented to be able to receive P2P messages
 */
public interface P2PReceiver {

    /**
     * Handler called when data is received on the chat channel
     * @param bundle contains the following fields:
     *               String 'message': the data content
     *               String 'from': the
     */
    void onReceiveData(Bundle bundle);

    /**
     * Handler called when presence of one of the roster items changes
     * @param bundle contains the following fields:
     *               Boolean: 'is_available'
     *               String: 'from' which is the item name (everything before @ of the jabber ID)
     */
    void onReceivePresence(Bundle bundle);
}
