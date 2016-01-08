package com.feamor.testing.server.games.chatgame;

import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.DataUtils;
import com.feamor.testing.server.utils.Ids;
import com.feamor.testing.server.utils.JSONBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by feamor on 24.10.2015.
 */
public class ChatGameController extends ActiveGame {

    public static class Results {
        public static final int SUCCESS = 1;
        public static final int INVALID_DATA = 2;
        public static final int NO_USER_WITH_SUCH_ID = 3;
        public static final int INVALID_PLAYER_STATUS = 4;
        public static final int USER_TIMEOUT = 5;
        public static final int UNSUPPORTED_COMMAND = 10;
        public static final int ERROR = 30;
        public static final int END_GAME = 40;
        public static final int USER_DISCONNECT_REQUEST = 41;
    }

    @Override
    protected void onCreate(GameCreator creator) {

    }

    @Override
    protected void onStarted() {

    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    protected GamePlayerAccessor createPlayer(GamePlayer gamePlayer) {
        ChatPlayer player = new ChatPlayer(gamePlayer);
        return player;
    }

    @Override
    protected void onNewPlayer(GamePlayerAccessor player) {
        ((ChatPlayer)player).changeStatus(ChatPlayer.ChatPlayerStatus.CONNECTING);
    }

    @Override
    protected void onMessage(int action, GamePlayerAccessor player, ByteBuf data) {
        switch(action) {
            case Ids.Actions.GameLogic.ChatGame.CLIENT_REQUEST_ENTER:
                userRequestEnter((ChatPlayer) player);
                break;
            case Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT:
                userDisconnected((ChatPlayer) player);
                break;
            case Ids.Actions.GameLogic.ChatGame.GET_ONLINE_CLIENTS:
                sendOnlinePlayers((ChatPlayer) player);
                break;
            case Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO:
                getUserInfo((ChatPlayer) player, data);
                break;
            case Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT:
                sendMessages((ChatPlayer)player, data);
                break;
            case Ids.Actions.GameLogic.ChatGame.GET_MESSAGES:
                getMessages((ChatPlayer) player, data);
                break;
            default:
            {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.UNSUPPORTED_COMMAND);
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, action, player.getGamePlayer().getSession(), reply);
            }
                break;
        }
    }

    private void getMessages(ChatPlayer player, ByteBuf data) {
        if (player.isOnline()) {
            int requestId = data.readInt();
            boolean isSend = data.readBoolean();
            int forClientId = data.readInt();
            int offset = data.readInt();
            int count = data.readInt();

            ArrayList<ChatMessage> messages;
            int total = -1;
            if (isSend) {
                messages = player.getSendedMessages(forClientId, offset, count);
                total = player.getSendedMessagesCount(forClientId);
            } else {
                messages = player.getReceivedMessages(forClientId, offset, count);
                total = player.getReceivedMessagesCount(forClientId);
            }

            JSONObject json = new JSONObject();
            json.put("total", total);
            if (messages != null) {
                JSONArray messagesJson = new JSONArray();
                for (ChatMessage msg : messages) {
                    messagesJson.put(msg.toJson());
                }
                json.put("messages", messagesJson);
            }

            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.SUCCESS);
            reply.writeInt(requestId);
            DataUtils.writeString(reply, json.toString());
            this.messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_MESSAGES, player.getGamePlayer().getSession(), reply);
        } else {
            disconnectUserWithInvaledStatus(player, Ids.Actions.GameLogic.ChatGame.GET_MESSAGES, "Error. User is not online, but try to get message");
        }
    }

    private void sendMessages(ChatPlayer player, ByteBuf data) {
        if (player.isOnline()) {
            int senderId = player.getId();
            int sendToId = data.readInt();
            String message = DataUtils.readString(data);

            ChatPlayer sendTo = (ChatPlayer) users.get(sendToId);
            if (sendTo != null) {
                if (sendTo.isOnline()) {
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(senderId);
                    DataUtils.writeString(reply, message);
                    messages.send(sendTo.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.RECEIVE_MESSAGE_FROM_CLIENT, sendTo.getGamePlayer().getSession(), reply);

                    reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.SUCCESS);
                    messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getGamePlayer().getSession(), reply);

                    player.sendMessage(message, sendTo);
                    sendTo.receiveMessage(message, player);
                } else {
                    //TODO: notify offline message,
                    //TODO: send
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.UNSUPPORTED_COMMAND);
                    messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getGamePlayer().getSession(), reply);
                }
            } else {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.NO_USER_WITH_SUCH_ID);
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getGamePlayer().getSession(), reply);
            }
        } else {
            disconnectUserWithInvaledStatus(player, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, "Error. User is not online, but try to send message");
        }
    }

    private void disconnectUserWithInvaledStatus(ChatPlayer player, int action, String error) {
        synchronized (users) {
            users.remove(player.getId());
        }
        disconnectUser(player, Results.ERROR, Results.INVALID_PLAYER_STATUS, JSONBuilder
                .start("action", action)
                .with("status", player.getStatus())
                .with("message", error).get());
    }

    private void getUserInfo(ChatPlayer player, ByteBuf data) {
        if (player.isOnline()) {
            int playerId = data.readInt();
            ChatPlayer info = (ChatPlayer) users.get(playerId);
            if (info != null) {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.SUCCESS);

                JSONObject json = new JSONObject();
                JSONObject userInfoJson = info.getGamePlayer().getInfo().getShortAsJson();
                json.put("user", userInfoJson);
                ArrayList<ChatMessage> receivedMessages = info.getReceivedMessages(info.getId());
                ArrayList<ChatMessage> sendedMessages = info.getSendedMessages(info.getId());

                if (receivedMessages != null) {
                    json.put("received", receivedMessages.size());
                } else {
                    json.put("received", -1);
                }
                if (sendedMessages != null) {
                    json.put("sended", sendedMessages.size());
                } else {
                    json.put("sended", -1);
                }

                String jsonData = json.toString();
                DataUtils.writeString(reply, jsonData);
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO, player.getGamePlayer().getSession(), reply);
            } else {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.NO_USER_WITH_SUCH_ID);
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO, player.getGamePlayer().getSession(), reply);
            }
        } else {
            disconnectUserWithInvaledStatus(player, Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO, "Error. User is not online, but try to get user info");
        }
    }

    private void userDisconnected(ChatPlayer player) {
        if (player.isOnline()) {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(player.getId());
            player.changeStatus(ChatPlayer.ChatPlayerStatus.OFFLINE);
            synchronized (users) {
                users.remove(player.getId());
            }
            disconnectUser(player, Results.END_GAME, Results.USER_DISCONNECT_REQUEST, null);
            notifyAll(Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT, reply);
        } else {
            disconnectUserWithInvaledStatus(player, Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT, "Error. User is not online, but try to Disconnect");
        }
    }

    private void userRequestEnter(ChatPlayer player) {
        if (player.isConnecting()) {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(player.getId());
            notifyAll(Ids.Actions.GameLogic.ChatGame.CLIENT_ENTERED_TO_CHAT, reply);

            player.changeStatus(ChatPlayer.ChatPlayerStatus.ONLINE);//notify all except player
            reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.SUCCESS);
            messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.CLIENT_REQUEST_ENTER, player.getGamePlayer().getSession(), reply);
        } else {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.INVALID_DATA);
            messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.CLIENT_REQUEST_ENTER, player.getGamePlayer().getSession(), reply);
            disconnectUserWithInvaledStatus(player, Ids.Actions.GameLogic.ChatGame.CLIENT_ENTERED_TO_CHAT, "Error. User is not connecting, but try to enter");
        }
    }

    private void notifyAll(int action, ByteBuf data) {
        for(GamePlayerAccessor p : users.values()) {
            ChatPlayer player = (ChatPlayer)p;
            if (player.isOnline()) {
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, action, player.getGamePlayer().getSession(), data);
            }
        }
    }

    private void sendOnlinePlayers(ChatPlayer toPlayer) {
        if (toPlayer.isOnline()) {
            JSONArray playersJson = new JSONArray();
            synchronized (users) {
                for (Map.Entry<Integer, GamePlayerAccessor> i : users.entrySet()) {
                    ChatPlayer player = (ChatPlayer) i.getValue();
                    if (player.isOnline()) {
                        playersJson.put(player.getGamePlayer().getInfo().getShortAsJson());
                    }
                }
            }

            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.SUCCESS);
            JSONObject json = new JSONObject();
            json.put("players", playersJson);
            json.put("total", users.size());
            String jsonString = json.toString();
            DataUtils.writeString(reply, jsonString);

            messages.send(toPlayer.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_ONLINE_CLIENTS, toPlayer.getGamePlayer().getSession(), reply);
        } else {
            disconnectUserWithInvaledStatus(toPlayer, Ids.Actions.GameLogic.ChatGame.GET_ONLINE_CLIENTS, "Error. User is not online, but try to get online players");
        }
    }

    public static final long UPDATE_TIMEOUT = 10 * 1000;
    public static final long MAX_CONNECTION_TIME = 30 * 1000;

    public long update() {
        checkPlayersTimeout();
        removeOffilePlayers();
        checkEndGameSession();
        return Calendar.getInstance().getTimeInMillis() + UPDATE_TIMEOUT;//Next update time;
    }

    private void checkPlayersTimeout() {
        long now = Calendar.getInstance().getTimeInMillis();
        long delta = MAX_CONNECTION_TIME;
        ArrayList<ChatPlayer> playersToRemove = new ArrayList<>();
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                ChatPlayer player = (ChatPlayer)p;
                if (player.isNotOnOffline()) {
                    if (now - player.getConnectTime().getTime() > delta) {
                        playersToRemove.add(player);
                    }
                }
            }

            for(ChatPlayer player : playersToRemove) {
                users.remove(player);
                disconnectUser(player, Results.END_GAME, Results.USER_TIMEOUT, JSONBuilder
                        .start("status", player.getStatus())
                        .with("message", "Check players timeout error.").get());
            }
        }
    }

    private void removeOffilePlayers(){
        long now = Calendar.getInstance().getTimeInMillis();
        long delta = MAX_CONNECTION_TIME;
        ArrayList<ChatPlayer> playersToRemove = new ArrayList<>();
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                ChatPlayer player = (ChatPlayer)p;
                if (now - player.getConnectTime().getTime() > delta) {
                    player.changeStatus(ChatPlayer.ChatPlayerStatus.OFFLINE);
                }
                playersToRemove.add(player);
            }
        }
        for(ChatPlayer player : playersToRemove) {
            users.remove(player.getId());
            disconnectUser(player, Results.ERROR, Results.USER_TIMEOUT, JSONBuilder
                    .start("status", player.getStatus())
                    .with("message", "Remove offline players.").get());
        }
    }

    private boolean checkEndGameSession(){
        return false;
    }

    @Override
    protected void onGameFinished() {
        removeOffilePlayers();
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                if (!((ChatPlayer)p).isOffline()) {
                    disconnectUser(p, Results.END_GAME, Results.END_GAME, null);
                }
            }
            users.clear();
        }

    }

    private static class ShortUserInfo {
        public int id;
        public String firstName;
        public String lastName;
    }
}