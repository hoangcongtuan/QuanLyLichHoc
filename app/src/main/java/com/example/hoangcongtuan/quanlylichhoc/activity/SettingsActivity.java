package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    Switch swNotification;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvents();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }



    private void getWidgets() {
        swNotification = (Switch) findViewById(R.id.swNotification);
    }

    private void setWidgets() {
        boolean enalbeNotification = sharedPreferences.getBoolean("enableNotification", true);
        swNotification.setChecked(enalbeNotification);
    }

    private void setWidgetsEvents() {
        swNotification.setOnClickListener(this);
    }

    private void unSubscribe() {
        Utils.QLLHUtils.getsInstance(this).unSubscribeAllTopics(DBLopHPHelper.getsInstance().getListUserMaHP());
        Utils.QLLHUtils.getsInstance(this).unSubscribeTopic("TBChung");
    }

    private void subscribe() {
        Utils.QLLHUtils.getsInstance(this).subscribeTopic(DBLopHPHelper.getsInstance().getListUserMaHP());
        Utils.QLLHUtils.getsInstance(this).subscribeTopic("TBChung");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swNotification:
                if (swNotification.isChecked())
                    subscribe();
                else
                    unSubscribe();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("enableNotification", swNotification.isChecked());
        editor.commit();
    }
}
