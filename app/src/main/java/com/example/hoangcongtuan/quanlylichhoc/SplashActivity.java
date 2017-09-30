package com.example.hoangcongtuan.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
        }
        else {
            //chuyen den mang hinh chinh
            //Intent intent = new Intent(this, MainActivity.class);
            Intent intent = new Intent(this, SetupActivity.class);
            //Intent intent = new Intent(this, SQLiteDB.class);
            Log.d(TAG, "onStart: Photo url = " + user.getPhotoUrl());
            startActivity(intent);

        }
    }
}
