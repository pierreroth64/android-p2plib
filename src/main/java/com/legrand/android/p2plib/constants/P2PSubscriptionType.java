/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */
package com.legrand.android.p2plib.constants;

/**
 * PO2P subscriptions types
 */
public enum P2PSubscriptionType {

    UNSET("unset"),
    BOTH("both"),
    TO("to"),
    FROM("from"),
    REMOVE("remove"),
    NONE("none");

    private String mName = "";

    P2PSubscriptionType(String name){
        mName = name;
    }

    public String toString(){
        return mName;
    }

    static public P2PSubscriptionType getTypeFromString(String subscriptionType) {
        for (P2PSubscriptionType type: P2PSubscriptionType.values()) {
            if (subscriptionType.equals(type.toString()))
                return type;
        }
        throw new RuntimeException(subscriptionType + " is unknown");
    }
}
