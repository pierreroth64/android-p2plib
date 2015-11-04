/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.core;

/**
 * P2PErrorCode is an enum of all available P2P error codes
 */
public enum P2PErrorCode {

    OK("OK"),
    FAILED("FAILED"),
    BAD_FORMAT("BAD FORMAT"),
    INVALID("INVALID");

    private String mName = "";

    P2PErrorCode(String name){
        mName = name;
    }

    public String toString(){
        return mName;
    }

    static public P2PErrorCode getErrorCodeFromString(String error) {
        for (P2PErrorCode code: P2PErrorCode.values()) {
            if (error.equals(code.toString()))
                return code;
        }
        throw new RuntimeException(error + " is unknown");
    }
}
