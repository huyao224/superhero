package com.next.utils;

public enum  MovieTypeEnum {

    SUPERHERO("superhero","热门超英"),
    TRAILER("trailer","热门预告片");

    public final String type;
    public final String value;

    private MovieTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
