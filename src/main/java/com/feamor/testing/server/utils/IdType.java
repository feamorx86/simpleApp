package com.feamor.testing.server.utils;

import java.io.Serializable;

/**
 * Created by feamor on 09.10.2015.
 */
public class IdType implements Serializable {
    private int id;
    private int type;

    public IdType(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void set(int id, int type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdType idType = (IdType) o;

        return (id == idType.id) && (type == idType.type);
    }

    public boolean equals(IdType IdObject) {
        boolean result = (IdObject != null && id == IdObject.id && type == IdObject.type);
        return result;
    }

    @Override
    public String toString() {
        return "( "+id+" : "+type+" )";
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type;
        return result;
    }
}
