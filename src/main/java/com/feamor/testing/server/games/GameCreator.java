package com.feamor.testing.server.games;

import com.feamor.testing.server.services.GameResolver;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 24.10.2015.
 */
public abstract class GameCreator {
    protected HashMap<Integer, GamePlayer> players;
    protected AtomicInteger playersIdGenerator;
    protected GameResolver gameResolver;

    public GameCreator() {
        players = new HashMap<Integer, GamePlayer>();
        playersIdGenerator = new AtomicInteger();
    }

    public void  initialize(GameResolver resolver) {
        this.gameResolver = gameResolver;
    }

    public abstract ActiveGame createGame();

    public void addNewPlayer(GamePlayer player) {
        synchronized (players) {
            players.put(player.getId().getId(), player);
        }
    }

    public void onGameStarted(ActiveGame game) {
        if (game.isStarted()) {
            synchronized (players) {
                for (GamePlayer player : players.values()) {
                    game.addPlayer(player);
                    gameResolver.notifyGameStarted(player, game.getId());
                }
                players.clear();
            }
        } else {
            //TODO: log error
        }
    }
}
