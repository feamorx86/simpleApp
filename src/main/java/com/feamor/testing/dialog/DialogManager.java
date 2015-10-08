package com.feamor.testing.dialog;

import com.gs.collections.api.tuple.primitive.IntIntPair;
import com.gs.collections.api.tuple.primitive.IntObjectPair;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 30.09.2015.
 */

public class DialogManager {

    public HashMap<String, DialogClient> getClientsByName() {
        return clientsByName;
    }

    private HashMap<Integer, DialogClient> clients = new HashMap<Integer, DialogClient>();
    private HashMap<String, DialogClient> clientsByName = new HashMap<String, DialogClient>();
    private HashMap<String, DialogClient> activeClients = new HashMap<String, DialogClient>();

    private HashMap<Integer, DialogMessage> messages = new HashMap<Integer, DialogMessage>();

    private HashMap<Integer, ArrayList<DialogMessage>> sendedMessagesFormClient = new HashMap<Integer, ArrayList<DialogMessage>>();
    private HashMap<Integer, ArrayList<DialogMessage>> receivedMessagesFormClient = new HashMap<Integer, ArrayList<DialogMessage>>();
    private HashMap<Integer, ArrayList<DialogMessage>> newMessagesFormClient = new HashMap<Integer, ArrayList<DialogMessage>>();

    private AtomicInteger clientIdGenerator = new AtomicInteger();
    private AtomicInteger sessionIdGenerator = new AtomicInteger();
    private AtomicInteger messageIdGenerator = new AtomicInteger();

    public static class Operations{
        public static final int ACTION_NEW_CLIENT = 100;
        public static final int NEW_CLIENT_SUCCESS = 120;
        public static final int NEW_CLIENT_ERROR_INVALED_DATA = 130;
        public static final int NEW_CLIENT_ERROR_NAME_BUSY = 131;

        public static final int ACTION_LOGIN = 200;
        public static final int LOGIN_SUCCESS= 201;
        public static final int LOGIN_ERROR_INVALED_DATA = 210;
        public static final int LOGIN_ERROR_INVALED_PASSWORD_OR_LOGIN = 220;
        public static final int ERROR_USER_NOT_LOGIN = 260;

        public static final int ACTION_LOGOUT = 300;
        public static final int LOGOUT_SUCCESS= 301;
        public static final int LOGOUT_ERROR_NO_SUCH_USER = 303;
        public static final int LOGOUT_ERROR_USER_NOT_LOGIN = 304;

        public static final int ACTION_GET_CLIENT_INFO = 400;
        public static final int GET_CLIENT_INFO_SUCCESS= 401;
        public static final int GET_CLIENT_INFO_ERROR_NO_REQUESTED_CLIENT= 403;

        public static final int ACTION_SEND_MESSAGE = 500;
        public static final int SEND_MESSAGE_SUCCESS = 501;
        public static final int SEND_MESSAGE_ERROR_NO_SUCH_RECEIVER = 503;

        public static final int ACTION_RECEIVED_MESSAGES = 600;

        public static final int ACTION_NEW_MESSAGE = 700;
        public static final int NEW_MESSAGE_NO_MESSAGES = 701;
        public static final int NEW_MESSAGE_COUNT = 702;
        public static final int NEW_MESSAGES_READ_ALL = 703;

        public static final int ACTION_GET_SENDED = 800;
        public static final int GET_SENDED_TOTAL = 801;
        public static final int GET_SENDED_MESSAGES = 802;
        public static final int GET_SENDED_ERROR_INVALED_PARAMS = 803;

        public static final int ACTION_GET_RECEIVED = 900;
        public static final int GET_RECEIVED_TOTAL = 901;
        public static final int GET_RECEIVED_MESSAGES = 902;
        public static final int GET_RECEIVED_ERROR_INVALED_PARAMS = 903;

        public static final int ACTION_REMOVE_MESSAGE = 1000;
        public static final int REMOVE_MESSAGE_SUCCESS = 1001;
        public static final int REMOVE_MESSAGE_NO_SUCH_MESSAGE = 1002;

    }

    public DialogManager() {

    }

    public int createNewUser(String name, String password, DialogClient.DialogClientActionListener actionListener) {
        int result;
        if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(password)) {
            String localName = name.toUpperCase();
            if (clientsByName.containsKey(localName)) {
                result = Operations.NEW_CLIENT_ERROR_NAME_BUSY;
            } else {
                DialogClient client = new DialogClient(actionListener);
                client.setId(clientIdGenerator.getAndIncrement());
                client.setName(name);
                client.setPassword(password);
                client.setStatus(DialogClient.STATUS_OFFLINE);
                clients.put(client.getId(), client);
                clientsByName.put(localName, client);
                result = Operations.NEW_CLIENT_SUCCESS;
            }
        } else {
            result = Operations.NEW_CLIENT_ERROR_INVALED_DATA;
        }
        return result;
    }

    public IntObjectPair<IntObjectPair<String>> loginClient(String name, String password) {
        IntObjectPair<IntObjectPair<String>> result;
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)){
            result = PrimitiveTuples.pair(Operations.LOGIN_ERROR_INVALED_DATA, null);
        } else {
            String localName = name.toUpperCase();
            DialogClient client = clientsByName.get(localName);
            if (client != null && client.getPassword().equals(password)) {
                String session = "TEST_"+sessionIdGenerator.incrementAndGet();
                activeClients.put(session, client);
                IntObjectPair<String> status = PrimitiveTuples.pair(client.getId(), session);
                client.setStatus(DialogClient.STATUS_ONLINE);
                result = PrimitiveTuples.pair(Operations.LOGIN_SUCCESS, status);
//                notifyAction(Operations.ACTION_LOGIN, client);
            } else {
                result = PrimitiveTuples.pair(Operations.LOGIN_ERROR_INVALED_PASSWORD_OR_LOGIN, null);
            }
        }
        return result;
    }

    public  int logoutClient(DialogClient client) {
        int result;
        client.setStatus(DialogClient.STATUS_OFFLINE);
        activeClients.remove(client.getId());
        result = Operations.LOGOUT_SUCCESS;
//        notifyAction(Operations.ACTION_LOGOUT, client, null);
        return result;
    }

    public DialogClient getActiveUser(int clientId, String sessionId) {
        DialogClient client = activeClients.get(sessionId);
        if (client != null && client.getId() == clientId) {
            return client;
        } else {
            return null;
        }
    }

    public void notifyAction(int action, DialogClient client, Object param) {
        if (client!=null && client.getActionListener()!=null){
            client.getActionListener().onAction(action, param);
        }
    }

    public IntObjectPair<DialogClient.ClientInfo> getClientInfo(DialogClient client, int requestedId) {
        IntObjectPair<DialogClient.ClientInfo> result;
        DialogClient requestedClient = clients.get(requestedId);
        if (requestedClient!=null) {
            DialogClient.ClientInfo info = requestedClient.createinfo();
            result = PrimitiveTuples.pair(Operations.GET_CLIENT_INFO_SUCCESS, info);
        } else {
            result = PrimitiveTuples.pair(Operations.GET_CLIENT_INFO_ERROR_NO_REQUESTED_CLIENT, null);
        }
        return  result;
    }

    public int [] getActiveClientsIds() {
        int [] result;
        if (activeClients.size() >  0) {
            result = new int[activeClients.size()];
            int i = 0;
            for (DialogClient client : activeClients.values()) {
                result[i] = client.getId();
                i++;
            }
        } else {
            result = null;
        }
        return result;
    }

    private void addMessageToList(HashMap<Integer, ArrayList<DialogMessage>> map, int clientId, DialogMessage message) {
        ArrayList<DialogMessage> clientMessages;
        clientMessages = map.get(clientId);
        if (clientMessages == null) {
            clientMessages = new ArrayList<DialogMessage>();
            map.put(clientId, clientMessages);
        }
        clientMessages.add(message);
    }

    public int sendMessage(DialogClient fromClient, int toClientId, String messageText) {
        int result;
        DialogClient receiverClient = clients.get(toClientId);
        if (receiverClient != null) {
            int messageId = messageIdGenerator.incrementAndGet();
            DialogMessage message = new DialogMessage();
            message.setId(messageId);
            message.setSenderId(fromClient.getId());
            message.setReceiverId(toClientId);
            message.setSendTime(Calendar.getInstance().getTime());
            message.setMessage(messageText);
            messages.put(messageId, message);
            addMessageToList(sendedMessagesFormClient, fromClient.getId(), message);
            addMessageToList(receivedMessagesFormClient, toClientId, message);
            addMessageToList(newMessagesFormClient, toClientId, message);
            notifyAction(Operations.ACTION_NEW_MESSAGE, clients.get(toClientId), null);
            result = Operations.SEND_MESSAGE_SUCCESS;
        } else {
            result = Operations.SEND_MESSAGE_ERROR_NO_SUCH_RECEIVER;
        }
        return result;
    }

    public IntIntPair hasNewMessages(DialogClient client) {
        IntIntPair result;
        ArrayList<DialogMessage> clientMessages = newMessagesFormClient.get(client.getId());
        if (clientMessages != null) {
            result = PrimitiveTuples.pair(Operations.NEW_MESSAGE_COUNT, clientMessages.size());
        } else {
            result = PrimitiveTuples.pair(Operations.NEW_MESSAGE_COUNT, 0);
        }
        return result;
    }

    public IntObjectPair<ArrayList<DialogMessage>> readNewMessages(DialogClient client) {
        IntObjectPair<ArrayList<DialogMessage>> result;
        ArrayList<DialogMessage> clientMessages = newMessagesFormClient.get(client.getId());
        if (clientMessages!=null) {
            newMessagesFormClient.remove(client.getId());
            result = PrimitiveTuples.pair(Operations.NEW_MESSAGES_READ_ALL, clientMessages);
        } else {
            result = PrimitiveTuples.pair(Operations.NEW_MESSAGES_READ_ALL, null);
        }
        return result;
    }

    public IntIntPair getSendMessagesTotal(DialogClient client) {
        IntIntPair result;
        ArrayList<DialogMessage> clientMessages = sendedMessagesFormClient.get(client.getId());
        if (clientMessages!=null) {
            result = PrimitiveTuples.pair(Operations.GET_SENDED_TOTAL, clientMessages.size());
        } else {
            result = PrimitiveTuples.pair(Operations.GET_SENDED_TOTAL, 0);
        }
        return result;
    }

    public IntObjectPair<ArrayList<DialogMessage>> getSendedMessages(DialogClient client, int offset, int count) {
        IntObjectPair<ArrayList<DialogMessage>> result;
        ArrayList<DialogMessage> clientMessages = sendedMessagesFormClient.get(client.getId());
        if (clientMessages != null && clientMessages.size() > 0) {
            if (offset < clientMessages.size()){
                int to = offset + count;
                if (to > clientMessages.size()) {
                    to = clientMessages.size();
                }
                if (offset == to) {
                    result = PrimitiveTuples.pair(Operations.GET_SENDED_MESSAGES, null);
                } else {
                    ArrayList<DialogMessage> resultMessages = new ArrayList<DialogMessage>(to - offset);
                    for (int i = offset; i < to; i++) {
                        resultMessages.add(clientMessages.get(i));
                    }
                    result = PrimitiveTuples.pair(Operations.GET_SENDED_MESSAGES, resultMessages);
                }
            } else {
                result = PrimitiveTuples.pair(Operations.GET_SENDED_ERROR_INVALED_PARAMS, null);
            }
        } else {
            result = PrimitiveTuples.pair(Operations.GET_SENDED_MESSAGES, null);
        }
        return  result;
    }

    public IntObjectPair<ArrayList<DialogMessage>> getReceivedMessages(DialogClient client, int offset, int count) {
        IntObjectPair<ArrayList<DialogMessage>> result;
        ArrayList<DialogMessage> clientMessages = receivedMessagesFormClient.get(client.getId());
        if (clientMessages != null && clientMessages.size() > 0) {
            if (offset < clientMessages.size()){
                int to = offset + count;
                if (to > clientMessages.size()) {
                    to = clientMessages.size();
                }
                if (offset == to) {
                    result = PrimitiveTuples.pair(Operations.GET_RECEIVED_MESSAGES, null);
                } else {
                    ArrayList<DialogMessage> resultMessages = new ArrayList<DialogMessage>(to - offset);
                    for (int i = offset; i < to; i++) {
                        resultMessages.add(clientMessages.get(i));
                    }
                    result = PrimitiveTuples.pair(Operations.GET_RECEIVED_MESSAGES, resultMessages);
                }
            } else {
                result = PrimitiveTuples.pair(Operations.GET_RECEIVED_ERROR_INVALED_PARAMS, null);
            }
        } else {
            result = PrimitiveTuples.pair(Operations.GET_RECEIVED_MESSAGES, null);
        }
        return  result;
    }

    private int getMessagePosition(ArrayList<DialogMessage> clientMessages, int messageId) {
        int result = -1;
        if (clientMessages !=null) {
            for (int i = 0; i < clientMessages.size(); i++) {
                if (clientMessages.get(i).getId() == messageId) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    public int removeSendedMessage(DialogClient client, int messageId) {
        int result;
        ArrayList<DialogMessage> clientMessages = sendedMessagesFormClient.get(client.getId());
        int pos = getMessagePosition(clientMessages, messageId);
        if (pos!= -1) {
            DialogMessage message = clientMessages.remove(pos);
            if (clientMessages.size() == 0) {
                sendedMessagesFormClient.remove(client.getId());
            }
            clientMessages = receivedMessagesFormClient.get(message.getReceiverId());
            pos = getMessagePosition(clientMessages, messageId);
            if (pos == -1) {
                clientMessages = newMessagesFormClient.get(message.getReceiverId());
                pos = getMessagePosition(clientMessages, messageId);
                if (pos != -1) {
                    clientMessages.remove(pos);
                    if (clientMessages.size() == 0) {
                        newMessagesFormClient.remove(message.getReceiverId());
                    }
                }
                messages.remove(messageId);
            }
            result = Operations.REMOVE_MESSAGE_SUCCESS;
        } else {
            result = Operations.REMOVE_MESSAGE_NO_SUCH_MESSAGE;
        }
        return result;
    }

    public int removeReceivedMessage(DialogMessage client, int messageId) {
        int result;
        ArrayList<DialogMessage> clientMessages = receivedMessagesFormClient.get(client.getId());
        int pos = getMessagePosition(clientMessages, messageId);
        if (pos!= -1) {
            DialogMessage message = clientMessages.remove(pos);
            if (clientMessages.size() == 0) {
                receivedMessagesFormClient.remove(client.getId());
            }
            clientMessages = sendedMessagesFormClient.get(message.getSenderId());
            pos = getMessagePosition(clientMessages, messageId);
            if (pos == -1) {
                clientMessages = newMessagesFormClient.get(client.getId());
                pos = getMessagePosition(clientMessages, messageId);
                if (pos != -1) {
                    clientMessages.remove(pos);
                    if (clientMessages.size() == 0) {
                        newMessagesFormClient.remove(client.getId());
                    }
                }
                messages.remove(messageId);
            }
            result = Operations.REMOVE_MESSAGE_SUCCESS;
        } else {
            result = Operations.REMOVE_MESSAGE_NO_SUCH_MESSAGE;
        }
        return result;
    }

    public void clear() {
        clients.clear();
        clientsByName.clear();
        activeClients.clear();;
        messages.clear();

        sendedMessagesFormClient.clear();
        receivedMessagesFormClient.clear();
        newMessagesFormClient.clear();

        clientIdGenerator.set(0);
        sessionIdGenerator.set(0);
        messageIdGenerator.set(0);

    }
}
