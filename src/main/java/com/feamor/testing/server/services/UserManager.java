package com.feamor.testing.server.services;

import com.feamor.testing.PlayersDAO;
import com.feamor.testing.server.utils.NettyClient;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 08.10.2015.
 */
public class UserManager {

    public  static class Queues{
        public static final int NEW_CONNECTIONS = 10;
    }

    private IntObjectHashMap<Object> newConnections;
    private AtomicInteger newConnectionsGenerator;
    private Object newConnectionsLocker;

    public int addNewConnection(NettyClient client) {
        int id;
        synchronized (newConnectionsLocker) {
            id = newConnectionsGenerator.incrementAndGet();
            newConnections.put(id, newConnections);
        }
        return id;
    }

    public int registerUser()
}
