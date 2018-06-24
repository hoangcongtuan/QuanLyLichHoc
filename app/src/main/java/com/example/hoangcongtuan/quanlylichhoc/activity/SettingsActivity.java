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

    private  final static String TOPIC_TBCHUNG = "TBChung";

    private Switch swNotification;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_act_title));

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
        Utils.getsInstance(this).unSubscribeAllTopics(DBLopHPHelper.getsInstance().getListUserMaHP());
        Utils.getsInstance(this).unSubscribeTopic(
                TOPIC_TBCHUNG);
    }

    private void subscribe() {
        Utils.getsInstance(this).subscribeTopic(DBLopHPHelper.getsInstance().getListUserMaHP());
        Utils.getsInstance(this).subscribeTopic(
                TOPIC_TBCHUNG
        );
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
