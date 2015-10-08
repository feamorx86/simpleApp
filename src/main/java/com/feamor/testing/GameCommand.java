package com.feamor.testing;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by user on 21.08.2015.
 */
public class GameCommand implements Serializable {

    private int id;
    private int service;
    private int senderId;

    private HashMap<String, Object> data;

    public GameCommand() {
        data = new HashMap<String, Object>();
    }

    public GameCommand(int id) {
        this.id = id;
        data = new HashMap<String, Object>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }
}
