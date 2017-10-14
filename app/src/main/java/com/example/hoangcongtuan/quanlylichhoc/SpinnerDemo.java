package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

public class SpinnerDemo extends AppCompatActivity {

    private final static String TAG = SpinnerDemo.class.getName();

    private AutoCompleteTextView autoMaHP;
    private AutoCompleteTextView autoTenHP;
    private TextView tvTKB;
    ArrayList<String> lstMaHP;
    ArrayList<String> lstTenHP;
    ArrayAdapter<String> adapterMaHP;
    ArrayAdapter<String> adapterTenHP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_demo);

        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();
    }

    private void init() {
        lstMaHP = DBLopHPHelper.getsInstance().getListMaHP();
        lstTenHP = DBLopHPHelper.getsInstance().getListTenHP();

        adapterMaHP = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstMaHP);
        adapterTenHP = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstTenHP);

        Log.d(TAG, "init: " + lstMaHP.size());
    }

    private void getWidgets() {

        autoMaHP = (AutoCompleteTextView) findViewById(R.id.autoMaHP);
        autoTenHP = (AutoCompleteTextView) findViewById(R.id.autoTenHP);
        tvTKB = (TextView) findViewById(R.id.tvTKB);

    }

    private void setWidgets() {
        autoTenHP.setAdapter(adapterTenHP);
        autoMaHP.setAdapter(adapterMaHP);

        adapterTenHP.notifyDataSetChanged();
        adapterMaHP.notifyDataSetChanged();
    }

    private void setWidgetEvent() {

    }
}
