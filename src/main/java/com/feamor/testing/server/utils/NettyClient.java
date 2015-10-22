package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by feamor on 08.10.2015.
 */
public class NettyClient {

    private IdType id;
    private SocketChannel socket;

    public NettyClient(SocketChannel socketChannel) {
        this.socket = socketChannel;
    }

    public void sendMessage(DataMessage message) {
        socket.writeAndFlush(message);
    }


    public IdType getId() {
        return id;
    }


    public void setId(IdType id) {
        this.id = id;
    }


    public void setId(int id, int type) {
        if (this.id == null) {
            this.id = new IdType(id, type);
        } else {
            this.id.set(id, type);
        }
    }
}
