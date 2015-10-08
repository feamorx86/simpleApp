package com.feamor.testing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 21.08.2015.
 */
public class Player {
    private int id;
    private int state;
    private String name;
    private String login;
    private String password;
    private CommandConnection connection;

    public CommandConnection getConnection() {
        return connection;
    }

    public void setConnection(CommandConnection connection) {
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private ArrayList<Integer> friends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<Integer> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Integer> friends) {
        this.friends = friends;
    }
}
