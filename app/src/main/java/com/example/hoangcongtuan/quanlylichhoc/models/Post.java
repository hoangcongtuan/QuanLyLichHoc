package com.example.hoangcongtuan.quanlylichhoc.models;

import java.util.Date;

/**
 * Created by hoangcongtuan on 9/7/17.
 */

public class Post {
    Date date;
    String tittle;
    String content;
    String strDate;
    String key;

    public Post(String strDate, String tittle, String content, String key) {
        this.strDate = strDate;
        this.tittle = tittle;
        this.content = content;
        this.strDate = strDate;
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
