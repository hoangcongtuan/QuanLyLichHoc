package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.hoangcongtuan.quanlylichhoc.R;

/**
 * Created by hoangcongtuan on 10/15/17.
 */

public class CustomDialogBuilderEditMaHP extends AlertDialog.Builder {

    private final static String TAG = CustomDialogBuilderEditMaHP.class.getName();

    private View rootView;
    private EditText edtMaHP;

    public CustomDialogBuilderEditMaHP(@NonNull Context context) {
        super(context);

        init();
        getWidgets();
        setWidgets();
        setWidgetEvent();

        this.setView(rootView);
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        rootView = inflater.inflate(R.layout.dialog_edit_ma_hp, null);

    }

    private void getWidgets() {
        edtMaHP = rootView.findViewById(R.id.edtMaHP);

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

}
