package com.feamor.testing.server.games.chatgame;

import com.feamor.testing.Application;
import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.Ids;

/**
 * Created by feamor on 24.10.2015.
 */
public class  ChatGameCreator extends GameCreator {

    protected ActiveGame chatGame = null;

    @Override
    public void addNewPlayer(GamePlayer player) {
        if (chatGame == null) {
            chatGame = createGame();
            super.addNewPlayer(player);
            gameResolver.prepareGame(chatGame, this);
        } else {
            if (chatGame.isStarted()) {
                gameResolver.notifyGameStarted(player, chatGame.getId());
                chatGame.addPlayer(player);
            } else {
                super.addNewPlayer(player);
            }
        }
    }

    @Override
    public ActiveGame createGame() {
        //ChatGameController game = new ChatGameController();
        SimpleChatController game = new SimpleChatController();
        Application.getContext().getAutowireCapableBeanFactory().autowireBean(game);
        return game;
    }
}