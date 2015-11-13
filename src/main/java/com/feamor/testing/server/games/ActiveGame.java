package com.feamor.testing.server.games;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

/**
 * Created by feamor on 24.10.2015.
 */
public abstract class ActiveGame{

    public static class GameStates{
        public static final int NONE = 0;
        public static final int INITIALIZATION = 5;
        public static final int INITIALIZED = 6;
        public static final int STARTING = 10;
        public static final int STARTED = 15;

        public static final int FINISHING = 20;
        public static final int STOPPED = 25;
        public static final int ERROR = -1;
    }

    protected HashMap<Integer, GamePlayer> users;
    protected int id;
    protected int state;

    public ActiveGame()
    {
        state = GameStates.NONE;
        users = new HashMap<Integer, GamePlayer>();
    }

    public void initialize(GameCreator creator) {
        state = GameStates.INITIALIZATION;
        try {
            onCreate(creator);
            state = GameStates.INITIALIZED;
        } catch(Exception ex) {
            //TODO: add logiging of error
            ex.printStackTrace();
            state = GameStates.ERROR;
        }
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    protected abstract void onCreate(GameCreator creator);
    protected abstract void onStarted();
    protected abstract void onMessage(int action, GamePlayer player, ByteBuf data);
    protected abstract void onNewPlayer(GamePlayer player, int playerId);
    protected abstract void onGameFinished();

    public void addPlayer(GamePlayer player) {
        int playerId = player.getId().getId();
        users.put(playerId, player);
        onNewPlayer(player, playerId);
    }

    public void startGame(int id){
        state = GameStates.STARTING;
        try {
            onStarted();
            state = GameStates.STARTED;
        } catch(Exception ex) {
            //TODO: add logiging of error
            ex.printStackTrace();
            state = GameStates.ERROR;
        }
    }

    public  void endGame() {
        state = GameStates.FINISHING;
        try {
            onGameFinished();
            state = GameStates.STOPPED;
        } catch(Exception ex) {
            //TODO: add logiging of error
            ex.printStackTrace();
            state = GameStates.ERROR;
        }
        users.clear();
    }

    public boolean isStarted() {
        boolean result = state == GameStates.STARTED;
        return  result;
    }

    public boolean isError() {
        return state == GameStates.ERROR;
    }
}
