package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

public class SpinnerDemo extends AppCompatActivity {

    private final static String TAG = SpinnerDemo.class.getName();

    private AutoCompleteTextView autoMaHP;
    private AutoCompleteTextView autoTenHP;
    private TextView tvTKB;
    private TextView tvGV;
    ArrayList<String> lstMaHP;
    ArrayList<String> lstTenHP;
    ArrayAdapter<String> adapterMaHP;
    ArrayAdapter<String> adapterTenHP;

    LopHP currentLopHP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_demo);

        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();

        CustomDialogBuilderLopHP customDialogBuilderLopHP = new CustomDialogBuilderLopHP(this);
        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.show();
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
        tvGV = (TextView) findViewById(R.id.tvGV);

    }

    private void setWidgets() {
        autoTenHP.setAdapter(adapterTenHP);
        autoMaHP.setAdapter(adapterMaHP);

        adapterTenHP.notifyDataSetChanged();
        adapterMaHP.notifyDataSetChanged();
    }

    void update(LopHP lopHP) {
        autoMaHP.setText(lopHP.getMaHP());
        autoTenHP.setText(lopHP.getTenHP());
        tvGV.setText(lopHP.getTenGV());
        tvTKB.setText(lopHP.getTkb());
        currentLopHP = lopHP;
    }

    private void setWidgetEvent() {

        autoMaHP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                update(DBLopHPHelper.getsInstance().getLopHocPhan(autoMaHP.getText().toString()));
            }
        });

        autoTenHP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                update(DBLopHPHelper.getsInstance().getLopHPbyName(autoTenHP.getText().toString()));
            }
        });

    }

    public LopHP getCurrentLopHP() {
        return this.currentLopHP;
    }
}