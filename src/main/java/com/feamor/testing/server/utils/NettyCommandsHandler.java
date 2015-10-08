package com.feamor.testing.server.utils;

import com.feamor.testing.server.services.Messages;
import com.feamor.testing.server.services.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feamor on 08.10.2015.
 */
public class NettyCommandsHandler extends SimpleChannelInboundHandler<DataMessage> {

    private NettyClient client;
    private Messages messages;

    public NettyCommandsHandler(NettyClient client) {
        super();
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataMessage message)
            throws Exception {
        messages.newMessage(message.getService(), message.getAction(), message.getSession(), client.getId(), client.getIdType(), message.getData());
    }
}