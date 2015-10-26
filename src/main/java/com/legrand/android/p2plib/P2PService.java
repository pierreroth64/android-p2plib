/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.legrand.android.p2plib.constants.P2PErrorLevels;
import com.legrand.android.p2plib.constants.P2PGlobals;
import com.legrand.android.p2plib.constants.P2PMessageIDs;
import com.legrand.android.p2plib.exceptions.P2PException;
import com.legrand.android.p2plib.exceptions.P2PExceptionConnError;
import com.legrand.android.p2plib.listeners.P2PRosterListener;
import com.legrand.android.p2plib.utils.P2PThread;
import com.legrand.android.p2plib.utils.P2PUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

/**
 * The P2PService class is an android long running service which handles
 * the interactions with the P2P server and provide an interface to communicate
 * with it. (this interface is based internally on an android messenger).
 *
 * P2PMessenger is the abstraction on client code side to interact with the P2PService.
 * Therefore this P2PService has no public method as all the interactions are done through
 * internal android messenger messages.
 */
public class P2PService extends Service {
    public static final String TAG = P2PGlobals.P2P_TAG + ".Service";

    private AbstractXMPPConnection mP2PConnection = null;
    private Messenger mMessenger;
    private Hashtable<String, Messenger> mClientMessengers = new Hashtable<>(); // Messenger name <-> Messenger object
    private Hashtable<String, Chat> mChats = new Hashtable<>(); // JID <-> Chat object
    public String mCurrentUserName = "";
    public String mCurrentPassword = "";
    private P2PConf mConf;
    private Roster mRoster = null;


    public P2PService() {
        mConf = new P2PConf();
    }

    @Override
    public void onCreate() {
        mMessenger = createMessenger();
    }

    /***
     * P2PConnectionListener is an inner class that make P2PService listen to
     * underlying connection events coming from smack library
     */
    private class P2PConnectionListener implements ConnectionListener {

        private static final String TAG = P2PGlobals.P2P_TAG + " Conn. listener";

        @Override
        public void connected(XMPPConnection connection) {
            Log.d(TAG, "conn state: connected");
            sendConnectionStatusToClientMessengers();
            login(mCurrentUserName, mCurrentPassword);
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.d(TAG, "conn state: authenticated");
            sendConnectionStatusToClientMessengers();
        }

        @Override
        public void connectionClosed() {
            Log.d(TAG, "conn state: close");
            sendConnectionStatusToClientMessengers();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.e(TAG, "conn state: close on error");
            e.printStackTrace();
            sendConnectionStatusToClientMessengers();
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d(TAG, "conn state: reconnection success");
            sendConnectionStatusToClientMessengers();
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.d(TAG, "conn state: reconnecting in " + seconds + " seconds");
        }

        @Override
        public void reconnectionFailed(Exception e){
            Log.e(TAG, "conn state: reconnection failure");
            sendConnectionStatusToClientMessengers();
            e.printStackTrace();
        }
    }

    /**
     * This IncomingHanler class is used by the internal android messenger class to react to received
     * messages from client-code side messengers
     */
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String username, password;
            Message message = Message.obtain(msg);

            switch (message.what) {
                case P2PMessageIDs.MSG_SRVC_REGISTER:
                    // The bound client sent us its messenger! We'll be able to reply to it
                    String messengerName = message.getData().getString("messengerName");
                    mClientMessengers.put(messengerName, message.replyTo);
                    sendSrvcRegisterAck(messengerName);
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_DATA:
                    sendMessageToPeer(message.getData());
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_CREATE_ACCOUNT:
                    username = message.getData().getString("username");
                    password = message.getData().getString("password");
                    createAccount(username, password);
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_SET_CREDS:
                    username = message.getData().getString("username");
                    password = message.getData().getString("password");
                    setCredentials(username, password);
                    sendSrvcCredsAck(username, password);
                    if (credsChanged(username, password))
                        sendSrvcCredsChanged(username, password);
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_SET_SERVER_CONF:
                    if (setServerConf(message.getData())) {
                        if (mP2PConnection != null && mP2PConnection.isConnected()) {
                            reconnect();
                        }
                    }
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_GET_SERVER_CONF:
                    sendServerConfToClientMessengers();
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_LOGIN:
                    username = message.getData().getString("username");
                    password = message.getData().getString("password");
                    setCredentials(username, password);
                    login(username, password);
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_CONNECT:
                    connect();
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_DISCONNECT:
                    disconnect();
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_RECONNECT:
                    reconnect();
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_SUBSCRIBE:
                    subscribe(message.getData().getString("to"));
                    break;
                case P2PMessageIDs.MSG_SRVC_P2P_REFRESH_CONN_STATUS:
                    sendConnectionStatusToClientMessengers();
                    break;
                default:
                    Log.e(TAG, "oops! unknown P2P message:" + message.what);
                    super.handleMessage(message);
            }
        }
    }

    /**
     * Check whether the connection is created and raise an exception if not
     * @throws P2PExceptionConnError
     */
    private void checkConnection() throws P2PExceptionConnError {
        if (mP2PConnection == null)
            throw new P2PExceptionConnError("connection not setup (null)");
    }

    /**
     * Set server configuration
     * @param conf bundle with key-value pairs
     * @return true if conf changed
     */
    private Boolean setServerConf(Bundle conf) {

        Boolean confChanged = false;
        for (String confKey: conf.keySet()) {
            if (mConf.mSupportedParams.contains(confKey)) {
                confChanged = true;
            }
        }
        if (conf.containsKey("hostName"))
            mConf.mHostName = conf.getString("hostName");
        if (conf.containsKey("port"))
            mConf.mPort = conf.getInt("port");
        if (conf.containsKey("domainName"))
            mConf.mDomainName = conf.getString("domainName");
        if (conf.containsKey("SSLPin"))
            mConf.mSSLPin = conf.getString("SSLPin");
        if (conf.containsKey("pingInterval"))
            mConf.mPingInterval = conf.getInt("pingInterval");
        if (conf.containsKey("reconnectionEnabled"))
            mConf.mReconnectionEnabled = conf.getBoolean("reconnectionEnabled");
        if (conf.containsKey("reconnectionDelay"))
            mConf.mReconnDelaySeconds = conf.getInt("reconnectionDelay");

        Log.d(TAG, "Server configuration changed");

        return confChanged;
    }

    private void sendServerConfToClientMessengers() {
        Bundle conf = P2PConf.createBundleFromConf(mConf);
        try {
            Set<String> names = mClientMessengers.keySet();
            for(String name: names){
                Message msg = Message.obtain(null, P2PMessageIDs.MSG_CLIENT_P2P_CONF, 0, 0);
                msg.setData(conf);
                mClientMessengers.get(name).send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "could not send server conf to client messengers");
            e.printStackTrace();
        }

    }


    /**
     * Check whether credentials changed
     * @param username is the given username to be compared to current one
     * @param password is the given password to be compared to current one
     * @return true if creds changed
     */
    private Boolean credsChanged(String username, String password) {
        if ((username != mCurrentUserName) || (password != mCurrentPassword))
            return true;
        else
            return false;
    }

    /**
     * Send a service acknowledge message back the the client messenger
     * @param messengerName is the clien messenger name
     */
    private void sendSrvcRegisterAck(String messengerName) {
        Message msg = Message.obtain(null, P2PMessageIDs.MSG_SRVC_REGISTER_ACK, 0, 0);
        try {
            Log.d(TAG, "received client messenger: " + messengerName + ", sending register ack to it");
            mClientMessengers.get(messengerName).send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a credential acknowledge event to client messengers
     * @param username received
     * @param password received
     */
    private void sendSrvcCredsAck(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_ACK_CREDS, bundle);
    }

    /**
     * Send a credential change event to client messengers
     * @param username received
     * @param password received
     */
    private void sendSrvcCredsChanged(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_CREDS_CHANGED, bundle);
    }

    /**
     * Connect to P2P server
     * @return the running connecting thread
     */
    private Thread connect() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "connecting to P2P server...");
                try {
                    if (mP2PConnection == null)
                        mP2PConnection = createConnection();
                    if (!mP2PConnection.isConnected()) {
                        mP2PConnection.connect();
                        Log.d(TAG, "connected to P2P server");
                    } else {
                        Log.d(TAG, "already connected to P2P server");
                    }
                } catch (SmackException.AlreadyConnectedException e) {
                    Log.d(TAG, "(already) connected to P2P server");
                } catch (SmackException | IOException | XMPPException | IllegalArgumentException e) {
                    sendErrorToClientMessengers(P2PErrorLevels.P2P_LEVEL_WARNING, "could not connect to P2P server", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Set current P2P credentials
     * @param username
     * @param password
     */
    private void setCredentials(String username, String password) {
        mCurrentUserName = username;
        mCurrentPassword = password;
        Log.d(TAG, "set credentials for " + username);
    }

    /**
     * Login to the P2P server
     * @param username
     * @param password
     */
    private void login(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        new Thread(new P2PThread(bundle) {
            public void run() {
                String username = mBundle.getString("username");
                String password = mBundle.getString("password");
                Log.d(TAG, "login to P2P server with username: " + username + "...");
                try {
                    if (mP2PConnection == null) {
                        Log.w(TAG, "could not login since connection not setup");
                        return;
                    }
                    if (!mP2PConnection.isAuthenticated()) {
                        mP2PConnection.login(username, password);
                        Log.d(TAG, "logged into P2P as " + mCurrentUserName);
                    } else {
                        Log.d(TAG, "Already logged into P2P as " + mCurrentUserName);
                    }
                } catch (SmackException | IOException | XMPPException | IllegalArgumentException e) {
                    sendErrorToClientMessengers(P2PErrorLevels.P2P_LEVEL_WARNING, "could not log into P2P server", e.getMessage());
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("password", password);
                    sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_AUTHENTICATION_FAILED, bundle);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * May create a chat for this JID if not already created
     * @param JID to create the chat with
     * @return the created chat or null on failure
     */
    private Chat mayCreateChatForJID(String JID) {

        Chat chat = mChats.get(JID);
        if (chat != null) return chat;

        try {
            checkConnection();
            ChatManager chatmanager = ChatManager.getInstanceFor(mP2PConnection);
            chat = chatmanager.createChat(JID, new ChatMessageListener() {
                public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message", message.getBody());
                    bundle.putString("from", P2PUtils.extractWhoFromResource(message.getFrom()));
                    bundle.putString("jid", P2PUtils.extractJIDFromResource(message.getFrom()));
                    sendDataToClientMessengers(bundle);
                }
            });
            Log.d(TAG, "chat created for device: " + JID);
            mChats.put(JID, chat);
            return chat;
        } catch (P2PExceptionConnError e) {
            sendErrorToClientMessengers(P2PErrorLevels.P2P_LEVEL_ERROR, "could not create chat for device " + JID, e.getMessage());
            return null;
        }
    }

    /**
     * Disconnect from the P2P server
     */
    private Thread disconnect() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "disconnecting from P2P server...");
                if (mP2PConnection != null) {
                    mP2PConnection.disconnect();
                    mP2PConnection = null;
                }
                Log.d(TAG, "disconnected from P2P server");
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Reconnect to the P2P server
     */
    private void reconnect() {
        Thread thread = disconnect();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.w(TAG, "disconnect thread was interrupted");
            e.printStackTrace();
        }
        connect();
    }

    /**
     * Create account on the P2P server
     * @param username of the created account
     * @param password of the created account
     */
    private void createAccount(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        new Thread(new P2PThread(bundle) {
            public void run() {
                String username = mBundle.getString("username");
                String password = mBundle.getString("password");
                try {
                    Log.d(TAG, "creating account for: " + username);
                    checkConnection();
                    AccountManager accountMgr = AccountManager.getInstance(mP2PConnection);
                    accountMgr.createAccount(username, password);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("password", password);
                    sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_ACCOUNT_CREATED, bundle);
                } catch (SmackException|XMPPException|P2PExceptionConnError e) {
                    sendErrorToClientMessengers(P2PErrorLevels.P2P_LEVEL_ERROR, "error when creating xmpp account for: " + username, e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Send error from this service to the client messengers (see: P2PMessenger)
     * @param level is the error level (see. P2PErrorLevels)
     * @param message is the error message
     * @param detailedMessage is an optional detailed error message
     */
    private void sendErrorToClientMessengers(int level, String message, String detailedMessage) {
        Bundle error = new Bundle();
        error.putInt("level", level);
        error.putString("message", message);
        error.putString("detailedMessage", detailedMessage);

        if (level == P2PErrorLevels.P2P_LEVEL_ERROR)
            Log.e(TAG, message + " (" + detailedMessage + ")");
        else
            Log.w(TAG, message + " (" + detailedMessage + ")");

        try {
            Set<String> names = mClientMessengers.keySet();
            for(String name: names){
                Message msg = Message.obtain(null, P2PMessageIDs.MSG_SRVC_P2P_ERROR, 0, 0);
                msg.setData(error);
                mClientMessengers.get(name).send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "could not send error to client messengers");
            e.printStackTrace();
        }
    }

    /**
     * Send data from this service to the client messengers (see: P2PMessenger)
     * @param bundle containing the data to be sent
     */
    private void sendDataToClientMessengers(Bundle bundle) {
        try {
            Set<String> names = mClientMessengers.keySet();
            for(String name: names){
                Message msg = Message.obtain(null, P2PMessageIDs.MSG_CLIENT_P2P_DATA, 0, 0);
                msg.setData(bundle);
                mClientMessengers.get(name).send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "could not send data to client messengers");
            e.printStackTrace();
        }
    }

    /**
     * Send connection status to client messengers according to current P2P connection state
     */
    private void sendConnectionStatusToClientMessengers() {
        Bundle bundle;

        if (mP2PConnection == null) {
            sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_DISCONNECTED, null);
        } else if (mP2PConnection.isAuthenticated()) {
            bundle = new Bundle();
            bundle.putString("username", mCurrentUserName);
            sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_AUTHENTICATED, bundle);
        } else if (mP2PConnection.isConnected()) {
            sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_CONNECTED, null);
        } else {
            sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_DISCONNECTED, null);
        }
    }


    /**
     * Send event to client messengers (see: P2PMessenger)
     * @param event see P2PMessageIDs.java
     * @param bundle bundle containing event information
     */
    private void sendEventToClientMessengers(int event, Bundle bundle) {
        try {
            Set<String> names = mClientMessengers.keySet();
            for(String name: names){
                Message msg = Message.obtain(null, event, 0, 0);
                if (bundle != null)
                    msg.setData(bundle);
                mClientMessengers.get(name).send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "could not send event to client messengers");
            e.printStackTrace();
        }
    }

    /**
     * Send presence to client messengers (see: P2PMessenger)
     * @param presence is the smacklib presence object
     */
    private void sendPresenceToClientMessengers(Presence presence) {
        try {
            Set<String> names = mClientMessengers.keySet();
            for(String name: names){
                Message msg = Message.obtain(null, P2PMessageIDs.MSG_CLIENT_P2P_PRESENCE, 0, 0);
                Bundle bundle = P2PUtils.createPresenceBundle(presence);
                msg.setData(bundle);
                mClientMessengers.get(name).send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "could not send msg to client messengers");
            e.printStackTrace();
        }
    }

    /**
     * Send message to peer
     * @param bundle has two entries (keys):
     *               - "p2p_msg", string containing the data
     *               - "p2p_to", string containing the JID
     */
    private void sendMessageToPeer(Bundle bundle) {
        new Thread(new P2PThread(bundle) {
            public void run() {
                String message = mBundle.getString("p2p_msg");
                String to = mBundle.getString("p2p_to");
                try {
                    Chat chat = mayCreateChatForJID(to);
                    if (chat == null) {
                        throw new P2PException("could not create chat for JID " + to);
                    }
                    Log.d(TAG, "sending data: " + message + " to " + chat.getParticipant() + "...");
                    chat.sendMessage(message);
                    Bundle bundle = new Bundle();
                    bundle.putString("message", message);
                    sendEventToClientMessengers(P2PMessageIDs.MSG_CLIENT_P2P_EVENT_DATA_SENT, bundle);
                } catch (SmackException|P2PException e) {
                    Log.e(TAG, "error when sending xmpp message (" + e.getMessage() + ")");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, intent.getStringExtra("Component") + " is now bound to " + TAG);
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "P2P Service started");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Create a P2P connection object
     * @return connection object
     */
    private AbstractXMPPConnection createConnection() {
        Log.d(TAG, "creating connection...");
        if (! P2PConf.hasMinimalConf(mConf)) {
            Log.d(TAG, "P2P configuration is not complete, using default one");
            mConf = P2PConf.createDefaultConf();
        }
        AbstractXMPPConnection conn = new P2PConnectionBuilder().createConnection(mConf);
        conn.addConnectionListener(new P2PConnectionListener());
        mRoster = Roster.getInstanceFor(conn);
        mRoster.addRosterListener(new P2PRosterListener() {
            @Override
            public void presenceChanged(Presence presence) {
                mayCreateChatForJID(presence.getFrom());
                sendPresenceToClientMessengers(presence);
            }
        });
        Log.d(TAG, "connection created");
        return conn;
    }

    /**
     * Create an internal android messenger to communicate with client ones (see: P2PMessenger)
     * @return an android messenger
     */
    private Messenger createMessenger() {
        Messenger msgr = new Messenger(new IncomingHandler());
        return msgr;
    }

    /**
     * Request a subscription to a given JID to the P2P server
     * @param to
     */
    private void subscribe(String to) {
        Bundle bundle = new Bundle();
        bundle.putString("to", to);
        new Thread(new P2PThread(bundle) {
            public void run() {
                String to = mBundle.getString("to");
                Log.d(TAG, "subscribing to: " + to + "...");
                Presence subscribe = new Presence(Presence.Type.subscribe);
                subscribe.setTo(to);
                try {
                    checkConnection();
                    mP2PConnection.sendStanza(subscribe);
                }
                catch (SmackException.NotConnectedException|P2PExceptionConnError e) {
                    Log.d(TAG, "could not send subscription (" + e.getMessage() + ")");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
