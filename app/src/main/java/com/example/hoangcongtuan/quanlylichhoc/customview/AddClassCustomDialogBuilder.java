package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.helper.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class AddClassCustomDialogBuilder extends AlertDialog.Builder {

    private final static String TAG = AddClassCustomDialogBuilder.class.getName();

    private View rootView;
    private AutoCompleteTextView autoMaHP;
    private AutoCompleteTextView autoTenHP;
    private EditText edtTKB;
    private EditText edtGV;
    private TextInputLayout textInputLayout;


    public AddClassCustomDialogBuilder(@NonNull Context context) {
        super(context);

        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();

        this.setView(rootView);
    }

    private void init() {
        setTitle(getContext().getResources().getString(R.string.lop_hoc_phan));
        LayoutInflater inflater = LayoutInflater.from(getContext());
        rootView = inflater.inflate(R.layout.layout_them_hphan_dialog, null);
    }

    private void getWidgets() {

        autoMaHP = rootView.findViewById(R.id.autoMaHP);
        autoTenHP = rootView.findViewById(R.id.autoTenHP);
        edtTKB = rootView.findViewById(R.id.edtTKB);
        edtGV = rootView.findViewById(R.id.edtGV);
        textInputLayout = rootView.findViewById(R.id.textInputLayout);
    }

    private void setWidgets() {

    }

    private void updateUI(LopHP lopHP) {
        autoMaHP.setText(lopHP.getMaHP());
        autoTenHP.setText(lopHP.getTenHP());
        edtGV.setText(lopHP.getTenGV());
        edtTKB.setText(lopHP.getTkb());
    }

    public void showError(String str) {
        textInputLayout.setError(str);
    }

    public void showError(int strId) {
        textInputLayout.setError(
                getContext().getResources().getString(strId)
        );
    }

    public void setAutoCompleteList() {
        ArrayList<String> listId = DBLopHPHelper.getsInstance().getListMaHP();
        ArrayList<LopHP> listHP = DBLopHPHelper.getsInstance().getAllListHP();

        ArrayAdapter<String> adapterMaHP = new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_custome, listId);
        ArrayAdapter<LopHP> adapterTenHP = new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_custome, listHP);

        autoTenHP.setAdapter(adapterTenHP);
        autoMaHP.setAdapter(adapterMaHP);
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
                LopHP lopHP = (LopHP) adapterView.getItemAtPosition(i);
                updateUI(lopHP);
            }
        });

    }

    public LopHP getCurrentLopHP() {
        return DBLopHPHelper.getsInstance().getLopHocPhan(autoMaHP.getText().toString());
    }

}
