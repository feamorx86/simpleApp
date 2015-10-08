package com.feamor.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 21.08.2015.
 */
public class PlayersDAO {
    public boolean hasUserByLogin(String login) {
        return false;
    }
    private static AtomicInteger _internal = new AtomicInteger();
    public Player createNewPlayer(String login, String password, String name) {
        Player player = new Player();
        player.setLogin(login);
        player.setPassword(password);
        player.setName(name);
        //TODO: add save
        player.setId(_internal.incrementAndGet());
        return player;
    }

    private static List<String> _logins = Arrays.asList(new String[]{"test", "login1", "ivan"});

    public Player getUserByLoginAndPassword(String login, String password) {
        //TODO: add db request!!!
        if (_logins.contains(login) && "test".equalsIgnoreCase(password)) {
            return createNewPlayer(login, password, "some internal test name");
        } else {
            return null;
        }
    }
}
