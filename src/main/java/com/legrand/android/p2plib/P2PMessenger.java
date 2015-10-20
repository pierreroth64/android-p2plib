/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.legrand.android.p2plib.exceptions.P2PExceptionBadFormat;

/**
 * The P2PMessenger is an abstraction class that allow client code to communicate
 * with the P2P server
 */
public class P2PMessenger {
    private String TAG;
    private String mName;
    private Context mContext = null;
    private Messenger mP2PSrvcMessenger = null;
    private Messenger mOwnMessenger;
    private HandlerThread mHandlerThread;
    private List<P2PReceiver> mReceivers = new ArrayList<>();
    private List<P2PEventListener> mEventListeners = new ArrayList<>();
    private List<P2PServiceListener> mServiceListeners = new ArrayList<>();
    private boolean mBound;
    private P2PInputChecker mP2PInputChecker = new P2PInputChecker();

    /**
     * Constructor of the P2PMessenger
     * @param name of the created messenger
     */
    public P2PMessenger(String name) {
        TAG = P2PConstants.P2P_TAG + ".Messenger." + name;
        mName = name;
        mOwnMessenger = createClientMessenger();
    }

    /**
     * Create an internal android messenger to cmmunicate with
     * @return the create android messenger
     */
    private Messenger createClientMessenger() {
        mHandlerThread = new HandlerThread("IPChandlerThread");
        mHandlerThread.start();
        return new Messenger(new IncomingHandler(mHandlerThread));
    }

    /**
     * This class handles all the incoming messages from the internal android messenger
     */
    private class IncomingHandler extends Handler {

        public IncomingHandler(HandlerThread thr) {
            super(thr.getLooper());
        }

        /**
         * Handler called when an android message is received by the internal messenger
         * @param msg received, to be parsed
         */
        @Override
        public void handleMessage(Message msg) {
            Message message = Message.obtain(msg);
            switch (message.what) {
                case P2PConstants.MSG_SRVC_REGISTER_ACK:
                    new Thread(new P2PThread(null) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PServiceListener listener: mServiceListeners)
                                listener.onServiceRegisterDone();
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_DATA:
                    Log.d(TAG, "received data from " + message.getData().getString("jid"));
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PReceiver receiver: mReceivers)
                                receiver.onReceiveData(mBundle);
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_PRESENCE:
                    Log.d(TAG, "received presence from " + message.getData().getString("jid"));
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PReceiver receiver: mReceivers)
                                receiver.onReceivePresence(mBundle);
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_EVENT_CONNECTED:
                    new Thread(new P2PThread(null) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PEventListener listener: mEventListeners)
                                listener.onConnected();
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_EVENT_DISCONNECTED:
                    new Thread(new P2PThread(null) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PEventListener listener: mEventListeners)
                                listener.onDisconnected();
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_EVENT_ACCOUNT_CREATED:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PEventListener listener: mEventListeners)
                                listener.onAccountCreated(mBundle.getString("username"),
                                        mBundle.getString("password"));
                        }
                    }).start();
                    break;
                case P2PConstants.MSG_CLIENT_P2P_EVENT_AUTHENTICATED:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PEventListener listener: mEventListeners)
                                listener.onAuthenticated(mBundle.getString("username"));
                        }
                    }).start();
                    break;

                case P2PConstants.MSG_CLIENT_P2P_EVENT_DATA_SENT:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PEventListener listener: mEventListeners)
                                listener.onDataSent(mBundle.getString("message"));
                        }
                    }).start();
                    break;

                case P2PConstants.MSG_CLIENT_P2P_CONF:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PServiceListener listener: mServiceListeners)
                                listener.onConfChanged(mBundle);
                        }
                    }).start();
                    break;

                case P2PConstants.MSG_CLIENT_P2P_EVENT_ACK_CREDS:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PServiceListener listener: mServiceListeners)
                                listener.onReceivedCreds(mBundle.getString("username"),
                                        mBundle.getString("password"));
                        }
                    }).start();
                    break;

                case P2PConstants.MSG_CLIENT_P2P_EVENT_CREDS_CHANGED:
                    new Thread(new P2PThread(new Bundle(message.getData())) {
                        @Override
                        public void run() {
                            Looper.prepare();
                            for (P2PServiceListener listener: mServiceListeners)
                                listener.onCredsChanged(mBundle.getString("username"),
                                                        mBundle.getString("password"));
                        }
                    }).start();
                    break;

                default:
                    Log.e(TAG, "Oops! unknown message:" + message.what);
                    super.handleMessage(message);
            }
        }
    }

    /**
     * This class manages the connection setup with the P2P service
     */
    private ServiceConnection mP2PServConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mP2PSrvcMessenger = new Messenger(service);
            mBound = true;
            Log.d(TAG, "bound to P2P Service");

            // When connected to the service, send it our own messenger to establish
            // a two-ways connection
            sendOwnMessenger();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mP2PSrvcMessenger = null;
            mBound = false;
            Log.d(TAG, "unbound from P2P Service");
        }
    };

    /**
     * Send our android messenger back to the service one to setup a full-duplex channel
     */
    private void sendOwnMessenger() {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_REGISTER, 0, 0);
        msg.replyTo = mOwnMessenger;
        Bundle bundle = new Bundle();
        bundle.putString("messengerName", mName);
        msg.setData(bundle);
        try {
            mP2PSrvcMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /***
     * Send message to P2P service
     * @param msg to be sent
     * @param logMsg is the log message that will appear while sending
     */
    private void sendMsgToP2Psrvc(Message msg, String logMsg) {
        if (!mBound) {
            Log.w(TAG, "could not send msg to peer (not bound), operation was: " + logMsg);
            return;
        }
        if (!logMsg.equals(""))
            Log.d(TAG, logMsg);
        try {
            mP2PSrvcMessenger.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "could not send message to P2P service");
            e.printStackTrace();
        }
    }

    ///////// PUBLIC API ///////////////////////////////////////////////////////////////////////////

    /**
     * Add a P2PReceiver to the messanger
     * @param receiver to be added
     */
    public void addReceiver(P2PReceiver receiver) {
        mReceivers.add(receiver);
    }

    /**
     * Add a P2PEventListener to the messenger
     * @param listener
     */
    public void addEventListener(P2PEventListener listener) {
        mEventListeners.add(listener);
    }

    /**
     * Add a P2PServiceListener to the messenger
     * @param listener
     */
    public void addServiceListener(P2PServiceListener listener) {
        mServiceListeners.add(listener);
    }


    public void onStart(Context context) {
        Log.d(TAG, "binding to P2P service (" + mName +")");
        mContext = context;
        Intent intent = new Intent(context, P2PService.class);
        intent.putExtra("Component", mName);
        context.bindService(intent, mP2PServConnection, Context.BIND_AUTO_CREATE);
    }

    public void onStop(Context context) {
        Log.d(TAG, "unbinding from P2P service (" + mName + ")");
        mContext = null;
        if (mBound) {
            context.unbindService(mP2PServConnection);
            mBound = false;
        }
    }

    /**
     * Request P2P connection
     */
    public void connect() {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_CONNECT, 0, 0);
        sendMsgToP2Psrvc(msg, "requesting P2P connection...");
    }

    /**
     * Request P2P reconnection
     */
    public void reconnect() {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_RECONNECT, 0, 0);
        sendMsgToP2Psrvc(msg, "requesting P2P disconnection...");
    }

    /**
     * Sends credentials to P2P service in order to log into the P2P server
     * @param username is the short username (without @mydomain.com)
     * @param password is the raw password
     */
    public void setCredentials(String username, String password) {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_SET_CREDS, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        msg.setData(bundle);
        sendMsgToP2Psrvc(msg, "sending credentials to P2P service (user: " + username + ")...");
    }

    /**
     * Set the server configuration
     * @param conf must contain the following keys:
     *             'hostName': (string) the server hostname
     *             'port': (int) the server port
     *             'domainName': (string) the domain name (such as 'my.domain.net')
     *             Optional keys:
     *             'SSLPin': (String) to avoid SSL certificate check, you can provide a Pin
     *             'pingInterval': (int) ping interval in seconds
     *             'reconnectionEnabled: (Boolean) if true, enables automatic reconnectio
     *             'reconnectionDelay': (int) delay in seconds before trying to reconnect
     */
    public void setServerConf(Bundle conf) {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_SET_SERVER_CONF, 0, 0);
        msg.setData(new Bundle(conf));
        sendMsgToP2Psrvc(msg, "setting P2P conf...");
    }

    /**
     * Request Server
     */
    public void requestServerConf() {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_GET_SERVER_CONF, 0, 0);
        sendMsgToP2Psrvc(msg, "getting P2P conf...");
    }

    /**
     * Send data to JID through P2P service
     * @param bundle containing the message ("message" key is needed)
     * @param to JID to send the message to
     * @throws P2PExceptionBadFormat
     */
    public void sendData(Bundle bundle, String to) throws P2PExceptionBadFormat {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_DATA, 0, 0);
        mP2PInputChecker.checkBundle(bundle);
        mP2PInputChecker.checkJID(to);
        // replace 'message' key by 'p2p_message' key for naming consistency
        bundle.putString("p2p_msg", bundle.getString("message"));
        bundle.putString("p2p_to", to);
        bundle.remove("message");
        msg.setData(bundle);
        sendMsgToP2Psrvc(msg, "sending P2P message...");
    }

    /**
     * Request subscription to a JID
     * @param to is the requested JID
     */
    public void subscribe(String to) {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_SUBSCRIBE, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("to", to);
        msg.setData(bundle);
        sendMsgToP2Psrvc(msg, "subsribing to " + to + "...");
    }

    /**
     * Request P2P account creation
     * @param username is the user name
     * @param password is the password
     */
    public void createAccount(String username, String password) {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_CREATE_ACCOUNT, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        msg.setData(bundle);
        sendMsgToP2Psrvc(msg, "requesting P2P account creation (user: " + username + ")...");
    }

    /**
     * Request P2P connection status
     */
    public void requestRefreshConnectionStatus() {
        Message msg = Message.obtain(null, P2PConstants.MSG_SRVC_P2P_REFRESH_CONN_STATUS, 0, 0);
        sendMsgToP2Psrvc(msg, "requesting P2P connection status...");
    }
}
