package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;

import java.util.Calendar;

/**
 * Created by feamor on 08.10.2015.
 */
public class NettyClient {

    private IdType id;
    private SocketChannel socket;
    private long heartBeatTime;

    public NettyClient(SocketChannel socketChannel) {
        this.socket = socketChannel;
        heartBeatTime = Calendar.getInstance().getTimeInMillis();
    }

    public void heartBeat() {
        heartBeatTime = Calendar.getInstance().getTimeInMillis();
    }

    public long getHeartBeat() {
        return  heartBeatTime;
    }

    public void sendMessage(DataMessage message) {
        socket.writeAndFlush(message);
    }

    public void sendHeartBeat() {
        DataMessage message = new DataMessage(Ids.Services.CLIENTS, Ids.Actions.HEART_BEAT_ACTION, null, null);
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

    public void disconnect() {
        if (socket != null && socket.isOpen()) {
            socket.close();
        }
    }
}
