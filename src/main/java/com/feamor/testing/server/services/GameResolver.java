package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 19.10.2015.
 */
public class GameResolver {

    public static class GameCategory {
        public int id;
        public String name;
        public String descriptino;
    }

    public static class GameDescription{
        public int id;
        public String alias;

        public String name;
        public String description;
        public String iconUri;

        public ArrayList<Integer> gameCategories = new ArrayList<>();

        public UserInventoryItem asInventory() {
            UserInventoryItem inventory = new UserInventoryItem();
            inventory.count = 1;
            inventory.description = description;
            inventory.name = name;
            inventory.imageUri = iconUri;
            inventory.descriptionId = id;
            inventory.type = UserInventoryItem.Types.AVALABLE_GAME;
            return  inventory;
        }

    }


    @Autowired
    @Qualifier(Config.Executors.UTILITY)
    private ThreadPoolTaskExecutor utilityExecutor;

    private HashMap<Integer, ActiveGame> activeGames;
    private AtomicInteger gameIdGenerator = new AtomicInteger();

    private HashMap<Integer, GameCategory> gameCatogories = new HashMap<>();
    public void addGameCategory(GameCategory gameCategory) {
        gameCatogories.put(gameCategory.id, gameCategory);
    }

    private HashMap<Integer, GameDescription> gameDescriptions = new HashMap<>();

    public void addGameDescription(GameDescription description) {
        gameDescriptions.put(description.id, description);
    }

    private HashMap<String, GameCreator> creators = new HashMap<>();
    private HashMap<String, Class> creatorLibrary = new HashMap<>();

    public void addCreatorClass(String alias, Class clazz) {
        creatorLibrary.put(alias, clazz);
    }

    public GameDescription getGameDescription(int id) {
        GameDescription result = gameDescriptions.get(id);
        return  result;
    }

    public GameCreator getOrCreateGameCreator(String alias) {
        GameCreator result = null;
        synchronized (creators) {
            result = creators.get(alias);
            if (result == null) {
                Class clazz = creatorLibrary.get(alias);
                if (clazz != null) {
                    try {
                        result = (GameCreator) clazz.newInstance();
                        result.initialize(this);
                        creators.put(alias, result);
                    } catch (Exception ex) {
                        //TODO: add log error
                        ex.printStackTrace();
                        result = null;
                    }
                }
            }
        }
        return  result;
    }

    public void prepareGame(ActiveGame game, GameCreator creator) {
        utilityExecutor.submit(new RunnableWithParams<HashMap.Entry<ActiveGame, GameCreator>>(new HashMap.SimpleEntry<ActiveGame, GameCreator>(game, creator)) {
            @Override
            public void run() {
                ActiveGame gameToPrepare = param.getKey();
                GameCreator callback = param.getValue();
                gameToPrepare.initialize(callback);
                if (gameToPrepare.isError()) {
                    callback.onGameStarted(gameToPrepare);
                } else {
                    int id = gameIdGenerator.incrementAndGet();
                    gameToPrepare.startGame(id);
                    if (!gameToPrepare.isError()) {
                        activeGames.put(id, gameToPrepare);
                    }
                    callback.onGameStarted(gameToPrepare);
                }
            }
        });
    }

    public static class UserInventoryItem {

        public static class Types{
            public static final int AVALABLE_GAME = 10;
        }

        public int type;
        public long itemId;
        public int descriptionId;
        public int count;
        public String name;
        public String description;
        public String imageUri;

        public void writeToBuffer(ByteBuf buffer) {
            buffer.writeInt(type);
            buffer.writeLong(itemId);
            buffer.writeInt(descriptionId);
            buffer.writeInt(count);
            DataUtils.writeString(buffer, name);
            DataUtils.writeString(buffer, description);
            DataUtils.writeString(buffer, imageUri);
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("type", type);
            json.put("itemId", itemId);
            json.put("descriptionId", descriptionId);
            json.put("count", count);
            json.put("name", name);
            json.put("description", description);
            json.put("imageUri", imageUri);
            return json;
        }
    }

    public ArrayList<UserInventoryItem> getUserInventory(UserInfo userInfo) {
        ArrayList<UserInventoryItem> inventory = new ArrayList<>();
        UserInventoryItem item = null;
        if (userInfo.avalableGames != null) {
            for(Map.Entry<Long, Integer> game : userInfo.avalableGames.entrySet()) {
                GameResolver.GameDescription description = getGameDescription(game.getValue());
                if (description != null) {
                    item = description.asInventory();
                    item.itemId = game.getKey();
                    inventory.add(item);
                }
            }
        }

        return  inventory;
    }

    @Autowired
    private Messages messages;

    @Autowired
    private  UserManager userManager;

    public void notifyGameStarted(GamePlayer player, int gameId) {
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(gameId);
        player.setSession(userManager.generateSession(player));
        messages.send(player.getId(), Ids.Services.GAME_RESLOVER, Ids.Actions.GameResolver.GAME_STARTED, player.getSession(), reply);
    }
}
