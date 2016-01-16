package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;

/**
 * Created by feamor on 08.10.2015.
 */
public class DataMessage {
    private int service;
    private int action;
    private String session;
    private ByteBuf data;

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    public void retain() {
        if (data!=null) {
            data.retain();
        }
    }
//TODO: add ability to recycle all messages!!!
    public void recycle() {
        if (data!=null) {
            data.release();
        }
    }

    public DataMessage() {
        
    }
    public DataMessage(int service, int action, String session, ByteBuf data) {
        this.service = service;
        this.action = action;
        this.session = session;
        this.data = data;
    }
}
