package com.feamor.testing.server.services;

import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.Ids;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 19.10.2015.
 */
public class GameReslover {

    public static abstract class ActiveGame{
        protected HashMap<Integer, GamePlayer> users;
        protected int id;

        public ActiveGame(int id){
            this.id = id;
        }

        public void initialize() {

        }

        public abstract void onMessage(int action, GamePlayer player, ByteBuf data);

        public abstract void startGame();

        public abstract boolean isFinished();

        public abstract void endGame();

        public void finalize(){

        }
    }

    public static class GamePlayer {
        private int gameId;
        private IdType connection;
        private PlayersDAO.UserInfo info;

        public int getGameId() {
            return gameId;
        }

        public void setGameId(int gameId) {
            this.gameId = gameId;
        }

        public IdType getConnection() {
            return connection;
        }

        public void setConnection(IdType connection) {
            this.connection = connection;
        }

        public PlayersDAO.UserInfo getInfo() {
            return info;
        }

        public void setInfo(PlayersDAO.UserInfo info) {
            this.info = info;
        }
    }


    public static abstract class GameCreator {
        protected HashMap<Integer, GamePlayer> players;
        protected AtomicInteger playersIdGenerator;

        public GameCreator() {
            players = new HashMap<Integer, GamePlayer>();
            playersIdGenerator = new AtomicInteger();
        }

        public abstract ActiveGame createGame();

        public GamePlayer addNewPlayer(IdType connectionId, PlayersDAO.UserInfo userInfo) {
            GamePlayer player = null;
            int id = playersIdGenerator.incrementAndGet();
            player = new GamePlayer();
            player.setGameId(id);
            player.setConnection(connectionId);
            player.setInfo(userInfo);
            players.put(id, player);
            return player;
        }
    }

    public static class  ChatGameCreator extends  GameCreator{

        protected ActiveGame chatGame = null;

        @Override
        public GamePlayer addNewPlayer(IdType connectionId, PlayersDAO.UserInfo userInfo) {
            GamePlayer player = super.addNewPlayer(connectionId, userInfo);
            if (player!=null) {
                if (chatGame == null) {

                }
            }
            return player;
        }



        @Override
        public ActiveGame createGame() {
            if (chatGame == null) {
                chatGame = new ChatGame();
            }
            return chatGame;
        }
    }

    public static class ChatGame extends ActiveGame {
        public static class Actions {
            public static final int NEW_PLAYER = 100;
        }

    }

    public static  class Actions {
        public static final int GET_AVALABLE_GAMES = 100;
        public static final int REQUEST_GAME = 200;
        public static final int CANCEL_GAME_REQUEST = 210;

        public static final int OPEN_GAME_REQUEST = 300;
    }

    @Autowired
    private Messages messages;

    @ServiceActivator
    public  void onClientMessage(int action, IdType connection, String session, ByteBuf data){
        checkUserAndSession();
        switch(action) {
            case Actions.GET_AVALABLE_GAMES:
                ByteBuf avalableGames = writeAvalableGames(userInfo);
                messages.send(connection, Ids.Services.GAME_RESLOVER, action, session, avalableGames);
                break;
            case Actions.REQUEST_GAME:
                addGameRequest(connection, session, data);
                break;
        }
    }

    public ByteBuf writeAvalableGames(PlayersDAO.UserInfo userInfo) {
        int level = (int) userInfo.gameStatistics.get("level");
        ArrayList<PlayersDAO.GameDescription> games = getGamesWithLevel(level);
        write games;
    }

    public void addGameRequest(IdType connection, String sessio, ByteBuf data) {
        int gameDescriptionId = data.readInt();
        GameCreator gc = getOrCreateGameCreator(gameDescriptionId);
        gc.addNewPlayer(connection, userInfo);
    }


}
