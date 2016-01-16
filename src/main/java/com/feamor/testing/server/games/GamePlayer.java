package com.feamor.testing.server.games;

import com.feamor.testing.server.services.PlayersDAO;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.NettyClient;
import com.feamor.testing.server.utils.UserInfo;

/**
 * Created by feamor on 24.10.2015.
 */
public class GamePlayer {
    private NettyClient connection;
    private UserInfo info;
    private String session;
    private long heartBaetTime;

    public IdType getId() {
        return connection.getId();
    }

    public NettyClient getConnection() {
        return connection;
    }

    public void setConnection(NettyClient connection) {
        this.connection = connection;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }
}