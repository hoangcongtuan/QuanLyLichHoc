package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;

public class ProgressDialogBuilderCustom extends AlertDialog.Builder {

    private ProgressBar progressBar;
    private TextView tv_progress;
    private View rootView;
    public ProgressDialogBuilderCustom(Context context) {
        super(context);

        init();
        initView();
    }


    private void initView() {
        //inflate layout to view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        rootView = inflater.inflate(R.layout.layout_progress_dialog, null);

        //find view
        progressBar = rootView.findViewById(R.id.progress_bar);
        tv_progress = rootView.findViewById(R.id.tv_progress_text);

        //apply layout

        setView(rootView);
    }

    public void setText(String text) {
        this.tv_progress.setText(text);
    }

    public void setText(int resId) {
        this.tv_progress.setText(resId);
    }

    private void init() {
        this.setCancelable(false);
    }
}
