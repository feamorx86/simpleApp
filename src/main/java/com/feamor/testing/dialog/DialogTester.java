package com.feamor.testing.dialog;

import com.gs.collections.api.tuple.primitive.IntObjectPair;

import java.util.Random;

/**
 * Created by feamor on 01.10.2015.
 */
public class DialogTester {
    static DialogTester tester = null;
    public static void tests(StringBuilder testOut) {
        if (tester == null)
            tester = new DialogTester();
        tester.setOutput(testOut);
        if (!tester.testCreateLoginSend()) {
            System.out.println(" testCreateLoginSend : fail");
            return;
        }
        System.out.println("Tests Success");
    }

    static final String USER_NAME_PREFIX = "User_";
    static final String USER_PASSWORD_PREFIX = "pass_";

    private DialogManager controller;

    private StringBuilder output;

    public void setOutput(StringBuilder output) {
        this.output = output;
    }

    private void createController() {
        controller = new DialogManager();
    }

    private boolean createUsers(int count) {
        for(int i=0; i<count; i++){
            String name = USER_NAME_PREFIX+i;
            String password = USER_PASSWORD_PREFIX+i;
            log("Create user: "+name+" : "+password);
            int result = controller.createNewUser(name, password);
            if (result != DialogManager.Operations.NEW_CLIENT_SUCCESS) {
                log("fail to create code : "+result);
                return false;
            }
        }
        log("Test success");
        return true;
    }

    private boolean createUserInvalidData(int num) {
        String name = USER_NAME_PREFIX+num;
        String password = "";
        log("Create user no password: "+name+" : "+password);
        int result = controller.createNewUser(name, password);
        if (result != DialogManager.Operations.NEW_CLIENT_ERROR_INVALED_DATA) {
            log("Sunncess crate without of password code : "+result);
            return false;
        }

        name = "";
        password = USER_PASSWORD_PREFIX+num;
        log("Create user no name: "+name+" : "+password);
        result = controller.createNewUser(name, password);
        if (result != DialogManager.Operations.NEW_CLIENT_ERROR_INVALED_DATA) {
            log("Sunncess crate without of name code : "+result);
            return false;
        }

        name = USER_NAME_PREFIX+num;
        password = USER_PASSWORD_PREFIX+num;
        log("Create user existed user: "+name+" : "+password);
        result = controller.createNewUser(name, password);
        if (result != DialogManager.Operations.NEW_CLIENT_ERROR_NAME_BUSY) {
            log("Sunncess crate not unique user: "+result);
            return false;
        }

        return true;
    }

    private Random rand = new Random();

    private void log(String message) {
        output.append(message);
        output.append("<br>");
        System.out.println(message);
    }

    private IntObjectPair<String> normalLogin(int clientNum) {
        IntObjectPair<String> result = null;
        log("login normal first: client #"+clientNum);
        IntObjectPair<IntObjectPair<String>> loginResult = controller.loginClient(USER_NAME_PREFIX + clientNum, USER_PASSWORD_PREFIX + clientNum);
        if(loginResult != null && loginResult.getOne() == DialogManager.Operations.LOGIN_SUCCESS) {
            result = loginResult.getTwo();
        } else {
            log("fail to login "+loginResult == null?"null" : "code = "+loginResult.getOne());
            result = null;
        }
        return result;
    }

    private  boolean sendMessage(int clientId, String sessionId, int clientNum, int maxClients) {
        int next = clientNum++;
        if (next >= maxClients) next = 0;
        String message = "Test message from : "+USER_NAME_PREFIX+clientNum+", to : "+USER_NAME_PREFIX+next+". Bla-Bla-Bla!!!";
        log("Send message: " + message);
        String clientToName = USER_NAME_PREFIX+next;
        clientToName = clientToName.toUpperCase();
        DialogClient sendToClient = controller.getClientsByName().get(clientToName);
        if (sendToClient == null) {
            log("Send message: client to not found name ="+clientToName);
            return false;
        }
        int sendToId =  sendToClient.getId();


        DialogClient client = controller.getActiveUser(clientId, sessionId);
        if (client == null) {
            log("Send message: Can`t get client : "+clientId+", session"+sessionId);
            return false;
        }
        int sendResult = controller.sendMessage(client, sendToId, message);
        if (sendResult != DialogManager.Operations.SEND_MESSAGE_SUCCESS) {
            log("Send message: from : "+clientId+", to : "+sendToId+", code : "+sendResult);
            return false;
        }
        return true;
    }

    private boolean testLogout(int clientId, String sessionId) {
        DialogClient client = controller.getActiveUser(clientId, sessionId);
        if (client == null) {
            log("Logout: Can`t get client : "+clientId+", session"+sessionId);
            return false;
        }
        int result = controller.logoutClient(client);
        if (result != DialogManager.Operations.LOGOUT_SUCCESS) {
            log("Logout: fail, code : "+result);
            return false;
        }
        return true;
    }

    public boolean testCreateLoginSend() {
        int max_clients = 5;
        log("Create controoler");
        if (controller == null) {
            createController();
        }
        log("\n\nCreate "+max_clients+" clients");
        if (!createUsers(max_clients)) return false;
        int randClient;

        log("\n\nCreate invalid users");
        randClient= rand.nextInt(max_clients);
        if (!createUserInvalidData(randClient)) return false;

        log("\n\ncheck correct login");
        randClient= rand.nextInt(max_clients);
        IntObjectPair<String> clientAuth = normalLogin(randClient);
        if (clientAuth == null) return false;

        log("\n\nTest send message");
        if (!sendMessage(clientAuth.getOne(), clientAuth.getTwo(), randClient, max_clients)) return false;

        log("\n\nLogout user");
        if (!testLogout(clientAuth.getOne(), clientAuth.getTwo())) return false;


        log("\n\nPring all SendMessages");

        return true;
    }

}
