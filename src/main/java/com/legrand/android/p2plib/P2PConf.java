/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * The P2PConf is a container class embedding P2P configuration
 */
public class P2PConf {
    public String mHostName = "";
    public int mPort = 0;
    public String mDomainName = "";
    public String mSSLPin = "";
    public int mReconnDelaySeconds = 10;
    public Boolean mReconnectionEnabled = false;
    public int mPingInterval = 60;
    public List<String> mSupportedParams = new ArrayList<>();

    public P2PConf() {
        mSupportedParams.add("hostName");
        mSupportedParams.add("port");
        mSupportedParams.add("domainName");
        mSupportedParams.add("SSLPin");
        mSupportedParams.add("reconnectionDelay");
        mSupportedParams.add("reconnectionEnabled");
        mSupportedParams.add("pingInterval");
    }

    /**
     * Create a default configuration
     * @return the created default configuration object
     */
    public static P2PConf createDefaultConf() {
        P2PConf conf = new P2PConf();
        conf.mHostName = "p2pserver.cloudapp.net";
        conf.mPort = 80;
        conf.mDomainName = "iot.legrand.net";
        conf.mSSLPin = "CERTPLAIN:30820296308201ffa003020102020900f5b01f59b6c77251300d06092a864886f70d01010b05003064310b30090603550406130246523113301106035504080c0a536f6d652d53746174653110300e060355040a0c074c656772616e64310d300b060355040b0c0453776174311f301d06035504030c167032707365727665722e636c6f75646170702e6e6574301e170d3135303732323039333431375a170d3235303731393039333431375a3064310b30090603550406130246523113301106035504080c0a536f6d652d53746174653110300e060355040a0c074c656772616e64310d300b060355040b0c0453776174311f301d06035504030c167032707365727665722e636c6f75646170702e6e657430819f300d06092a864886f70d010101050003818d0030818902818100a2927f8850964c772ff72255cc9906916db687a168998e31f696f5b44a46730044f043aeee4f3dfe42f54e56c9c5c6a971092558bdebcf9df73e74bab1d3c2ad9d7eb6da369b98992d65487e8ec6a3f080154f81cc1d6e3f6a4aab2d290994ff47be6b0b7295ee8a224f88bc8565a38e8554ebe4984e88d19bec92eceb02e85b0203010001a350304e301d0603551d0e04160414f6744f39b4fe801d13e0285573c0282942a7e25f301f0603551d23041830168014f6744f39b4fe801d13e0285573c0282942a7e25f300c0603551d13040530030101ff300d06092a864886f70d01010b050003818100870faa7c96688d4808ffd3d30e91c9ac85f3ceb0c799070daf207803053f57c963d2342291d09fcad9a2834fd9bf3c5c8f3f24a24c7b07181287fdb34d44d412b287905bc50214aa86b76ccd920a6dc7f2cc14202ff9611d39f15e2ab6a7b4121821358cc6cea3a2538de5cf15df3f043c9e9c354d5778c3afb4bfb804ce62fd";
        conf.mReconnDelaySeconds = 5;
        conf.mReconnectionEnabled = true;
        conf.mPingInterval = 120;
        return conf;
    }

    /**
     * Build a Bundle from a gievn P2PConf object
     * @param conf the gievn P2PConf object
     * @return the built Bundle
     */
    public static Bundle createBundleFromConf(P2PConf conf) {
        Bundle bundle = new Bundle();
        bundle.putString("hostName", conf.mHostName);
        bundle.putInt("port", conf.mPort);
        bundle.putString("domainName", conf.mDomainName);
        bundle.putBoolean("reconnectionEnabled", conf.mReconnectionEnabled);
        bundle.putInt("reconnectionDelay", conf.mReconnDelaySeconds);
        bundle.putInt("pingInterval", conf.mPingInterval);
        return bundle;
    }

    /**
     * Check for minimal configuration needed by the service
     * @param mConf is the P2PConf to check
     * @return true if minimal conf it ok in this P2PConf object
     */
    public static boolean hasMinimalConf(P2PConf mConf) {
        return ((mConf.mHostName != "") && (mConf.mPort != 0) && (mConf.mDomainName != ""));
    }
}