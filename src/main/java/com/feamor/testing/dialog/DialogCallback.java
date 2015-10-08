package com.feamor.testing.dialog;

/**
 * Created by feamor on 30.09.2015.
 */
public interface DialogCallback {
    void onNewMessage(DialogClient client, DialogMessage message);
    void onNewClient(DialogClient client, int id, String name);
    void onClientConnected(DialogClient client, int id, String name);
    void onControllerAction(DialogClient client, int action, Object data);
}
