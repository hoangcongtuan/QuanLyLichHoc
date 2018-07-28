package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.hoangcongtuan.quanlylichhoc.R;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class EditClassIDCustomDialogBuilder extends AlertDialog.Builder {

    private final static String TAG = EditClassIDCustomDialogBuilder.class.getName();

    private View rootView;
    private AutoCompleteTextView edtMaHP;
    private TextInputLayout textInputLayout;
    private ArrayAdapter<String> arrayAdapter;

    public EditClassIDCustomDialogBuilder(@NonNull Context context) {
        super(context);
        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();
        this.setView(rootView);
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        rootView = inflater.inflate(R.layout.layout_edit_ma_hp_dialog, null);
    }

    private void getWidgets() {
        edtMaHP = rootView.findViewById(R.id.edtMaHP);
        textInputLayout = rootView.findViewById(R.id.textInputLayout);
    }

    private void setWidgets() {

    }

    private void setWidgetEvent() {

    }

    public void setMaHP(String strMaHP) {
        this.edtMaHP.setText(strMaHP);
    }

    public String getMaHP() {
        return edtMaHP.getText().toString();
    }

    public void showError(String str) {
        this.textInputLayout.setError(str);
    }

    public void setAutoCompleteList(ArrayList<String> lstClass) {
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_custome, lstClass);
        edtMaHP.setAdapter(arrayAdapter);
    }

    public void showError(int strResourceId) {
        this.textInputLayout.setError(getContext().getResources().getString(strResourceId));
    }
}
