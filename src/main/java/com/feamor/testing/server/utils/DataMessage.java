package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;

/**
 * Created by feamor on 08.10.2015.
 */
public class DataMessage {
    private short service;
    private short action;
    private Object session;
    private Object data;

    public short getService() {
        return service;
    }

    public void setService(short service) {
        this.service = service;
    }

    public short getAction() {
        return action;
    }

    public void setAction(short action) {
        this.action = action;
    }

    public Object getSession() {
        return session;
    }

    public void setSession(Object session) {
        this.session = session;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
