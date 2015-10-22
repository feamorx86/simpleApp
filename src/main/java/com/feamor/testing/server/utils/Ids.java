package com.feamor.testing.server.utils;

/**
 * Created by feamor on 08.10.2015.
 */
public class Ids {
    public static  class Services{
        public static final int CLIENTS = 10;
        public static final int GAMES = 20;
        public static final int GAME_RESLOVER = 30;
    }

    public static class Actions {
        public static class Clients {
            public static final int REGISTER_NEW = 100;
            public static final int LOGIN = 200;
            public static final int RECOVER = 300;
            public static final int UNREGISTER = 400;

            public static final int GET_USER_INFO = 1000;
        }
    }
}
