/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.utils;

import android.os.Bundle;

/**
 * This P2PThread implements a Runnable and stores a bundle
 * that can be retrieved fromp the run() method (trough a this.mBundle)
 */
public class P2PThread implements Runnable {

    public Bundle mBundle = null;

    /**
     * Constructor expecting a bundle
     * @param bundle to be stored internally
     */
    public P2PThread(Bundle bundle) {
        mBundle = bundle;
    }

    /**
     * Run the P2P thread
     */
    public void run() {

    }
}
