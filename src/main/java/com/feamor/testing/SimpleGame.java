package com.feamor.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * Created by user on 21.08.2015.
 */
public class SimpleGame {
    @Autowired
    private ConnectionManager manager;
    @Autowired
    private PlayersDAO playersDAO;
    private HashMap<Integer, Player> players;

    public int processCommand(GameCommand command) {
        switch (command.getId()) {
            case SimpleGameCommands.CMD_REGISTER_USER:
                registerNewUser(command);
                break;
            case SimpleGameCommands.CMD_LOGIN_USER:
                tryUserLogin(command);
                break;
        }
        return 0;
    }

    private void tryUserLogin(GameCommand command) {
        int connectionId = command.getSenderId();
        CommandConnection connection = manager.getConnection(connectionId);
        if (connection == null) {
            //log error
        } else {
            String login = (String) command.get("login");
            String password = (String) command.get("password");

            GameCommand result = new GameCommand();
            result.setId(SimpleGameCommands.CMD_LOGIN_USER);
            if (StringUtils.isEmpty(login)) {
                result.put("result", Boolean.FALSE);
                result.put("problem", "empty login");
                result.put("description", "User `login` is empty. Please enter `login`.");
            } else if (StringUtils.isEmpty(password)) {
                result.put("result", Boolean.FALSE);
                //TODO: add check password
                result.put("problem", "empty password");
                result.put("description", "User `password` is empty. Please enter `password`.");
            } else {
                Player player = playersDAO.getUserByLoginAndPassword(login, password);
                if (player == null) {
                    result.put("result", Boolean.FALSE);
                    result.put("problem", "user not exist");
                    result.put("description", "User with such login and password did not exist. Check that you enter correct login and password.");
                } else {
                    result.put("result", Boolean.TRUE);
                    result.put("user", player);
                    player.setConnection(connection);
                    notifyUserEnter(player, false);
                    players.put(player.getId(), player);
                }
            }
            connection.sendCommand(result);
        }
    }

    private void registerNewUser(GameCommand command) {
        int connectionId = command.getSenderId();
        CommandConnection connection = manager.getConnection(connectionId);
        if (connection == null) {
            //log error
        } else {
            String login = (String) command.get("login");
            String password = (String) command.get("password");
            String name = (String) command.get("name");

            GameCommand result = new GameCommand();
            result.setId(SimpleGameCommands.CMD_REGISTER_USER);
            if (StringUtils.isEmpty(login)) {
                result.put("result", Boolean.FALSE);
                result.put("problem", "empty login");
                result.put("description", "User `login` is empty. Please enter `login`.");
            } else if (StringUtils.isEmpty(password)) {
                result.put("result", Boolean.FALSE);
                //TODO: add check password
                result.put("problem", "empty password");
                result.put("description", "User `password` is empty. Please enter `password`.");
            } else if (StringUtils.isEmpty(name)) {
                result.put("result", Boolean.FALSE);
                result.put("problem", "empty name");
                result.put("description", "User `name` is empty. Please enter you name.");
            } else {
                if (playersDAO.hasUserByLogin(login)) {
                    result.put("result", Boolean.FALSE);
                    result.put("problem", "user exist");
                    result.put("description", "User with such login ("+login+") already exist. Please enter another login.");
                } else {
                    Player player = playersDAO.createNewPlayer(login, password, name);
                    result.put("result", Boolean.TRUE);
                    result.put("id", player.getId());
                    player.setConnection(connection);
                    notifyUserEnter(player, true);
                    players.put(player.getId(), player);
                }
            }
            connection.sendCommand(result);
        }
    }

    private void notifyUserEnter(Player player, boolean isNewUser) {
        GameCommand command = new GameCommand(SimpleGameCommands.CMD_USER_ENTER_CHAT);
        command.put("id", player.getId());
        command.put("new", isNewUser);
        command.put("name", player.getName());

        for(Player p : players.values()) {
            p.getConnection().sendCommand(command);
        }
    }
}
