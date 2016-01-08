package com.feamor.testing.server.games.chatgame;

import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GamePlayer;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by feamor on 26.11.2015.
 */
public class ChatPlayer implements ActiveGame.GamePlayerAccessor{

    public static class ChatPlayerStatus{
        public static final int UNKNOWN = 0;
        public static final int OFFLINE = 1;
        public static final int ONLINE = 2;
        public static final int CONNECTING = 3;
    }

    private GamePlayer gamePlayer;
    private int status;
    Date connectTime;
    private HashMap<Integer, ArrayList<ChatMessage>> receivedMessages;
    private HashMap<Integer, ArrayList<ChatMessage>> sendedMessages;

    public ChatPlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        status = ChatPlayerStatus.UNKNOWN;
        receivedMessages = new HashMap<>();
        sendedMessages = new HashMap<>();
        connectTime = Calendar.getInstance().getTime();
    }

    @Override
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public boolean isOnline() {
        return status == ChatPlayerStatus.ONLINE;
    }

    public boolean isOffline() {
        return status == ChatPlayerStatus.OFFLINE;
    }

    public boolean isConnecting() {
        return status == ChatPlayerStatus.CONNECTING;
    }

    public boolean isNotOnOffline() {
        return status != ChatPlayerStatus.ONLINE && status != ChatPlayerStatus.OFFLINE;
    }

    public int getId() {
        return  gamePlayer.getId().getId();
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void sendMessage(String message, ChatPlayer toPlayer)
    {
        int id = toPlayer.getId();
        ChatMessage chatMessage = new ChatMessage(message, Calendar.getInstance().getTime(), id);
        ArrayList<ChatMessage> messagesList = sendedMessages.get(id);
        if (messagesList == null) {
            messagesList = new ArrayList<>();
            sendedMessages.put(id, messagesList);
        }
        messagesList.add(chatMessage);
    }

    public void receiveMessage(String message, ChatPlayer fromPlayer)
    {
        int id = fromPlayer.getId();
        ChatMessage chatMessage = new ChatMessage(message, Calendar.getInstance().getTime(), id);
        ArrayList<ChatMessage> messagesList = receivedMessages.get(id);
        if (messagesList == null) {
            messagesList = new ArrayList<>();
            receivedMessages.put(id, messagesList);
        }
        messagesList.add(chatMessage);
    }

    public void changeStatus(int newStatus) {
        status = newStatus;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<ChatMessage> getReceivedMessages(int player) {
        ArrayList<ChatMessage> messages = receivedMessages.get(player);
        return messages;
    }

    public ArrayList<ChatMessage> getSendedMessages(int player) {
        ArrayList<ChatMessage> messages = sendedMessages.get(player);
        return messages;
    }

    public int getSendedMessagesCount(int player) {
        ArrayList<ChatMessage> messages = sendedMessages.get(player);
        int result = 0;
        if (messages != null) {
            result = messages.size();
        }
        return result;
    }

    public int getReceivedMessagesCount(int player) {
        ArrayList<ChatMessage> messages = receivedMessages.get(player);
        int result = 0;
        if (messages != null) {
            result = messages.size();
        }
        return result;
    }

    public ArrayList<ChatMessage> getSendedMessages(int player, int offset, int count) {
        ArrayList<ChatMessage> allMessages = sendedMessages.get(player);
        ArrayList<ChatMessage> result = null;
        if (allMessages!=null) {
            result = new ArrayList<>();
            for(int i=offset; i<count && i<allMessages.size(); i++) {
                result.add(allMessages.get(i));
            }
        }
        return  result;
    }

    public ArrayList<ChatMessage> getReceivedMessages(int player, int offset, int count) {
        ArrayList<ChatMessage> allMessages = receivedMessages.get(player);
        ArrayList<ChatMessage> result = null;
        if (allMessages!=null) {
            result = new ArrayList<>();
            for(int i=offset; i<count && i<allMessages.size(); i++) {
                result.add(allMessages.get(i));
            }
        }
        return  result;
    }

    public JSONObject getShortInfo(){
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("firstName", getGamePlayer().getInfo().firstName);
        json.put("secondName", getGamePlayer().getInfo().secondName);
        json.put("iconUri", getGamePlayer().getInfo().iconUri);
        return json;
    }
}