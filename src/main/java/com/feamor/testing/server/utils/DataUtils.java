package com.feamor.testing.server.utils;

import io.netty.buffer.ByteBuf;

/**
 * Created by feamor on 11.10.2015.
 */
public class DataUtils {



    public static String readString(ByteBuf buffer) {
        String result = null;
        try {
            int length = buffer.readInt();
            if (length > 0) {
                byte[] data = new byte[length];
                if (length == 0) {
                    result = "";
                } else {
                    buffer.readBytes(data, 0, length);
                    result = new String(data);
                }
            } else {
                result = null;
            }
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    public static void writeString(ByteBuf buffer, String string) {
        if (string == null) {
            buffer.writeInt(-1);
        } else {
            byte[] data = string.getBytes();
            buffer.writeInt(data.length);
            if (data.length > 0) {
                buffer.writeBytes(data);
            }
        }
    }
}
