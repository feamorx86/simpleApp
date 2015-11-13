package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.DataUtils;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.Ids;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.ArrayList;

/**
 * Created by feamor on 25.10.2015.
 */
@MessageEndpoint
public class GameController {


    @Autowired
    private UserManager userManager;

    @Autowired
    private GameResolver gameResolver;

    @Autowired
    private Messages messages;

    @ServiceActivator(poller = @Poller(taskExecutor = Config.Executors.GAME_LOGIC, fixedRate = "500", maxMessagesPerPoll = "100"), inputChannel = Config.Channels.GAME_MESSAGES)
    public void onNewMessage(@Header(name = Messages.SERVICE_HEADER) int service,
                             @Header(name = Messages.ACTION_HEADER)int action,
                             @Header(name = Messages.SESSION_HEADER, required = false)String session,
                             @Header(name = Messages.ID_HEADER)IdType id,
                             @Payload(required = false) ByteBuf data) {
        GamePlayer player = userManager.getPlayer(id, session);
        switch(action) {
            case Ids.Actions.GameResolver.GET_FULL_USER_INFO:
            {
                if (player!=null) {
                    JSONObject json = getFullUserInfoAsJson(player);
                    sendJson(json, player, Ids.Actions.GameResolver.GET_FULL_USER_INFO);
                }
            }
            break;
            case Ids.Actions.GameResolver.GET_USER_INVENTORY:
            {
                if (player!=null) {
                    JSONObject json = getUserInventoryAsJson(player);
                    sendJson(json, player, Ids.Actions.GameResolver.GET_USER_INVENTORY);
                } else {
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.INVALID_SESSION);
                    messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.GET_USER_INVENTORY, player.getSession(), reply);
                }
            }
            break;
            case Ids.Actions.GameResolver.START_GAME_REQUEST:
                if (player!=null) {
                    long gameId = data.readLong();
                    addGameRequest(player, gameId, data);
                } else {
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.INVALID_SESSION);
                    messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.START_GAME_REQUEST, player.getSession(), reply);
                }
                break;
        }
    }

    public static class Results {
        public static final int SUCCESS = 0;
        public static final int INVALID_SESSION = 10;
        public static final int INVALID_DATA = 20;
        public static final int INTERNAL_ERROR = 15;


        public static final int GAME_IS_UNAVALABLE_NOW = 50;
    }

    private void addGameRequest(GamePlayer player, long gameId, ByteBuf data) {
        Integer descriptionId = player.getInfo().avalableGames.get(gameId);
        if (descriptionId != null) {
            GameResolver.GameDescription description = gameResolver.getGameDescription(descriptionId);
            if (description != null) {
                GameCreator creator = gameResolver.getOrCreateGameCreator(description.alias);
                if (creator != null) {
                    creator.addNewPlayer(player);
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.SUCCESS);
                    messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.START_GAME_REQUEST, player.getSession(), reply);
                } else {
                    ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                    reply.writeInt(Results.GAME_IS_UNAVALABLE_NOW);
                    messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.START_GAME_REQUEST, player.getSession(), reply);
                }
            } else {
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
                reply.writeInt(Results.GAME_IS_UNAVALABLE_NOW);
                messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.START_GAME_REQUEST, player.getSession(), reply);
            }
        } else {
            //TODO: Logerror
            ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
            reply.writeInt(Results.INVALID_DATA);
            messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.START_GAME_REQUEST, player.getSession(), reply);
        }
    }

    private void sendJson(JSONObject json, GamePlayer player, int action) {
        String jsonData = json.toString();
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(Results.SUCCESS);
        DataUtils.writeString(reply, jsonData);
        messages.send(player.getId(), Ids.Services.GAME_RESLOVER, action, player.getSession(), reply);
    }

    private JSONObject getFullUserInfoAsJson(GamePlayer player) {
        JSONObject json = new JSONObject();
        JSONObject userInfo = new JSONObject();
        userInfo.put("firstName", player.getInfo().firstName);
        userInfo.put("secondName", player.getInfo().secondName);
        userInfo.put("icon", player.getInfo().iconUri);

        json.put("userInfo", userInfo);
        json.put("inventory", getUserInventoryAsJson(player));

        //TODO: create user statistics and send!
        //json.put("statistics", getUserStatisticsAsJson(player))
        return json;
    }

    private JSONObject  getUserInventoryAsJson(GamePlayer player) {
        ArrayList<GameResolver.UserInventoryItem> inventory = gameResolver.getUserInventory(player.getInfo());
        JSONObject json = new JSONObject();
        JSONArray inventoryItemsJson = new JSONArray();
        if (inventory!=null) {
            for(GameResolver.UserInventoryItem item : inventory) {
                inventoryItemsJson.put(item.toJson());
            }
        }
        json.put("items", inventoryItemsJson);
        return json;
    }
}
