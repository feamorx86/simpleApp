package com.feamor.testing.server.services;

import com.feamor.testing.IntegrationConfig;
import com.feamor.testing.server.Config;
import com.feamor.testing.server.utils.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Created by feamor on 08.10.2015.
 */

@MessageEndpoint
public class UsersController {
    @Autowired
    private UserManager userManager;

    @ServiceActivator(poller = @Poller(taskExecutor = Config.Executors.UTILITY), inputChannel = Config.Channels.CLIENT_MESSAGES)
    public void onNewMessage(@Header(value = "action")short action,
                             @Header(value = "session")String session,
                             @Header(value = "id")int id,
                             @Header(value = "idType")int idType,
                             @Payload Object data) {
        switch (action){
            case Ids.Actions.Clients.REGISTER_NEW:

                break;
            default:
                break;
        }
    }
}
