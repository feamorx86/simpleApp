package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Created by feamor on 08.10.2015.
 */

@MessagingGateway
public interface Messages {
    @Gateway(replyChannel = Config.Channels.NEW_MESSAGES)
    public void newMessage(@Header(value = "services") short service,
                           @Header(value = "action")short action,
                           @Header(value = "session")String session,
                           @Header(value = "id")int id,
                           @Header(value = "idType")int idType,
                           @Payload Object data);
}
