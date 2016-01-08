package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.utils.IdType;
import io.netty.buffer.ByteBuf;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Created by feamor on 08.10.2015.
 */

//@MessagingGateway
//public interface Messages {
//
//    public static final String SERVICE_HEADER = "h_Service";
//    public static final String ACTION_HEADER = "h_Action";
//    public static final String SESSION_HEADER = "h_Session";
//    public static final String ID_HEADER = "h_Id";
//
//    @Gateway(requestChannel = Config.Channels.NEW_MESSAGES, requestTimeout = 200)
//    public void newMessage(int service,
//                           int action,
//                           String session,
//                           IdType id,
//                           @Payload ByteBuf data);
//
//    @Gateway(requestChannel = Config.Channels.SEND_MESSAGES, requestTimeout = 200)
//    public void send(@Header(name = ID_HEADER)IdType id,
//                     @Payload(required = false) ByteBuf data);
//}

@MessagingGateway
public interface Messages {

    public static final String SERVICE_HEADER = "h_Service";
    public static final String ACTION_HEADER = "h_Action";
    public static final String SESSION_HEADER = "h_Session";
    public static final String ID_HEADER = "h_Id";

//    @Autowired
//    @Qualifier(Config.Channels.NEW_MESSAGES)
//    private QueueChannel messagesChannel;
//
//    @Autowired
//    @Qualifier(Config.Executors.MESSAGES)
//    private ThreadPoolTaskExecutor executor;

    @Gateway(requestChannel = Config.Channels.NEW_MESSAGES)
    public void newMessage(@Header(value = SERVICE_HEADER, required = true) int service,
                           @Header(value = ACTION_HEADER, required = true) int action,
                           @Header(value = SESSION_HEADER, required = false) String session,
                           @Header(value = ID_HEADER, required = true) IdType id,
                           @Payload(required = false) ByteBuf data);
//{
//        Message<ByteBuf> message =
//        MessageBuilder.withPayload(data)
//                .setHeader(SERVICE_HEADER, service)
//                .setHeader(ACTION_HEADER, action)
//                .setHeader(SESSION_HEADER, session)
//                .setHeader(ID_HEADER, id)
//                .build();
//
//        executor.execute(new RunnableWithParams<Message<ByteBuf>>(message) {
//
//            @Override
//            public void run() {
//                messagesChannel.send(param);
//            }
//        });
//    }

    @Gateway(requestChannel =  Config.Channels.SEND_MESSAGES)
    public void send(@Header(ID_HEADER) IdType id,
                     @Header(SERVICE_HEADER)int service,
                     @Header(ACTION_HEADER) int action,
                     @Header(value = SESSION_HEADER, required = false) String session,
                     @Payload(required = false) ByteBuf data);
//    {
//
//    }
}
