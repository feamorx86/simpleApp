package com.feamor.testing.server.games;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.feamor.testing.server.services.GameResolver;
import com.feamor.testing.server.services.Messages;
import com.feamor.testing.server.utils.DataUtils;
import com.feamor.testing.server.utils.Ids;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by feamor on 24.10.2015.
 */
public class ChatGame extends ActiveGame {

    public static class Results {
        public static final int SUCCESS = 1;
        public static final int INVALID_DATA = 2;
        public static final int NO_USER_WITH_SUCH_ID = 3;
        public static final int UNSUPPORTED_COMMAND = 10;
    }

    @Autowired
    private Messages messages;

    @Autowired
    private GameResolver gameResolver;

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
    protected void onMessage(int action, GamePlayer player, ByteBuf data) {
        switch(action) {
            case Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT:
                userDisconnected(player, data);
                break;
            case Ids.Actions.GameLogic.ChatGame.GET_ONLINE_CLIENTS:
                sendOnlinePlayers(player);
                break;
            case Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO:
                getFullUserInfo(player, data);
                break;
            case Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT:
                sendMessages(player, data);
                break;
            default:
            {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.UNSUPPORTED_COMMAND);
                messages.send(player.getId(), Ids.Services.GAMES, action, player.getSession(), reply);
            }
                break;
        }
    }

    private void sendMessages(GamePlayer player, ByteBuf data) {
        int senderId = data.readInt();
        int sendToId = data.readInt();
        String message = DataUtils.readString(data);

        if (senderId != player.getId().getId()) {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.INVALID_DATA);
            messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getSession(), reply);
        } else {
            GamePlayer sendTo = users.get(sendToId);
            if (sendTo != null) {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(senderId);
                DataUtils.writeString(reply, message);
                messages.send(sendTo.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.RECEIVE_MESSAGE_FROM_CLIENT, sendTo.getSession(), reply);

                reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.SUCCESS);
                messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getSession(), reply);
            } else {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.NO_USER_WITH_SUCH_ID);
                messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.SEND_MESSAGE_TO_CLIENT, player.getSession(), reply);
            }
        }
    }

    private void getFullUserInfo(GamePlayer player, ByteBuf data) {
        int playerId = data.readInt();
        GamePlayer info =  users.get(playerId);
        if (info != null){
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.SUCCESS);

            JSONObject json = new JSONObject();
            json.put("firstname", info.getInfo().firstName);
            json.put("secondName", info.getInfo().secondName);
            json.put("iconUri", info.getInfo().iconUri);
            json.put("email", info.getInfo().email);
            String jsonData = json.toString();
            DataUtils.writeString(reply, jsonData);
            messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO, player.getSession(), reply);
        } else {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.INVALID_DATA);
            messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_USER_IFNO, player.getSession(), reply);
        }
    }

    private void userDisconnected(GamePlayer player, ByteBuf data) {
        int id = data.readInt();
        if(id != player.getId().getId()) {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.INVALID_DATA);
            messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT, player.getSession(), reply);
        } else {
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(id);
            users.remove(id);
            notifyAll(Ids.Actions.GameLogic.ChatGame.CLIENT_EXIT_FROM_CHAT, reply);
            reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.SUCCESS);
            //TODO: notify game resolver - game end!!!
        }
    }

    private void notifyAll(int action, ByteBuf data) {
        for(GamePlayer player : users.values()) {
            messages.send(player.getId(), Ids.Services.GAMES, action, player.getSession(), data);
        }
    }

    private void notifyAllExpect(GamePlayer expectPlayer, int action, ByteBuf data) {
        for(GamePlayer player : users.values()) {
            if (!player.getId().falstEquals(expectPlayer.getId())) {
                messages.send(player.getId(), Ids.Services.GAMES, action, player.getSession(), data);
            }
        }
    }

    private void sendOnlinePlayers(GamePlayer player) {
        ArrayList<ShortUserInfo> players = new ArrayList<>();
        synchronized (users) {
            for (Map.Entry<Integer, GamePlayer> i : users.entrySet()) {
                if (!player.getId().falstEquals(i.getValue().getId())) {
                    ShortUserInfo u = new ShortUserInfo();
                    u.firstName = i.getValue().getInfo().firstName;
                    u.lastName = i.getValue().getInfo().secondName;
                    u.id = i.getKey();
                    players.add(u);
                }
            }
        }
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(players.size());
        for(ShortUserInfo p : players) {
            reply.writeInt(p.id);
            DataUtils.writeString(reply, p.firstName);
            DataUtils.writeString(reply, p.lastName);
        }
        messages.send(player.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.GET_ONLINE_CLIENTS, player.getSession(), reply);
    }

    @Override
    protected void onNewPlayer(GamePlayer player, int playerId) {
        sendOnlinePlayers(player);

        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(playerId);
        DataUtils.writeString(reply, player.getInfo().firstName);
        DataUtils.writeString(reply, player.getInfo().secondName);
        notifyAllExpect(player, Ids.Actions.GameLogic.ChatGame.CLIENT_ENTERED_TO_CHAT, reply);
    }

    @Override
    protected void onGameFinished() {
        synchronized (users) {
            for (GamePlayer p : users.values()) {
                messages.send(p.getId(), Ids.Services.GAMES, Ids.Actions.GameLogic.ChatGame.YOU_DISCONNECTED, p.getSession(), null);
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