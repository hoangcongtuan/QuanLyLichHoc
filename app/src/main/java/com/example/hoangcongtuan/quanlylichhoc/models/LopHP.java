package com.example.hoangcongtuan.quanlylichhoc.models;

/**
 * Created by hoangcongtuan on 9/15/17.
 * Luu du lieu cho ung dung
 */

public class LopHP {
    public String maHP;
    public String tenHP;
    public String tenGV;
    public String tkb;

    public LopHP() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public LopHP(String maHP, String tenHP, String tenGV, String tkb) {
        this.maHP = maHP;
        this.tenHP = tenHP;
        this.tenGV = tenGV;
        this.tkb = tkb;
    }

    public LopHP(String maHp, LopHPObj lopHPObj) {
        this.maHP = maHp;
        this.tenHP = lopHPObj.tenHP;
        this.tenGV = lopHPObj.tenGV;
        this.tkb = lopHPObj.tkb;
    }


    public String getMaHP() {
        return maHP;
    }

    public void setMaHP(String maHP) {
        this.maHP = maHP;
    }

    public String getTenHP() {
        return tenHP;
    }

    public void setTenHP(String tenHP) {
        this.tenHP = tenHP;
    }

    public String getTenGV() {
        return tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public String getTkb() {
        return tkb;
    }

    public void setTkb(String tkb) {
        this.tkb = tkb;
    }

    @Override
    public String toString() {
        return maHP + " - " + tenHP + " - "
                + tenGV + " - " + tkb;
    }
}
