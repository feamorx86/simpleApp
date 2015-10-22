package com.feamor.testing.server.utils;

import com.feamor.testing.server.NettyManager;
import com.feamor.testing.server.services.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.log4j.Logger;

/**
 * Created by feamor on 11.10.2015.
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    static Logger log = Logger.getLogger(NettyChannelInitializer.class.getName());
    private NettyManager manager;
    private Messages messages;

    public NettyChannelInitializer(NettyManager manager, Messages messages) {
        super();
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    protected void initChannel(SocketChannel ch)
            throws Exception {
        NettyClient client = new NettyClient(ch);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("frame", new LengthFieldBasedFrameDecoder(1024*100, 0, Integer.BYTES));
        pipeline.addLast("codec", new NettyCommandCodec(manager.getCommandAllocator()));
        pipeline.addLast("handler", new NettyCommandsHandler(client, messages));
        manager.getUserManager().addNewConnection(client);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("Netty error", cause);
        super.exceptionCaught(ctx, cause);
    }
}
