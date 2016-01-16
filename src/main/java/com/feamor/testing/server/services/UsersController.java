package com.feamor.testing.server.services;

import com.feamor.testing.Application;
import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by feamor on 08.10.2015.
 */

@MessageEndpoint
public class UsersController {
    private static final Logger log = Logger.getLogger(UsersController.class);
    @Autowired
    private UserManager userManager;

    @Autowired
    private Messages messages;

    @ServiceActivator(poller = @Poller(taskExecutor = Config.Executors.UTILITY, fixedDelay = "200", maxMessagesPerPoll = "100"), inputChannel = Config.Channels.CLIENT_MESSAGES)
    public void onNewMessage(@Header(name = Messages.SERVICE_HEADER) int service,
                             @Header(name = Messages.ACTION_HEADER)int action,
                             @Header(name = Messages.SESSION_HEADER, required = false)String session,
                             @Header(name = Messages.ID_HEADER)IdType id,
                             @Payload(required = false) ByteBuf data) {

        switch (action){
            case Ids.Actions.Clients.REGISTER_NEW:
                registerUser(id, data);
                break;
            case Ids.Actions.Clients.LOGIN:
                loginUser(id, data);
                break;
            default:
                break;
        }
    }

    @ServiceActivator(poller = @Poller(taskExecutor = Config.Executors.SENDER, fixedDelay = "200", maxMessagesPerPoll = "100"), inputChannel = Config.Channels.SEND_MESSAGES)
    public void sendMessage(@Header(Messages.ID_HEADER) IdType id,
                            @Header(Messages.SERVICE_HEADER)int service,
                            @Header(Messages.ACTION_HEADER) int action,
                            @Header(value = Messages.SESSION_HEADER, required = false) String session,
                            @Payload(required = false) ByteBuf data) {

        NettyClient connection = userManager.getConnectionForId(id);
        if (connection!=null) {
            DataMessage message = new DataMessage(service, action, session, data);
            connection.sendMessage(message);
        } else {
            log.error("Connection is null");
        }
    }

    public void registerUser(IdType connectionId, ByteBuf data) {
        String jsonString = DataUtils.readString(data);
        JSONObject json = null;
        if (data!=null) {
            data.release();
        }
        if (!StringUtils.isEmpty(jsonString)) {
            try {
                json = new JSONObject(jsonString);
            } catch (JSONException ex) {
                //todo: add logging error
                json = null;
            }
        }
        if (json == null) {
            ByteBuf replyData = ByteBufAllocator.DEFAULT.ioBuffer();
            replyData.writeInt(UserManager.Results.INVALID_DATA);
            messages.send(connectionId, Ids.Services.CLIENTS, Ids.Actions.Clients.REGISTER_NEW, null, replyData);
        } else {
            int type = json.optInt("type", -1);
            String id = json.optString("id", null);
            String password = json.optString("password", null);
            String other = json.optString("other", null);
            int result = userManager.registerUser(type, id, password, other);
            ByteBuf replyData = ByteBufAllocator.DEFAULT.ioBuffer();
            replyData.writeInt(result);
            messages.send(connectionId, Ids.Services.CLIENTS, Ids.Actions.Clients.REGISTER_NEW, null, replyData);
        }
    }

    public void loginUser(IdType connectionId, ByteBuf data) {
        String jsonString = DataUtils.readString(data);
        JSONObject json = null;
        if (data!=null) {
            data.release();
        }
        if (!StringUtils.isEmpty(jsonString)) {
            try {
                json = new JSONObject(jsonString);
            } catch (JSONException ex) {
                //todo: add logging error
                json = null;
            }
        }
        if (json == null) {

            ByteBuf replyData = ByteBufAllocator.DEFAULT.ioBuffer();
            replyData.writeInt(UserManager.Results.INVALID_DATA);
            messages.send(connectionId, Ids.Services.CLIENTS, Ids.Actions.Clients.LOGIN, null, replyData);
        } else {
            int type = json.optInt("type", -1);
            String id = json.optString("id", null);
            String password = json.optString("password", null);
            String other = json.optString("other", null);
            Map.Entry<Integer, UserInfo> result = userManager.loginUser(type, id, password, other);
            String session = null;
            ByteBuf replyData = ByteBufAllocator.DEFAULT.ioBuffer();
            if (result.getKey() == UserManager.Results.SUCCESS) {
                GamePlayer player = userManager.createGamePlayer(result.getValue(), connectionId);
                if (player!=null) {
                    session = player.getSession();
                    replyData.writeInt(UserManager.Results.SUCCESS);
                    replyData.writeInt(player.getId().getId());
                }
            } else {
                replyData.writeInt(UserManager.Results.LOGIN_OR_PASSWORD_INVALID);
            }
            messages.send(connectionId, Ids.Services.CLIENTS, Ids.Actions.Clients.LOGIN, session, replyData);
        }
    }
}
