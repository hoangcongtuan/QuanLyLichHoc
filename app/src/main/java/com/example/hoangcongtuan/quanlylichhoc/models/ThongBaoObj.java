package com.example.hoangcongtuan.quanlylichhoc.models;

/**
 * Created by hoangcongtuan on 9/8/17.
 */

public class ThongBaoObj {
    public String content;
    public String day;
    public String event;
    public String key;

    public ThongBaoObj() {

    }

    public ThongBaoObj(String context, String day, String event, String key) {
        this.content = context;
        this.day = day;
        this.event = event;
        this.key = key;
    }

    @Override
    public String toString() {
        return "ThongBaoObj{" +
                "context='" + content + '\'' +
                ", day='" + day + '\'' +
                ", event='" + event + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
