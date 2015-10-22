package com.feamor.testing.server.utils;

/**
 * Created by feamor on 12.10.2015.
 */
public abstract class RunnableWithParams<T> implements  Runnable  {
    protected T param;
    public RunnableWithParams(T paarm) {
        this.param = paarm;
    }
}
