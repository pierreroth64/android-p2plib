/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.constants;

/**
 * P2PMessageIDs holds all the message IDs exchanged between P2PMessenger and P2PService
 */
public class P2PMessageIDs {

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
    public static final int MSG_SRVC_P2P_SET_SERVER_CONF = 21;
    public static final int MSG_SRVC_P2P_GET_SERVER_CONF = 22;
    public static final int MSG_CLIENT_P2P_CONF = 23;
    public static final int MSG_SRVC_P2P_ERROR = 24;
    public static final int MSG_CLIENT_P2P_EVENT_AUTHENTICATION_FAILED = 25;
    public static final int MSG_CLIENT_P2P_EVENT_ACCOUNT_CREATION_FAILED = 26;
    public static final int MSG_SRVC_UNREGISTER = 27;
}
