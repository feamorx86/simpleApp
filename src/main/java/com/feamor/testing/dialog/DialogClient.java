package com.feamor.testing.dialog;

import com.gs.collections.impl.list.mutable.primitive.IntArrayList;

import java.util.ArrayList;

/**
 * Created by feamor on 29.09.2015.
 */
public class DialogClient {

    public static final int STATUS_ONLINE = 0;
    public static final int STATUS_OFFLINE = 1;

    private int id;
    private String name;
    private String password;
    private int status;
    private DialogClientActionListener actionListener;

    public DialogClient(DialogClientActionListener actionListener) {
        this.actionListener = actionListener;
        status = STATUS_OFFLINE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ClientInfo createinfo() {
        ClientInfo info = new ClientInfo();
        info.id = getId();
        info.name = getName();
        info.status = getStatus();
        return info;
    }

    public static class ClientInfo{
        int id;
        int status;
        String name;
    }

    public DialogClientActionListener getActionListener() {
        return actionListener;
    }

    public interface DialogClientActionListener {
        void onAction(int action, Object params);
    }
}
