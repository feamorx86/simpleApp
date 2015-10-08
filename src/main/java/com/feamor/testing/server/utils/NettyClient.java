package com.feamor.testing.server.utils;

import com.feamor.testing.server.utils.BaseConnection;
import com.feamor.testing.server.utils.DataMessage;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by feamor on 08.10.2015.
 */
public class NettyClient implements BaseConnection {

    private int id;
    private int idType;

    @Override
    public void sendMessage(DataMessage message) {

    }

    @Override
    public int getIdType() {
        return  idType;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setIdType(int type) {
        this.idType = type;
    }
}
