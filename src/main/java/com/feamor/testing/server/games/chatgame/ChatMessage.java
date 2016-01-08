package com.feamor.testing.server.games.chatgame;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by feamor on 26.11.2015.
 */
public class ChatMessage {
    private String message;
    private Date createTime;
    private int playerId;

    public ChatMessage(String message, Date createTime, int senderId) {
        this.message = message;
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getMessage() {
        return message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("message", message);
        json.put("time", createTime.getTime());
        json.put("player", playerId);
        return json;
    }
}