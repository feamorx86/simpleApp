package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by feamor on 11.10.2015.
 */
public class NettyCommandCodec extends ByteToMessageCodec<DataMessage> {

    private ByteBufAllocator allocator;
    public NettyCommandCodec(ByteBufAllocator allocator) {
        super();
        this.allocator = allocator;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DataMessage msg, ByteBuf out) throws Exception {
        int lenght = Integer.BYTES + Integer.BYTES;//Service + Action
        //session lenght + session
        lenght+=Integer.BYTES;
        byte[] session = null;
        if (!StringUtils.isEmpty(msg.getSession())) {
            session = msg.getSession().getBytes();
        }
        //data length + data
        lenght+=Integer.BYTES;
        if (msg.getData()!=null) {
            msg.getData().readableBytes();
        }
        out.writeInt(lenght);
        out.writeInt(msg.getService());
        out.writeInt(msg.getAction());
        if(session != null) {
            out.writeInt(session.length);
            out.writeBytes(session);
        } else {
            out.writeInt(0);
        }
        if (msg.getData()!=null) {
            out.writeInt(msg.getData().readableBytes());
            out.writeBytes(msg.getData());
        } else {
            out.writeInt(0);
        }
        msg.recycle();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DataMessage command = new DataMessage();
        int lenght = in.readInt();
        int service = in.readInt();
        int action = in.readInt();
        int sessionLength = in.readInt();
        String session = null;
        if (sessionLength > 0) {
            byte[] sessionData = new byte[sessionLength];
            in.readBytes(sessionData, 0, sessionLength);
            session = new String(sessionData);
        } else {
            session = "";
        }
        int dataLength = in.readInt();
        ByteBuf data = null;
        if (dataLength > 0) {
            data = allocator.buffer(dataLength, dataLength);
            in.readBytes(data, dataLength);
        } else {
            data = null;
        }
        command.setService(service);
        command.setAction(action);
        command.setSession(session);
        command.setData(data);
        command.retain();
        out.add(command);
    }
}