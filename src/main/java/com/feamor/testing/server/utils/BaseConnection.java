package com.feamor.testing.server.utils;

/**
 * Created by feamor on 08.10.2015.
 */
public interface BaseConnection {
    void sendMessage(DataMessage message);

    int getIdType();
    int getId();
    void setId(int id);
    void setIdType(int type);
}
