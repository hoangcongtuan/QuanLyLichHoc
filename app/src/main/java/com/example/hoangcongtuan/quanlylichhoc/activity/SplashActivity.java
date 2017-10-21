package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Kiem tra trang thai dang nhap, neu da dang nhap thi toi man hinh chinh, con ko thi toi man hinh dang nhap
 */

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = SplashActivity.class.getName();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        DBLopHPHelper.init(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //kiem tra tai khoan hien
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //chuyen den man hinh dang nhap
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            //chuyen den mang hinh chinh
            if(DBLopHPHelper.getsInstance().isUserLocalDBAvailable()) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(this, SetupActivity.class);
                //Intent intent = new Intent(this, SQLiteDB.class);
                Log.d(TAG, "onStart: Photo url = " + user.getPhotoUrl());
                startActivity(intent);
                finish();
            }

//            Intent intent = new Intent(SplashActivity.this, SpinnerDemo.class);
//            startActivity(intent);


        }
    }
}
