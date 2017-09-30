package com.example.hoangcongtuan.quanlylichhoc.models;

/**
 * Created by hoangcongtuan on 9/15/17.
 * Lop dung de luu du lieu get tu firebase
 */


public class LopHPObj {
    public String ten_giang_vien;
    public String ten_hoc_phan;
    public String tkb;


    public LopHPObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getTen_giang_vien() {
        return ten_giang_vien;
    }

    public void setTen_giang_vien(String ten_giang_vien) {
        this.ten_giang_vien = ten_giang_vien;
    }

    public String getTen_hoc_phan() {
        return ten_hoc_phan;
    }

    public void setTen_hoc_phan(String ten_hoc_phan) {
        this.ten_hoc_phan = ten_hoc_phan;
    }

    public String getTkb() {
        return tkb;
    }

    public void setTkb(String tkb) {
        this.tkb = tkb;
    }
}
