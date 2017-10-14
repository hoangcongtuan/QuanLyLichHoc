package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.content.Context;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;

import java.util.ArrayList;

public class ThoiKhoaBieu {
    private final static String TAG = ThoiKhoaBieu.class.getName();

    private DBLopHPHelper dbLopHPHelper;
    private ArrayList<LopHP> listLopHP;

    public ThoiKhoaBieu(Context context, ArrayList<String> listMaHP) {

        listLopHP = new ArrayList<>();

        dbLopHPHelper = DBLopHPHelper.getsInstance(context);
        for (String maHP : listMaHP) {

            LopHP lopHP = dbLopHPHelper.getLopHocPhan(maHP);
            listLopHP.add(lopHP);
        }
    }

    public ArrayList<LopHP> getThoiKhoaBieu() {
        return listLopHP;
    }

    public void addLopHP(String maHP) {
        LopHP lopHP = dbLopHPHelper.getLopHocPhan(maHP);
        int index = getIndexOf(maHP);
        if (index == -1) {
            listLopHP.add(lopHP);
        } else {
            listLopHP.set(index, lopHP);
        }
    }

    public void removeLopHP(String maHP) {
        int index = getIndexOf(maHP);
        if (index == -1) {
            Log.d(TAG, "removeLopHP: don't have " + maHP);
        } else {
            listLopHP.remove(index);
        }
    }

    private int getIndexOf(String maHP) {
        for (int i = 0; i < listLopHP.size(); i++) {
            if (listLopHP.get(i).ma_hoc_phan.equals(maHP)) {
                return i;
            }
        }
        return -1;
    }
}
