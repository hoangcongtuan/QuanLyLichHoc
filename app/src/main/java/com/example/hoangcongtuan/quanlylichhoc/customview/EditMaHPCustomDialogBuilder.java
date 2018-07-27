package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.hoangcongtuan.quanlylichhoc.R;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class EditMaHPCustomDialogBuilder extends AlertDialog.Builder {

    private final static String TAG = EditMaHPCustomDialogBuilder.class.getName();

    private View rootView;
    private AutoCompleteTextView edtMaHP;
    private TextInputLayout textInputLayout;

    public EditMaHPCustomDialogBuilder(@NonNull Context context) {
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_custome, lstClass);
        edtMaHP.setAdapter(arrayAdapter);
    }

    public void showError(int strResourceId) {
        this.textInputLayout.setError(getContext().getResources().getString(strResourceId));
    }
}
