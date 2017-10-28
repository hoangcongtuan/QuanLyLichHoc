package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
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
                //kiem tra co payload tu notification gui toi hay ko
                Intent splashIntent = getIntent();
                Log.d(TAG, "onCreate: Intent = " + splashIntent.toString());
                if (splashIntent.getExtras() != null && splashIntent.hasExtra("tieu_de")) {
//                    Log.d(TAG, "onCreate: thoi_gian = " + splashIntent.getStringExtra("thoi_gian"));
//                    Log.d(TAG, "onCreate: tieu_de = " + splashIntent.getStringExtra("tieu_de"));
//                    Log.d(TAG, "onCreate: noi_dung = " + splashIntent.getStringExtra("noi_dung"));
                    String strExtras;
                    if (splashIntent.hasExtra("tieu_de"))
                        intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
                    else
                        intent.putExtra("tieu_de", "Null");

                    if (splashIntent.hasExtra("thoi_gian"))
                        intent.putExtra("thoi_gian", splashIntent.getStringExtra("thoi_gian"));
                    else
                        intent.putExtra("thoi_gian", "Null");

                    if (splashIntent.hasExtra("noi_dung"))
                        intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
                    else
                        intent.putExtra("noi_dung", "Null");

                    if (splashIntent.hasExtra("id"))
                        intent.putExtra("id", splashIntent.getStringExtra("id"));
                    else
                        intent.putExtra("id", "Null");

                    if (splashIntent.hasExtra("type"))
                        intent.putExtra("type", splashIntent.getStringExtra("type"));
                    else
                        intent.putExtra("type", "Null");

//                    intent.putExtra("thoi_gian", splashIntent.getStringExtra("thoi_gian"));
//                    intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
//                    intent.putExtra("id", splashIntent.getStringExtra("id"));
//                    intent.putExtra("type", splashIntent.getStringExtra("type"));
                }
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(this, SetupActivity.class);
                //Intent intent = new Intent(this, SQLiteDB.class);
                //Log.d(TAG, "onStart: Photo url = " + user.getPhotoUrl());
                startActivity(intent);
                finish();
            }

//            Intent intent = new Intent(SplashActivity.this, SpinnerDemo.class);
//            startActivity(intent);


        }

        //Log.d(TAG, "onStart: " + FirebaseInstanceId.getInstance().getToken());
    }
}
