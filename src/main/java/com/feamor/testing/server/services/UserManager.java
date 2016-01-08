package com.feamor.testing.server.services;

import com.feamor.testing.Application;
import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.DataMessage;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.NettyClient;
import com.feamor.testing.server.utils.UserInfo;
import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by feamor on 08.10.2015.
 */
@Scope(value = "singleton")
public class UserManager {

    private static final Logger log = Logger.getLogger(UserManager.class);

    public static class Authorizations {
        public static final int BY_LOGIN_AND_PASSWORD = 10;

    }

    public static class Results {
        public static final int SUCCESS = 100;
        public static final int INVALID_DATA = 110;
        public static final int INTERNAL_ERROR = 120;

        public static final int REGISTER_UNKNOWN_TYPE = 200;
        public static final int REGISTER_SUCH_USER_EXIST = 201;

        public static final int LOGIN_OR_PASSWORD_INVALID = 300;
    }

    public  static class Queues{
        public static final int NEW_CONNECTIONS = 10;
        public static final int ONLINE_PLAYERS = 15;
    }

    public static class Timeouts{
        public static final long NEW_CONNECTION = 10 * 1000;
    }

    private HashMap<Integer, QueueObject> newConnections;
    private AtomicInteger idGenerator;

    private HashMap<Integer, GamePlayer> onlinePlayers;

    private AtomicLong sessionIdGenerator;
    private Random sessionRandom;
    private Object connectionsLocker;

    @Autowired
    private PlayersDAO playersDAO;

    public UserManager() {
        newConnections = new HashMap<>();
        idGenerator = new AtomicInteger();
        onlinePlayers = new HashMap<>();
        connectionsLocker = new Object();
        sessionIdGenerator = new AtomicLong();
        sessionRandom = new Random();
    }

    public NettyClient getConnectionForId(IdType id){
        NettyClient connection = null;
        if (id != null) {
            switch (id.getType()) {
                case Queues.NEW_CONNECTIONS: {
                    synchronized (connectionsLocker) {
                        QueueObject queueObject = newConnections.get(id.getId());
                        if (queueObject != null) {
                            connection = queueObject.client;
                        }
                    }
                }
                break;
                case Queues.ONLINE_PLAYERS: {
                    synchronized (connectionsLocker) {
                        GamePlayer player = onlinePlayers.get(id.getId());
                        if (player != null) {
                            connection = player.getConnection();
                        }
                    }
                }
                break;
            }
        } else {
            log.error("id is null");
        }
        return connection;
    }

    public void addNewConnection(NettyClient client) {
        synchronized (connectionsLocker) {
            int id = idGenerator.incrementAndGet();
            newConnections.put(id, new QueueObject(id, Timeouts.NEW_CONNECTION, client));
            client.setId(id, Queues.NEW_CONNECTIONS);
        }
    }

    public int registerUser(int type, String id, String password, String other) {
        int result;
        switch(type) {
            case Authorizations.BY_LOGIN_AND_PASSWORD:
                result =  playersDAO.registerUserWithLoginAnsPassword(id, password);
                break;
            default:
                result = Results.REGISTER_UNKNOWN_TYPE;
                break;
        }
        return result;
    }

    public Map.Entry<Integer, UserInfo> loginUser(int type, String id, String password, String other){
        Map.Entry<Integer, UserInfo> result;
        switch (type) {
            case Authorizations.BY_LOGIN_AND_PASSWORD:
                result = playersDAO.loginUserWithLoginAndPassword(id, password);
                break;
            default:
                result = new HashMap.SimpleEntry<Integer, UserInfo>(Results.REGISTER_UNKNOWN_TYPE, null);
                break;
        }
        return result;
    }

    public String generateSession(GamePlayer player) {
        String result = sessionIdGenerator.incrementAndGet()+ "-" + player.getId().getId() + "-" + sessionRandom.nextLong();
        return  result;
    }

    public GamePlayer createGamePlayer(UserInfo userInfo, IdType id) {
        GamePlayer player = null;

        if (userInfo == null || id == null) {
            log.error("CreateGamePlayer : id  or userInfo is null");
        } else {
            if (id.getType() == Queues.NEW_CONNECTIONS) {
                QueueObject newConnection;
                synchronized (connectionsLocker) {
                    if (!onlinePlayers.containsKey(id.getId())) {
                        newConnection = newConnections.remove(id.getId());
                        if (newConnection != null) {
                            id.setType(Queues.ONLINE_PLAYERS);
                            player = new GamePlayer();
                            player.setInfo(userInfo);
                            player.setConnection(newConnection.client);
                            player.setSession(generateSession(player));
                            onlinePlayers.put(id.getId(), player);
                        } else {
                            log.error("There is no player in newConnections and onlinePlayers, id : " + id + ", userInfo : " + userInfo);
                        }
                    } else {
                        log.error("Player already online, player-resume-connection now is not supported! id : " + id + ", userInfo : " + userInfo);
                    }
                }
            } else {
                log.error("Player stete is - ONLINE player-resume-connection now is not supported! id : " + id + ", userInfo : " + userInfo);
            }
        }
        return player;
    }

    public GamePlayer getPlayer(IdType id, String session) {
        GamePlayer result = null;
        if (id != null && session != null) {
            switch (id.getType()) {
                case Queues.ONLINE_PLAYERS:
                    result = onlinePlayers.get(id.getId());
                    if (result != null) {
                        if (!session.equals(result.getSession())) {
                            result = null;
                        }
                    }
                    break;
            }
        }
        return result;
    }

    private static class QueueObject{
        public int id;
        public long time;
        public long delta;
        public NettyClient client;

        public QueueObject(int id, long delta, NettyClient client) {
            this.id = id;
            this.time = Calendar.getInstance().getTimeInMillis();
            this.delta = delta;
            this.client = client;
        }
    }
}
