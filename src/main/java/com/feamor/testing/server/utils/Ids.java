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

    public static class SystemResults {
        public static final int SUCCESS = 0;
        public static final int INVALID_SESSION = 10;
        public static final int INVALID_DATA = 20;
        public static final int INTERNAL_ERROR = 15;
        public static final int NO_GAME_WITH_SUCH_ID = 16;

        public static final int GAME_IS_UNAVALABLE_NOW = 50;

        public static final int SYSTEM_RESULTS_END = 100;
    }




    public static class Actions {

        public static final int HEART_BEAT_ACTION = -1;

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
            public static final int GAME_FINISHED = 10;

            public static final int GET_GAMES_LIST = 500;
            public static final int GET_GAME_DESCRIPTION = 510;
            public static final int ADD_GAME_TO_INVENTORY_REQUEST = 550;
        }

        public static final int GAME_LOGIC_START_START_VALUE = 10000;

        public static class GameLogic {
            public static class ChatGame{
                private static final int BASE = GAME_LOGIC_START_START_VALUE;
                public static final int CLIENT_ENTERED_TO_CHAT = BASE + 1;
                public static final int CLIENT_EXIT_FROM_CHAT = BASE + 2;
                public static final int GET_USER_IFNO = BASE + 4;
                public static final int GET_ONLINE_CLIENTS = BASE + 5;
                public static final int CLIENT_REQUEST_ENTER = BASE + 6;
                public static final int GET_MESSAGES = BASE + 7;

                public static final int SEND_MESSAGE_TO_CLIENT = BASE + 20;
                public static final int RECEIVE_MESSAGE_FROM_CLIENT = BASE + 30;
            }

            public static class SimpleChat {
                private static final int BASE = GAME_LOGIC_START_START_VALUE + 100;
                public static final int NEW_USER = BASE + 1;
                public static final int USER_EXIT= BASE + 2;
                public static final int REQUEST_USERS_LIST = BASE + 4;
                public static final int LIST_USERS = BASE + 5;
                public static final int SEND_MESSAGE = BASE + 6;
                public static final int SEND_MESSAGE_RESULT = BASE + 7;
                public static final int RECEIVE_MESSAGE = BASE + 8;
                public static final int REQUEST_USER_INFO = BASE + 9;
            }
        }

    }
}
