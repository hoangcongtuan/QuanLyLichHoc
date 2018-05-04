package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.Alarm.AddAlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.VersionInfo;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Kiem tra trang thai dang nhap, neu da dang nhap thi toi man hinh chinh, con ko thi toi man hinh dang nhap
 */

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = SplashActivity.class.getName();
    private final static String KEY_ALL_HP_DB_VERSION = "ALL_HP_DATABASE_VERSION";

    private FirebaseAuth mAuth;
    private final static int secondsDelayed = 0;
    private CoordinatorLayout layout_splash;
    private TextView tvLoadingInfo;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        layout_splash = findViewById(R.id.layout_finish);
        tvLoadingInfo = findViewById(R.id.tvLoadingInfo);
        tvVersion = findViewById(R.id.tvVersion);

        String version = "Phiên bản" + getResources().getString(R.string.app_version);
        tvVersion.setText(version);
        mAuth = FirebaseAuth.getInstance();
        DBLopHPHelper.init(getApplicationContext());

        //check google play services
    }

    private void check_app_version(final VersionInfo latest_version) {
        tvLoadingInfo.setText(
                getResources().getString(R.string.check_app_version)
        );
        String current_app_version = getResources().getString(R.string.app_version);
        String latest_app_version = latest_version.app_version;
        if (current_app_version.equals(latest_app_version)) {
            //dang la ver moi nhat
            Log.d(TAG, "check_app_version: latest app verison");
            check_database_version(latest_version);
        }
        else {
            //cap nhat
            Log.d(TAG, "check_app_version: older version, let update");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(latest_version.new_version_message);
            builder.setPositiveButton(getResources().getString(R.string.update),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //go to url
                            Intent browser = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(latest_version.link_latest_version)
                            );

                            startActivity(browser);
                        }
                    });
            builder.setNegativeButton(getResources().getString(R.string.later),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            check_database_version(latest_version);
                        }
                    });
            builder.setCancelable(false);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }

    private void check_database_version(VersionInfo versionInfo) {

        tvLoadingInfo.setText(
                getResources().getString(R.string.check_db_version)
        );
        //get current database version
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String current_all_hp_db_version = preference.getString(KEY_ALL_HP_DB_VERSION, "none");
        String latest_all_hp_db_version = versionInfo.database_version;
        if (current_all_hp_db_version.equals("none")) {
            Log.d(TAG, "check_database_version: Download all hp db");
            download_all_hp_database(versionInfo);
        }
        else if (current_all_hp_db_version.equals(latest_all_hp_db_version)) {
            Log.d(TAG, "check_database_version: all hp db is latest");
            //dang la moi nhat
            //kiem tra dang nhap
            check_login();
        }
        else {
            //cap nhat db
            Log.d(TAG, "check_database_version: update all hp db");
            download_all_hp_database(versionInfo);
        }
    }

    public void check_login() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            //chuyen den man hinh login
            Log.d(TAG, "check_login: not logged in, let login");
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            //login roi
            //tai du lieu user ve
            tvLoadingInfo.setText(
                    getResources().getString(R.string.sync_user_db)
            );
            Log.d(TAG, "check_login: login roi, tai du lieu user ve");
            DatabaseReference firebaseDBUserMaHP = FirebaseDatabase.getInstance().getReference()
                    .child(LoginActivity.KEY_FIRBASE_USER)
                    .child(firebaseUser.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);

            //delete, unsubscribe old topic
            ArrayList<String> list_old_topic = DBLopHPHelper.getsInstance().getListUserMaHP();

            DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
            Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeAllTopics(list_old_topic);
            Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeTopic(LoginActivity.TOPIC_TBCHUNG);

            firebaseDBUserMaHP.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //da thiet lap lop hoc phan
                        if (dataSnapshot.getChildrenCount() != 0) {
                            //co du lieu trong do
                            //save Firebase DB to local DB
                            Log.d(TAG, "onDataChange: override old user db, go to main act");
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DBLopHPHelper.getsInstance().insertUserMaHocPhan((String)snapshot.getValue());
                            }

                            ArrayList<String> list_topic = DBLopHPHelper.getsInstance().getListUserMaHP();
                            //subscribe new topic
                            Utils.QLLHUtils.getsInstance(getApplicationContext()).subscribeTopic(list_topic);
                            Utils.QLLHUtils.getsInstance(getApplicationContext()).subscribeTopic(LoginActivity.TOPIC_TBCHUNG);


                            //check intent
                            Intent splashIntent = getIntent();
                            if (splashIntent.getExtras() != null && splashIntent.hasExtra("screen")) {
                                String screen = splashIntent.getStringExtra("screen");
                                if (screen.equals("main")) {
                                    Intent main_intent = new Intent(SplashActivity.this, MainActivity.class);
                                    if (splashIntent.hasExtra("tieu_de"))
                                        main_intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
                                    else
                                        main_intent.putExtra("tieu_de", "Null");

                                    if (splashIntent.hasExtra("thoi_gian"))
                                        main_intent.putExtra("thoi_gian", splashIntent.getStringExtra("thoi_gian"));
                                    else
                                        main_intent.putExtra("thoi_gian", "Null");

                                    if (splashIntent.hasExtra("noi_dung"))
                                        main_intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
                                    else
                                        main_intent.putExtra("noi_dung", "Null");

                                    if (splashIntent.hasExtra("id"))
                                        main_intent.putExtra("id", splashIntent.getStringExtra("id"));
                                    else
                                        main_intent.putExtra("id", "Null");

                                    if (splashIntent.hasExtra("type"))
                                        main_intent.putExtra("type", splashIntent.getStringExtra("type"));
                                    else
                                        main_intent.putExtra("type", "Null");
                                    startActivity(main_intent);
                                    finish();
                                }
                                else if (screen.equals("add_alarm")) {
                                    Intent add_alarm_intent = new Intent(SplashActivity.this, AddAlarmActivity.class);
                                    if (splashIntent.hasExtra("tieu_de"))
                                        add_alarm_intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
                                    else
                                        add_alarm_intent.putExtra("tieu_de", "Null");

                                    if (splashIntent.hasExtra("noi_dung"))
                                        add_alarm_intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
                                    else
                                        add_alarm_intent.putExtra("noi_dung", "Null");
                                    startActivity(add_alarm_intent);
                                    finish();
                                }
                                else {
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
//                            //goto MainAct
//                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
                        }
                        else {
                            //chua co du lieu lop hoc phan
                            //di toi man hinh setup
                            //finishAuthWithFirebase();
                            Log.d(TAG, "onDataChange: no user data, let setup your data");
                            Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        //di toi man hinh setup
                        //finishAuthWithFirebase();
                        Log.d(TAG, "onDataChange: no user data, let setup your data");
                        Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(final DatabaseError databaseError) {
                    Snackbar snackbar = Snackbar.make(layout_splash, getResources().getString(R.string.error)
                            + databaseError.getCode() + ": " + databaseError.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(getResources().getString(R.string.details),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                                    builder.setTitle(getResources().getString(R.string.details));
                                    builder.setMessage(databaseError.getDetails());
                                    builder.show();
                                }
                            });
                    snackbar.show();
                }
            });

            //gather some information
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a");
            String latest_online = sdfDate.format(Calendar.getInstance().getTime());
            DatabaseReference ref_user_online = FirebaseDatabase.getInstance().getReference()
                    .child(LoginActivity.KEY_FIRBASE_USER).child(firebaseUser.getUid())
                    .child(LoginActivity.KEY_FIREBASE_USERINFO).child(LoginActivity.KEY_FIREBASE_USER_LATEST_ONLINE);
            ref_user_online.setValue(latest_online);
        }
    }

    public void download_all_hp_database(final VersionInfo versionInfo) {
        tvLoadingInfo.setText(
                getResources().getString(R.string.update_db)
        );
        DBLopHPHelper.getsInstance().setOnCheckDB(new DBLopHPHelper.OnCheckDB() {
            @Override
            public void onDBAvailable() {

            }

            @Override
            public void onDownloadFinish() {
                //download finish
                //update db version to share preference
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_ALL_HP_DB_VERSION,versionInfo.database_version);
                editor.commit();

                //check login
                check_login();
            }

            @Override
            public void onStartDownload() {

            }
        });

        DBLopHPHelper.getsInstance().download_all_hp_database();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //kiem tra phien ban phan mem
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref_version = databaseReference.child("version_info");

        ref_version.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VersionInfo latest_version_info = dataSnapshot.getValue(VersionInfo.class);
                check_app_version(latest_version_info);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null) {
//            //chuyen den man hinh dang nhap
//            new Handler().postDelayed(new Runnable() {
//                public void run() {
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, secondsDelayed * 1000);
//
//        }
//        else {
//            if(DBLopHPHelper.getsInstance().isUserLocalDBAvailable()) {
//                //kiem tra co payload tu notification gui toi hay ko
//                Intent splashIntent = getIntent();
//                //Log.d(TAG, "onCreate: Intent = " + splashIntent.toString());
//                if (splashIntent.getExtras() != null && splashIntent.hasExtra("screen")) {
//                    //co lenh chuyen den man hinh screen
//                    String screen = splashIntent.getStringExtra("screen");
//                    if (screen.equals("main")) {
//                        Intent intent = new Intent(this, MainActivity.class);
//                        if (splashIntent.hasExtra("tieu_de"))
//                            intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
//                        else
//                            intent.putExtra("tieu_de", "Null");
//
//                        if (splashIntent.hasExtra("thoi_gian"))
//                            intent.putExtra("thoi_gian", splashIntent.getStringExtra("thoi_gian"));
//                        else
//                            intent.putExtra("thoi_gian", "Null");
//
//                        if (splashIntent.hasExtra("noi_dung"))
//                            intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
//                        else
//                            intent.putExtra("noi_dung", "Null");
//
//                        if (splashIntent.hasExtra("id"))
//                            intent.putExtra("id", splashIntent.getStringExtra("id"));
//                        else
//                            intent.putExtra("id", "Null");
//
//                        if (splashIntent.hasExtra("type"))
//                            intent.putExtra("type", splashIntent.getStringExtra("type"));
//                        else
//                            intent.putExtra("type", "Null");
//                        startActivity(intent);
//                        finish();
//                    }
//                    else if (screen.equals("add_alarm")) {
//                        Intent intent = new Intent(this, AddAlarmActivity.class);
//                        if (splashIntent.hasExtra("tieu_de"))
//                            intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
//                        else
//                            intent.putExtra("tieu_de", "Null");
//
//                        if (splashIntent.hasExtra("noi_dung"))
//                            intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
//                        else
//                            intent.putExtra("noi_dung", "Null");
//                        startActivity(intent);
//                        finish();
//                    }
//                    else {
//                        Intent intent = new Intent(this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//                else {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//            else {
//                Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
    }
}
