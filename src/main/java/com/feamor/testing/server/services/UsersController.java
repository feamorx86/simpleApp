package com.feamor.testing.server.services;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.games.GamePlayer;
import com.feamor.testing.server.utils.DataUtils;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.Ids;
import com.feamor.testing.server.utils.UserInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
    @Autowired
    private UserManager userManager;

    @Autowired
    private Messages messages;

    @ServiceActivator(poller = @Poller(taskExecutor = Config.Executors.UTILITY, fixedRate = "500", maxMessagesPerPoll = "100"), inputChannel = Config.Channels.CLIENT_MESSAGES)
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
            ByteBuf replyData = null;
            if (result.getKey() == UserManager.Results.SUCCESS) {
                GamePlayer player = userManager.createGamePlayer(result.getValue(), connectionId);
                if (player!=null) {
                    replyData = ByteBufAllocator.DEFAULT.ioBuffer();
                    session = player.getSession();
                    replyData.writeInt(UserManager.Results.SUCCESS);
                    replyData.writeInt(player.getId().getId());
                }
            }
            userManager.sendMessage(connectionId, Ids.Services.CLIENTS, Ids.Actions.Clients.LOGIN, session, replyData);
        }
    }
}
