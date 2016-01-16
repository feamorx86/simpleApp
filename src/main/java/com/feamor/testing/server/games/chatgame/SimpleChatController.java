package com.feamor.testing.server.games.chatgame;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 15.12.2015.
 */
public class SimpleChatController extends ActiveGame {

    private static final Logger log = Logger.getLogger(SimpleChatController.class);

    public static class Results {
        public static final int NO_USER_WITH_SUCH_ID = Ids.SystemResults.SYSTEM_RESULTS_END + 3;
        public static final int INVALID_PLAYER_STATUS = Ids.SystemResults.SYSTEM_RESULTS_END + 4;
        public static final int USER_TIMEOUT = Ids.SystemResults.SYSTEM_RESULTS_END + 5;
        public static final int UNSUPPORTED_COMMAND = Ids.SystemResults.SYSTEM_RESULTS_END + 10;
        public static final int ERROR = Ids.SystemResults.SYSTEM_RESULTS_END + 30;
        public static final int END_GAME = Ids.SystemResults.SYSTEM_RESULTS_END + 40;
        public static final int USER_DISCONNECT_REQUEST = Ids.SystemResults.SYSTEM_RESULTS_END + 41;
    }

    @Override
    protected void onCreate(GameCreator creator) {

    }

    @Override
    public void onPlayerDisconnected(GamePlayerAccessor player) {
        super.onPlayerDisconnected(player);
        userExits(player);
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
            case Ids.Actions.GameLogic.SimpleChat.NEW_USER:
                userEntered((ChatPlayer) player);
                break;
            case Ids.Actions.GameLogic.SimpleChat.USER_EXIT:
                userExits((ChatPlayer) player);
                disconnectUser(player, Results.END_GAME, 0, null);
                break;
            case Ids.Actions.GameLogic.SimpleChat.REQUEST_USERS_LIST: {
                int callback = data.readInt();
                sendPlayersList((ChatPlayer) player, callback);
            }
                break;
            case Ids.Actions.GameLogic.SimpleChat.SEND_MESSAGE: {
                int receiver = data.readInt();
                int callback = data.readInt();
                String message = DataUtils.readString(data);
                sendMessages((ChatPlayer) player, receiver, message, callback);
            }
            break;
            case Ids.Actions.GameLogic.SimpleChat.REQUEST_USER_INFO:
                int requestedUserId = data.readInt();
                int callback = data.readInt();
                sendUserInfo(player, requestedUserId, callback);
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

    private void sendUserInfo(GamePlayerAccessor player, int requestedUserId, int callback) {
        GamePlayerAccessor accessor = null;
        synchronized (users) {
            accessor = users.get(requestedUserId);
        }
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(callback);
        if (accessor != null) {
            JSONObject userInfoJson = accessor.getGamePlayer().getInfo().getShortAsJson();
            reply.writeInt(Ids.SystemResults.SUCCESS);
            reply.writeInt(accessor.getGamePlayer().getInfo().id);
            DataUtils.writeString(reply, userInfoJson.toString());
        } else {
            reply.writeInt(Results.NO_USER_WITH_SUCH_ID);
            reply.writeInt(requestedUserId);
        }
        messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.SimpleChat.REQUEST_USER_INFO, player.getGamePlayer().getSession(), reply);
    }

    private void sendPlayersList(ChatPlayer player, int callback) {
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(callback);
        JSONObject json = new JSONObject();
        JSONArray usersList = new JSONArray();
        synchronized (users) {
            for (GamePlayerAccessor accessor : users.values()) {
                if (accessor != player) {
                    usersList.put(((ChatPlayer) accessor).getShortInfo());
                }
            }
        }
        json.put("users", usersList);
        DataUtils.writeString(reply, json.toString());
        messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.SimpleChat.LIST_USERS, player.getGamePlayer().getSession(), reply);
    }

    private void userExits(GamePlayerAccessor player) {
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(player.getGamePlayer().getId().getId());
        notifyAll(Ids.Actions.GameLogic.SimpleChat.USER_EXIT, reply);
    }

    private void userEntered(ChatPlayer player) {
        player.changeStatus(ChatPlayer.ChatPlayerStatus.ONLINE);
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        JSONObject json = player.getShortInfo();
        DataUtils.writeString(reply, json.toString());
        notifyAll(Ids.Actions.GameLogic.SimpleChat.NEW_USER, reply);
    }

    private void sendMessages(ChatPlayer player, int receiverId, String message, int callback) {
        ChatPlayer sendTo = null;
        synchronized (users) {
            sendTo = (ChatPlayer) users.get(receiverId);
        }
        if (sendTo != null) {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(player.getId());
            DataUtils.writeString(reply, message);
            messages.send(sendTo.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.SimpleChat.RECEIVE_MESSAGE, sendTo.getGamePlayer().getSession(), reply);

            reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Ids.SystemResults.SUCCESS);
            reply.writeInt(callback);

            messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.SimpleChat.SEND_MESSAGE_RESULT, player.getGamePlayer().getSession(), reply);
        } else {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.UNSUPPORTED_COMMAND);
            reply.writeInt(callback);
            messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getGamePlayer().getSession(), reply);
        }
    }

    private void notifyAll(int action, ByteBuf data, int exceptPlayer) {
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                ChatPlayer player = (ChatPlayer) p;
                if (player.getId() != exceptPlayer) {
                    messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, action, player.getGamePlayer().getSession(), data.copy());
                }
                data.release();
            }
        }
    }

    private void notifyAll(int action, ByteBuf data) {
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                ChatPlayer player = (ChatPlayer) p;
                messages.send(player.getGamePlayer().getId(), Ids.Services.GAMES, action, player.getGamePlayer().getSession(), data.copy());
            }
        }
        data.release();
    }

    @Override
    protected void onGameFinished() {
//        if (updateUsersTaskFuture != null && !updateUsersTaskFuture.isCancelled()) {
//            updateUsersTaskFuture.cancel(false);
//        }
        synchronized (users) {
            for (GamePlayerAccessor p : users.values()) {
                disconnectUser(p, Results.END_GAME, Results.END_GAME, null);
            }
            users.clear();
        }

    }
}
