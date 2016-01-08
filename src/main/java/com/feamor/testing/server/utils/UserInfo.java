package com.feamor.testing.server.utils;

import com.feamor.testing.server.services.GameResolver;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by feamor on 24.10.2015.
 */
public class UserInfo {
    public String login;
    public String password;

    public String firstName;
    public String secondName;
    public String iconUri;

    public String email;

    public HashMap<Integer, Object> gameStatistics;
    public HashMap<Long, Integer> avalableGames;

    public int id;

    public UserInfo(String login, String password) {
        this.login = login;
        this.password = password;

        gameStatistics = new HashMap<>();
        avalableGames = new HashMap<>();
    }

    public JSONObject getShortAsJson() {
        JSONObject json = new JSONObject();
        json.put("firstName", firstName);
        json.put("secondName", secondName);
        json.put("icon", iconUri);
        return  json;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                '}';
    }
}
