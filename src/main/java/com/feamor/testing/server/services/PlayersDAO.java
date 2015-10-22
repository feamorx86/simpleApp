package com.feamor.testing.server.services;

import org.eclipse.jetty.websocket.common.util.TextUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 21.08.2015.
 */
public class PlayersDAO {

    public static class UserInfo {
        public String login;
        public String password;

        public String firstName;
        public String secondName;
        public String iconUri;

        public String email;

        public HashMap<Integer, Object> gameStatistics;

        public int id;

        public UserInfo(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    public static class GameCategory {
        public int id;
        public String name;
        public String descriptino;
    }

    public static class GameDescription{
        public int id;
        public String alias;

        public String name;
        public String description;
        public String iconUri;

        public ArrayList<Integer> gameCategories;

    }


    private HashMap<String, UserInfo> users = new HashMap<String, UserInfo>();

    public int registerUserWithLoginAnsPassword(String login, String password) {
        int result;
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            result = UserManager.Results.INVALID_DATA;
        } else{
            synchronized (users) {
                String loLogin = login.toLowerCase();
                if (users.containsKey(loLogin)) {
                    result = UserManager.Results.REGISTER_SUCH_USER_EXIST;
                } else {
                    UserInfo info = new UserInfo(login, password);
                    info.firstName = "test_first_name";
                    info.secondName = "test_second_name";
                    info.iconUri = "";
                    users.put(loLogin, info);
                    result = UserManager.Results.SUCCESS;
                }
            }
        }
        return result;
    }

    public UserInfo getUserInfo(String login) {
        UserInfo result = null;
        if (!StringUtils.isEmpty(login)) {
            String loLogin = login.toLowerCase();
            result = users.get(loLogin);
        }
        return result;
    }

    public Map.Entry<Integer, UserInfo> loginUserWithLoginAndPassword(String login, String password) {
        Map.Entry<Integer, UserInfo> result;
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            result = new HashMap.SimpleEntry<Integer, UserInfo>(UserManager.Results.INVALID_DATA, null);
        } else{
            synchronized (users) {
                String loLogin = login.toLowerCase();
                UserInfo user = users.get(loLogin);
                if (user == null || !user.password.equals(password)) {
                    result = new HashMap.SimpleEntry<Integer, UserInfo>(UserManager.Results.LOGIN_OR_PASSWORD_INVALID, null);
                } else {
                    result = new HashMap.SimpleEntry<Integer, UserInfo>(UserManager.Results.SUCCESS, user);
                }
            }
        }
        return result;
    }


}
