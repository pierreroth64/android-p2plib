/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.util.Log;

import com.legrand.android.p2plib.constants.P2PGlobals;
import com.legrand.android.p2plib.constants.P2PMessageIDs;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionManager;
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

    private static final String TAG = P2PGlobals.P2P_TAG + " Conn. builder";

    /**
     * Create a connection with Legrand default settings
     * @return the configured connection
     */
    public AbstractXMPPConnection createDefaultConnection() {
        P2PConf conf = P2PConf.createDefaultConf();
        return createConnection(conf);
    }

    /**
     * Create a configured XMPP connection
     * @param conf is a P2PConf object
     * @return the created connection
     */
    public AbstractXMPPConnection createConnection(P2PConf conf) {
        //SmackConfiguration.DEBUG = true;
        AbstractXMPPConnection conn;

        // build connection
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setServiceName(conf.mDomainName);
        configBuilder.setHost(conf.mHostName);
        configBuilder.setPort(conf.mPort);
        configBuilder.setCustomSSLContext(createSSLContext(conf));
        configBuilder.setHostnameVerifier(new P2PHostnameVerifier());
        configBuilder.setResource("");
        conn = new XMPPTCPConnection(configBuilder.build());

        // set reconnection policy
        ReconnectionManager connMgr = ReconnectionManager.getInstanceFor(conn);
        if (conf.mReconnectionEnabled)
            connMgr.enableAutomaticReconnection();
        else
            connMgr.disableAutomaticReconnection();
        connMgr.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        connMgr.setFixedDelay(conf.mReconnDelaySeconds);

        // set reconnection default policy
        ReconnectionManager.setEnabledPerDefault(conf.mReconnectionEnabled);
        ReconnectionManager.setDefaultReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        ReconnectionManager.setDefaultFixedDelay(conf.mReconnDelaySeconds);

        // set ping interval
        PingManager pingManager = PingManager.getInstanceFor(conn);
        pingManager.setPingInterval(conf.mPingInterval);

        // set default ping interval
        PingManager.setDefaultPingInterval(conf.mPingInterval);

        Log.d(TAG, "created connection on " + conf.mHostName + ":" + conf.mPort + " (domain: " + conf.mDomainName + ")");

        return conn;
    }

    /**
     * create a SSLContext (to be used when self-signed certificates are used on the server)
     * @param conf is a P2PConf object
     * @return
     */
    private SSLContext createSSLContext(P2PConf conf) {

        SSLContext sc = null;
        try {
            sc = JavaPinning.forPin(conf.mSSLPin);
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
