package com.example.hoangcongtuan.quanlylichhoc.models;

import java.util.Date;

/**
 * Created by hoangcongtuan on 9/7/17.
 */

public class ThongBao  {
    Date date;
    String tittle;
    String content;
    String strDate;
    public ThongBao(String date, String tittle, String content) {
        this.strDate = date;
        this.tittle = tittle;
        this.content = content;
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
}
