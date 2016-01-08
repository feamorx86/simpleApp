package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.utils.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by feamor on 08.10.2015.
 */
@MessageEndpoint
public class ServicesRouter {

    @Autowired
    @Qualifier(Config.Channels.CLIENT_MESSAGES)
    private MessageChannel clientsChannel;

    @Autowired
    @Qualifier(Config.Channels.GAME_MESSAGES)
    private MessageChannel gameLogicChannel;

    @Autowired
    @Qualifier(Config.Channels.ERRORS)
    private MessageChannel errorsChannel;

    @Router(inputChannel = Config.Channels.NEW_MESSAGES, poller = @Poller(maxMessagesPerPoll = "100", fixedDelay = "200", taskExecutor = Config.Executors.MESSAGES))
    public MessageChannel route(Message<?> message) {
        int service = (Integer)message.getHeaders().get(Messages.SERVICE_HEADER);
        MessageChannel result = errorsChannel ;
        switch(service) {
            case Ids.Services.CLIENTS:
                result = clientsChannel;
                break;
            case Ids.Services.GAME_RESLOVER:
                result = gameLogicChannel;
                break;
            case Ids.Services.GAMES:
                result = gameLogicChannel;
        }
        return result;
    }
}
