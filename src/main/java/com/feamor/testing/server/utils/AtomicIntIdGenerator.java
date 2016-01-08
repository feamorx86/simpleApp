package com.feamor.testing.server.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 19.12.2015.
 */
public class AtomicIntIdGenerator implements  IdGenerator {
    private AtomicInteger generator;

    public AtomicIntIdGenerator() {
        generator = new AtomicInteger();
    }

    public AtomicIntIdGenerator(int startValue) {
        generator = new AtomicInteger();
        generator.set(startValue);
    }

    @Override
    public int generateId(Object... args) {
        return generator.incrementAndGet();
    }
}
