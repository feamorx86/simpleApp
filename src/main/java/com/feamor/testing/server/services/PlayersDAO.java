package com.feamor.testing.server.services;

import com.feamor.testing.server.utils.UserInfo;
import org.eclipse.jetty.websocket.common.util.TextUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 21.08.2015.
 */
public class PlayersDAO {

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
                    info.firstName = "login";
                    info.secondName = "";
                    info.iconUri = "";
                    users.put(loLogin, info);
                    result = UserManager.Results.SUCCESS;
                }
            }
        }
        return result;
    }

    public boolean changeUserInfo(UserInfo newUserInfo) {
        boolean result = false;
        if (newUserInfo!=null) {
            String id = newUserInfo.login;
            if (!StringUtils.isEmpty(id)) {
                id = id.toLowerCase();
                if (users.get(id) != null) {
                    users.put(id, newUserInfo);
                    result = true;
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
