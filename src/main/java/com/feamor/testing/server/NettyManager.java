package com.feamor.testing.server;

import com.feamor.testing.server.services.Messages;
import com.feamor.testing.server.services.UserManager;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.NettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Created by feamor on 11.10.2015.
 */
public class NettyManager {
    static Logger log = Logger.getLogger(NettyChannelInitializer.class.getName());
    @Autowired
    private UserManager userManager;

    @Autowired
    private Messages messages;

    private EventLoopGroup acceptEventLoopGroup;
    private EventLoopGroup clientEventLoopGroup;
    private ServerBootstrap bootstrap;
    private ByteBufAllocator commandAllocator;

    public void startServer() {
        commandAllocator = new PooledByteBufAllocator(true);
        acceptEventLoopGroup = new NioEventLoopGroup();
        clientEventLoopGroup = new NioEventLoopGroup();
        ChannelFuture bindFuture = null;
        try {
            bootstrap = new ServerBootstrap();

            bootstrap.group(acceptEventLoopGroup, clientEventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);

            bootstrap.childHandler(new NettyChannelInitializer(this, messages));
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            bindFuture = bootstrap.bind(PORT);
            bindFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("---------- Netty server started ----------");
                    } else {
                        if (future.isCancelled()) {
                            log.error("Fail to bind server, port:" + PORT + ", Operation Canceled");
                            //TODO: add error code / type
                        } else {
                            log.error("Fail to bind server, port:" + PORT, future.cause());
                            //TODO: add error code / type
                        }
                    }
                }
            });
        } catch(Throwable e) {
            log.error("Fail to start server", e);
        }
    }

    public static final int PORT = 19790;

    public UserManager getUserManager() {
        return userManager;
    }

    public ByteBufAllocator getCommandAllocator() {
        return commandAllocator;
    }
}
