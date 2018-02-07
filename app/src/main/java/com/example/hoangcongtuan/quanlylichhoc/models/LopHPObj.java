package com.example.hoangcongtuan.quanlylichhoc.models;

/**
 * Created by hoangcongtuan on 9/15/17.
 * Lop dung de luu du lieu get tu firebase
 */


public class LopHPObj {
    public String tenGV;
    public String tenHP;
    public String tkb;


    public LopHPObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getTenGV() {
        return tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public String getTenHP() {
        return tenHP;
    }

    public void setTenHP(String tenHP) {
        this.tenHP = tenHP;
    }

    public String getTkb() {
        return tkb;
    }

    public void setTkb(String tkb) {
        this.tkb = tkb;
    }
}
