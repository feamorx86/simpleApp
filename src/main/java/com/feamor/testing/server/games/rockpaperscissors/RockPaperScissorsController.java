package com.feamor.testing.server.games.rockpaperscissors;

import com.feamor.testing.server.games.ActiveGame;
import com.feamor.testing.server.games.GameCreator;
import com.feamor.testing.server.games.GamePlayer;
import io.netty.buffer.ByteBuf;

/**
 * Created by Home on 19.01.2016.
 */
public class RockPaperScissorsController extends ActiveGame {

    @Override
    protected void onCreate(GameCreator creator) {

    }

    @Override
    protected void onStarted() {

    }

    @Override
    protected void onMessage(int action, GamePlayerAccessor player, ByteBuf data) {

    }

    @Override
    protected void onNewPlayer(GamePlayerAccessor player) {

    }

    @Override
    protected void onGameFinished() {

    }

    @Override
    protected GamePlayerAccessor createPlayer(GamePlayer gamePlayer) {
        return null;
    }
}
