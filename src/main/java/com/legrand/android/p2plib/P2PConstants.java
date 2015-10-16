/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

/**
 * P2PConstants holds all the constants of the PéP library
 */
public class P2PConstants {

    public static final String P2P_TAG = "P2PLib";

    public static final int MSG_SRVC_P2P_CONNECT = 1;
    public static final int MSG_SRVC_P2P_DISCONNECT = 2;
    public static final int MSG_SRVC_P2P_RECONNECT = 3;
    public static final int MSG_SRVC_P2P_SET_CREDS = 4;
    public static final int MSG_SRVC_P2P_LOGIN = 5;
    public static final int MSG_SRVC_REGISTER = 6;
    public static final int MSG_SRVC_REGISTER_ACK = 7;
    public static final int MSG_SRVC_P2P_CREATE_ACCOUNT = 8;
    public static final int MSG_SRVC_P2P_DATA = 9;
    public static final int MSG_CLIENT_P2P_DATA = 10;
    public static final int MSG_CLIENT_P2P_PRESENCE = 11;
    public static final int MSG_SRVC_P2P_SUBSCRIBE = 12;
    public static final int MSG_CLIENT_P2P_EVENT_CONNECTED = 13;
    public static final int MSG_CLIENT_P2P_EVENT_ACCOUNT_CREATED = 14;
    public static final int MSG_CLIENT_P2P_EVENT_AUTHENTICATED = 15;
    public static final int MSG_CLIENT_P2P_EVENT_DATA_SENT = 16;
    public static final int MSG_CLIENT_P2P_EVENT_DISCONNECTED = 17;
    public static final int MSG_CLIENT_P2P_EVENT_ACK_CREDS = 18;
    public static final int MSG_CLIENT_P2P_EVENT_CREDS_CHANGED = 19;
    public static final int MSG_SRVC_P2P_REFRESH_CONN_STATUS = 20;

    // server configuration
    public static final String P2P_CONF_SERVER = "p2pserver.cloudapp.net";
    public static final String P2P_CONF_DOMAIN = "iot.legrand.net";
    public static final int P2P_CONF_PORT = 80;

    // connection configuration
    public static final Boolean P2P_RECONNECTION_ENABLED = true;
    public static final int P2P_RECONNECTION_DELAY_S = 5;
    public static final int P2P_PING_INTERVAL_S = 120;
}
