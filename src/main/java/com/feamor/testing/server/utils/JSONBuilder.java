package com.feamor.testing.server.utils;

import org.json.JSONObject;

/**
 * Created by feamor on 27.11.2015.
 */
public class JSONBuilder {
    public static JSONBuilder start(String key, String value) {
        return new JSONBuilder().with(key, value);
    }

    public static JSONBuilder start(String key, int value) {
        return new JSONBuilder().with(key, value);
    }

    public static JSONBuilder start(String key, float value) {
        return new JSONBuilder().with(key, value);
    }

    public static JSONBuilder start(String key, long value) {
        return new JSONBuilder().with(key, value);
    }

    public static JSONBuilder start(String key, JSONObject value) {
        return new JSONBuilder().with(key, value);
    }

    private JSONObject json;

    public JSONBuilder() {
        json = new JSONObject();
    }

    public JSONBuilder with(String key, String value) {
        json.put(key, value);
        return this;
    }

    public JSONBuilder with(String key, int value) {
        json.put(key, value);
        return this;
    }

    public JSONBuilder with(String key, float value) {
        json.put(key, value);
        return this;
    }

    public JSONBuilder with(String key, JSONObject value) {
        json.put(key, value);
        return this;
    }

    public JSONObject get() {
        return json;
    }
}
