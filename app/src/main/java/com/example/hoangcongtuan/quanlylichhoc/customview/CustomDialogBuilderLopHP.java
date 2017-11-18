package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class CustomDialogBuilderLopHP extends AlertDialog.Builder {

    private final static String TAG = CustomDialogBuilderLopHP.class.getName();

    View rootView;
    private AutoCompleteTextView autoMaHP;
    private AutoCompleteTextView autoTenHP;
    private TextView tvTKB;
    private TextView tvGV;
    //danh sach ma hoc phan, tenHP
    ArrayList<String> lstMaHP;
    ArrayList<String> lstTenHP;
    ArrayAdapter<String> adapterMaHP;
    ArrayAdapter<String> adapterTenHP;

    LopHP currentLopHP;

    public CustomDialogBuilderLopHP(@NonNull Context context) {
        super(context);

        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();

        this.setView(rootView);
    }

    private void init() {
        //Log.d(TAG, "init: custome Alert Dialog");
        setTitle(getContext().getResources().getString(R.string.lop_hoc_phan));
        LayoutInflater inflater = LayoutInflater.from(getContext());
        rootView = inflater.inflate(R.layout.dialog_them_hphan, null);
        lstMaHP = DBLopHPHelper.getsInstance().getListMaHP();
        lstTenHP = DBLopHPHelper.getsInstance().getListTenHP();

        adapterMaHP = new ArrayAdapter<String>(getContext(), R.layout.layout_dropdown_custome, lstMaHP);
        adapterTenHP = new ArrayAdapter<String>(getContext(), R.layout.layout_dropdown_custome, lstTenHP);

    }

    private void getWidgets() {

        autoMaHP = (AutoCompleteTextView)rootView.findViewById(R.id.autoMaHP);
        autoTenHP = (AutoCompleteTextView)rootView.findViewById(R.id.autoTenHP);
        tvTKB = (TextView)rootView.findViewById(R.id.tvTKB);
        tvGV = (TextView)rootView.findViewById(R.id.tvGV);

    }

    private void setWidgets() {
        autoTenHP.setAdapter(adapterTenHP);
        autoMaHP.setAdapter(adapterMaHP);

//        adapterTenHP.notifyDataSetChanged();
//        adapterMaHP.notifyDataSetChanged();
    }

    void updateUI(LopHP lopHP) {
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
                updateUI(DBLopHPHelper.getsInstance().getLopHocPhan(autoMaHP.getText().toString()));
            }
        });

        autoTenHP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateUI(DBLopHPHelper.getsInstance().getLopHPbyName(autoTenHP.getText().toString()));
            }
        });

    }

    public LopHP getCurrentLopHP() {
        return this.currentLopHP;
    }

}
