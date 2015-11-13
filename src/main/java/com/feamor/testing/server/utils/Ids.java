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
        }

        public static class GameResolver {
            public static final int GET_FULL_USER_INFO = 1;
            public static final int GET_USER_INVENTORY = 2;
            public static final int START_GAME_REQUEST = 3;
            public static final int CANCEL_START_GAME = 4;
            public static final int GAME_STARTED = 5;

            public static final int GET_GAMES_LIST = 500;
            public static final int GET_GAME_DESCRIPTION = 510;
            public static final int ADD_GAME_TO_INVENTORY_REQUEST = 550;
        }

        public static class GameLogic {
            public static class ChatGame{
                private static final int BASE = 10000;

                public static final int SYSTEM_MESSAGE = BASE + 100;
                public static final int CLIENT_ENTERED_TO_CHAT = BASE + 1;
                public static final int CLIENT_EXIT_FROM_CHAT = BASE + 2;
                public static final int GET_USER_IFNO = BASE + 4;
                public static final int GET_ONLINE_CLIENTS = BASE + 5;
                public static final int YOU_DISCONNECTED = BASE + 6;

                public static final int SEND_MESSAGE_TO_CLIENT = BASE + 20;
                public static final int RECEIVE_MESSAGE_FROM_CLIENT = BASE + 30;
            }
        }

    }
}
