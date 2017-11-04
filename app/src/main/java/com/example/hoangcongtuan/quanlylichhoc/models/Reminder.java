package com.example.hoangcongtuan.quanlylichhoc.models;

import java.util.Random;

/**
 * Created by huuthangit on 2017-10-25.
 */

public class Reminder {
    private int id;
    private String title;
    private String content;
    private String date;
    private String time;
    private int repeat;
    private String type;

    public Reminder() {

    }

    public Reminder(int id, String title, String content, String date, String time, int repeat, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.type = type;
    }

    public Reminder(String title, String content, String date, String time, int repeat, String type) {
        Random rd = new Random();
        this.id = rd.nextInt();
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
