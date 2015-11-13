package com.feamor.testing.server.games;

import com.feamor.testing.Application;
import com.feamor.testing.server.services.GameResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Created by feamor on 24.10.2015.
 */
public class  ChatGameCreator extends GameCreator {

    protected ActiveGame chatGame = null;

    @Override
    public void addNewPlayer(GamePlayer player) {
        if(chatGame == null) {
            chatGame = createGame();
            gameResolver.prepareGame(chatGame, this);
        }
        if (chatGame.isStarted()) {
            gameResolver.notifyGameStarted(player, chatGame.getId());
            chatGame.addPlayer(player);
        } else {
            super.addNewPlayer(player);
        }
    }

    @Override
    public ActiveGame createGame() {
        ChatGame game = new ChatGame();
        Application.getContext().getAutowireCapableBeanFactory().autowireBean(game);
        return game;
    }
}