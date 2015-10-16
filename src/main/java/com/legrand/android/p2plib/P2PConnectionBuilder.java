/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import eu.geekplace.javapinning.JavaPinning;


/**
 * P2PHostnameVerifier is a stub class always returning true when verifying hostname
 */
class P2PHostnameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}

/**
 * The P2PConnectionBuilder encapsulates the connection creation
 */
public class P2PConnectionBuilder {

    private static final String TAG = P2PConstants.P2P_TAG + " Conn. builder";
    private static final String SSL_PIN_STRING = "CERTPLAIN:30820296308201ffa003020102020900f5b01f59b6c77251300d06092a864886f70d01010b05003064310b30090603550406130246523113301106035504080c0a536f6d652d53746174653110300e060355040a0c074c656772616e64310d300b060355040b0c0453776174311f301d06035504030c167032707365727665722e636c6f75646170702e6e6574301e170d3135303732323039333431375a170d3235303731393039333431375a3064310b30090603550406130246523113301106035504080c0a536f6d652d53746174653110300e060355040a0c074c656772616e64310d300b060355040b0c0453776174311f301d06035504030c167032707365727665722e636c6f75646170702e6e657430819f300d06092a864886f70d010101050003818d0030818902818100a2927f8850964c772ff72255cc9906916db687a168998e31f696f5b44a46730044f043aeee4f3dfe42f54e56c9c5c6a971092558bdebcf9df73e74bab1d3c2ad9d7eb6da369b98992d65487e8ec6a3f080154f81cc1d6e3f6a4aab2d290994ff47be6b0b7295ee8a224f88bc8565a38e8554ebe4984e88d19bec92eceb02e85b0203010001a350304e301d0603551d0e04160414f6744f39b4fe801d13e0285573c0282942a7e25f301f0603551d23041830168014f6744f39b4fe801d13e0285573c0282942a7e25f300c0603551d13040530030101ff300d06092a864886f70d01010b050003818100870faa7c96688d4808ffd3d30e91c9ac85f3ceb0c799070daf207803053f57c963d2342291d09fcad9a2834fd9bf3c5c8f3f24a24c7b07181287fdb34d44d412b287905bc50214aa86b76ccd920a6dc7f2cc14202ff9611d39f15e2ab6a7b4121821358cc6cea3a2538de5cf15df3f043c9e9c354d5778c3afb4bfb804ce62fd";

    /**
     * Create a configured XMPP connection
     * @return the created connection
     */
    public AbstractXMPPConnection createConnection() {
        //SmackConfiguration.DEBUG = true;
        AbstractXMPPConnection conn;

        // build connection
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setServiceName(P2PConstants.P2P_CONF_DOMAIN);
        configBuilder.setHost(P2PConstants.P2P_CONF_SERVER);
        configBuilder.setPort(P2PConstants.P2P_CONF_PORT);
        configBuilder.setCustomSSLContext(createSSLContext());
        configBuilder.setHostnameVerifier(new P2PHostnameVerifier());
        configBuilder.setResource("");
        conn = new XMPPTCPConnection(configBuilder.build());

        // set reconnection policy
        ReconnectionManager connMgr = ReconnectionManager.getInstanceFor(conn);
        if (P2PConstants.P2P_RECONNECTION_ENABLED)
            connMgr.enableAutomaticReconnection();
        else
            connMgr.disableAutomaticReconnection();
        connMgr.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        connMgr.setFixedDelay(P2PConstants.P2P_RECONNECTION_DELAY_S);

        // set reconnection default policy
        ReconnectionManager.setEnabledPerDefault(P2PConstants.P2P_RECONNECTION_ENABLED);
        ReconnectionManager.setDefaultReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        ReconnectionManager.setDefaultFixedDelay(P2PConstants.P2P_RECONNECTION_DELAY_S);

        Log.d(TAG, "automatic reconnection enabled: " + connMgr.isAutomaticReconnectEnabled());

        // set ping interval
        PingManager pingManager = PingManager.getInstanceFor(conn);
        pingManager.setPingInterval(P2PConstants.P2P_PING_INTERVAL_S);

        // set default ping interval
        PingManager.setDefaultPingInterval(P2PConstants.P2P_PING_INTERVAL_S);

        //

        return conn;
    }

    /**
     * create a SSLContext (to be used when self-signed certificates are used on the server)
     * @return
     */
    private SSLContext createSSLContext() {

        SSLContext sc = null;
        try {
            sc = JavaPinning.forPin(SSL_PIN_STRING);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Log.d(TAG, "error when creating SSLContect");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d(TAG, "error when creating SSLContect");
        }
        return sc;
    }
}
