package com.example.hoangcongtuan.quanlylichhoc.models;

/**
 * Created by hoangcongtuan on 9/15/17.
 * Luu du lieu cho ung dung
 */

public class LopHP {
    public String ma_hoc_phan;
    public String ten_hoc_phan;
    public String ten_giang_vien;
    public String tkb;

    public LopHP() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public LopHP(String ma_hoc_phan, String ten_hoc_phan, String ten_giang_vien, String tkb) {
        this.ma_hoc_phan = ma_hoc_phan;
        this.ten_hoc_phan = ten_hoc_phan;
        this.ten_giang_vien = ten_giang_vien;
        this.tkb = tkb;
    }

    public LopHP(String maHp, LopHPObj lopHPObj) {
        this.ma_hoc_phan = maHp;
        this.ten_hoc_phan = lopHPObj.ten_hoc_phan;
        this.ten_giang_vien = lopHPObj.ten_giang_vien;
        this.tkb = lopHPObj.tkb;
    }


    public String getMa_hoc_phan() {
        return ma_hoc_phan;
    }

    public void setMa_hoc_phan(String ma_hoc_phan) {
        this.ma_hoc_phan = ma_hoc_phan;
    }

    public String getTen_hoc_phan() {
        return ten_hoc_phan;
    }

    public void setTen_hoc_phan(String ten_hoc_phan) {
        this.ten_hoc_phan = ten_hoc_phan;
    }

    public String getTen_giang_vien() {
        return ten_giang_vien;
    }

    public void setTen_giang_vien(String ten_giang_vien) {
        this.ten_giang_vien = ten_giang_vien;
    }

    public String getTkb() {
        return tkb;
    }

    public void setTkb(String tkb) {
        this.tkb = tkb;
    }

    @Override
    public String toString() {
        return ma_hoc_phan + " - " + ten_hoc_phan + " - "
                + ten_giang_vien + " - " + tkb;
    }
}
