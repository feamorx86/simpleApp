package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.DataMessage;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.NettyClient;
import com.feamor.testing.server.utils.UserInfo;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;
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
@MessageEndpoint
public class UserManager {

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

    private HashMap<Integer, QueueObject> newConnections = new HashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger();
    private Object newConnectionsLocker = new Object();

    private HashMap<Integer, GamePlayer> onlinePlayers = new HashMap<>();
    private Object onlinePlayersLocker = new Object();

    private AtomicLong sessionIdGenerator = new AtomicLong();
    private Random sessionRandom = new Random();

    @Autowired
    private PlayersDAO playersDAO;

    @ServiceActivator(inputChannel = Config.Channels.SEND_MESSAGES, poller = @Poller(fixedRate = "300", taskExecutor = Config.Executors.UTILITY, maxMessagesPerPoll = "100"))
    public void sendMessage(@Header(Messages.ID_HEADER) IdType id,
                            @Header(Messages.SERVICE_HEADER)int service,
                            @Header(Messages.ACTION_HEADER) int action,
                            @Header(value = Messages.SESSION_HEADER, required = false) String session,
                            @Payload(required = false) ByteBuf data) {
        if (id != null) {
            NettyClient connection = null;
            switch (id.getType())
            {
                case Queues.NEW_CONNECTIONS:{
                    QueueObject queueObject = newConnections.get(id.getId());
                    if (queueObject != null) {
                        connection = queueObject.client;
                    }
                }
                break;
                case Queues.ONLINE_PLAYERS: {
                    GamePlayer player = onlinePlayers.get(id.getId());
                    if (player!=null) {
                        connection = player.getConnection();
                    }
                }
                break;
            }

            if (connection!=null) {
                DataMessage message = new DataMessage(service, action, session, data);
                message.retain();
                connection.sendMessage(message);
            } else {
                //TODO: log error
            }

        } else {
            //TODO: log error
        }
    }

    public void addNewConnection(NettyClient client) {
        synchronized (newConnectionsLocker) {
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
        if (userInfo != null && id != null && !onlinePlayers.containsKey(id.getId())) {
            //remove data from NewConnections
            QueueObject newConnection;
            newConnection = newConnections.remove(id.getId());
            if (newConnection !=null) {
                id.setType(Queues.ONLINE_PLAYERS);
                player = new GamePlayer();
                player.setInfo(userInfo);
                player.setConnection(newConnection.client);
                player.setSession(generateSession(player));
                onlinePlayers.put(id.getId(), player);
            } else {
                //TODO: Log error
            }
        } else {
            //TODO: log error
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
