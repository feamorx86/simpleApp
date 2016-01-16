package com.feamor.testing.server.games;

import com.feamor.testing.server.services.GameResolver;
import com.feamor.testing.server.services.Messages;
import com.feamor.testing.server.utils.DataUtils;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.Ids;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.beans.factory.annotation.Autowired;

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

    public interface GamePlayerAccessor {
        GamePlayer getGamePlayer();
    }

    protected HashMap<Integer, GamePlayerAccessor> users;
    protected int id;
    protected int state;

    @Autowired
    protected Messages messages;

    @Autowired
    protected GameResolver gameResolver;


    public ActiveGame()
    {
        state = GameStates.NONE;
        users = new HashMap<Integer, GamePlayerAccessor>();
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
    protected abstract void onMessage(int action, GamePlayerAccessor player, ByteBuf data);
    protected abstract void onNewPlayer(GamePlayerAccessor player);
    public void onPlayerDisconnected(GamePlayerAccessor player) {
        synchronized (users) {
            users.remove(player.getGamePlayer().getId().getId());
        }
    }
    protected abstract void onGameFinished();

    public void addPlayer(GamePlayer player) {
        int playerId = player.getId().getId();
        GamePlayerAccessor accessor = createPlayer(player);
        synchronized (users)
        {
            users.put(playerId, accessor);
        }
        onNewPlayer(accessor);
    }

    public void handleMessage(int action, GamePlayer gamePlayer, ByteBuf data){
        onMessage(action, getPlayer(gamePlayer), data);
    }

    public GamePlayerAccessor getPlayer(GamePlayer gamePlayer) {
        synchronized (users) {
            return users.get(gamePlayer.getId().getId());
        }
    }

    protected abstract GamePlayerAccessor createPlayer(GamePlayer gamePlayer);

    public void startGame(int id){
        this.id = id;
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

    public void disconnectUser(GamePlayerAccessor user, int result, int code, Object reason) {
        ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer();
        reply.writeInt(getId());
        reply.writeInt(result);
        reply.writeInt(code);
        if (reason != null) {
            DataUtils.writeString(reply, reason.toString());
        } else {
            DataUtils.writeString(reply, null);
        }
        messages.send(user.getGamePlayer().getId(), Ids.Services.GAMES, Ids.Actions.GameResolver.GAME_FINISHED, user.getGamePlayer().getSession(), reply);
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
        synchronized (users) {
            users.clear();
        }
    }

    public boolean isStarted() {
        boolean result = state == GameStates.STARTED;
        return  result;
    }

    public boolean isError() {
        return state == GameStates.ERROR;
    }
}
