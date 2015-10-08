package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.utils.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import sun.plugin2.message.Message;

/**
 * Created by feamor on 08.10.2015.
 */
public class ServicesRouter {

    @Autowired
    @Qualifier(Config.Channels.CLIENT_MESSAGES)
    private MessageChannel clientsChannel;

    @Autowired
    @Qualifier(Config.Channels.ERRORS)
    private MessageChannel errorsChannel;

    @Router(inputChannel = Config.Channels.NEW_MESSAGES, poller = @Poller(taskExecutor = Config.Executors.MESSAGES))
    public MessageChannel route(@Header(value = "service") int service) {
        MessageChannel result = errorsChannel ;
        switch(service) {
            case Ids.Services.CLIENTS:
                result = clientsChannel;
                break;
        }
        return result;
    }
}
