/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.core;

/**
 * P2PReason embeds data explaining
 */
public class P2PReason {
    private P2PErrorCode mErrorCode;
    private String mReason;

    public P2PReason(P2PErrorCode errorCode, String reason) {
        mErrorCode = errorCode;
        mReason = reason;
    }

    public P2PErrorCode getErrorCode() {
        return mErrorCode;
    }

    public String getReason() {
        return mReason;
    }
}
