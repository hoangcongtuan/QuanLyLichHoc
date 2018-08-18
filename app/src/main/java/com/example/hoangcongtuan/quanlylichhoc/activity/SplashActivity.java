package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.alarm.AddAlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.VersionInfo;
import com.example.hoangcongtuan.quanlylichhoc.helper.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Kiem tra trang thai dang nhap, neu da dang nhap thi toi man hinh chinh, con ko thi toi man hinh dang nhap
 */

public class SplashActivity extends BaseActivity {
    private final static String TAG = SplashActivity.class.getName();
    private final static String KEY_ALL_HP_DB_VERSION = "ALL_HP_DATABASE_VERSION";
    private final static String KEY_VERSION = "version_info";

    private CoordinatorLayout layout_splash;
    private TextView tvLoadingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        layout_splash = findViewById(R.id.layout_finish);
        tvLoadingInfo = findViewById(R.id.tvLoadingInfo);

        DBLopHPHelper.init(getApplicationContext());
        createNotificationChannel();

        //check internet
        if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
            //kiem tra phien ban phan mem
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref_version = databaseReference.child(KEY_VERSION);

            ref_version.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    VersionInfo latest_version_info = dataSnapshot.getValue(VersionInfo.class);
                    check_app_version(latest_version_info);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error = " + databaseError.getDetails());
                    Crashlytics.log(Log.ERROR, TAG, "version info is null");
                }
            });
        }
        else
            showNoInternetAlert();
    }

    /**
     * Check App version, if is not latest version, require up date, if not, check database version
     * @param latest_version version info from firebase database
     */
    private void check_app_version(@NonNull final VersionInfo latest_version) {
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
            Crashlytics.log(Log.ERROR, TAG, "Need update, current version is = " + current_app_version);

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

    /**
     * Check class database version, if is not latest -> update, if not -> check login
     * @param versionInfo database version, get from firebase database
     */
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
        } else if (current_all_hp_db_version.equals(latest_all_hp_db_version)) {
            Log.d(TAG, "check_database_version: all hp db is latest");
            //dang la moi nhat
            //kiem tra dang nhap
            check_login(true);
        } else {
            //cap nhat db
            Log.d(TAG, "check_database_version: update all hp db");
            download_all_hp_database(versionInfo);
        }
    }

    /**
     * Check login status, if is login -> check intent extras and navigate to proper activity
     * If not login -> goto login activity
     * @param isInternetAvailable internet status
     */
    public void check_login(boolean isInternetAvailable) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (!isInternetAvailable) {
                Intent splashIntent = getIntent();
                if (splashIntent.getExtras() != null && splashIntent.hasExtra("screen")) {
                    String screen = splashIntent.getStringExtra("screen");
                    switch (screen) {
                        case "main":
                            Intent main_intent = new Intent(SplashActivity.this, MainActivity.class);

//                            if (splashIntent.hasExtra("tieu_de"))
//                                main_intent.putExtra("tieu_de", splashIntent.getStringExtra("tieu_de"));
//                            else
//                                main_intent.putExtra("tieu_de", "Null");
//
//                            if (splashIntent.hasExtra("thoi_gian"))
//                                main_intent.putExtra("thoi_gian", splashIntent.getStringExtra("thoi_gian"));
//                            else
//                                main_intent.putExtra("thoi_gian", "Null");
//
//                            if (splashIntent.hasExtra("noi_dung"))
//                                main_intent.putExtra("noi_dung", splashIntent.getStringExtra("noi_dung"));
//                            else
//                                main_intent.putExtra("noi_dung", "Null");

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
                            break;
                        case "add_alarm":
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
                            break;
                        default:
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            /* else, internet available
            tai du lieu user ve */
            tvLoadingInfo.setText(
                    getResources().getString(R.string.sync_user_db)
            );

            DatabaseReference firebaseDBUserMaHP = FirebaseDatabase.getInstance().getReference()
                    .child(LoginActivity.KEY_FIRBASE_USER)
                    .child(user.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);

            firebaseDBUserMaHP.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //da thiet lap lop hoc phan
                        if (dataSnapshot.getChildrenCount() != 0) {
                            //co du lieu trong do
                            //xoa old db
                            DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                            //save Firebase DB to local DB

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String id = (String)snapshot.getValue();
                                //change _ to .
                                id = Utils.getsInstance(SplashActivity.this).underLineToDot(id);
                                DBLopHPHelper.getsInstance().insertUserMaHocPhan(id);
                            }

                            final ArrayList<String> list_topic = Utils.getsInstance(SplashActivity.this).dotToUnderLine(
                                    DBLopHPHelper.getsInstance().getListUserMaHP()
                            );

                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String token = instanceIdResult.getToken();

                                            syncTopic(token,
                                                    getResources().getString(R.string.SERVER_KEY), list_topic);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Crashlytics.log(Log.ERROR, TAG, "Failer to get token");
                                        }
                                    });
                        }
                        else {
                            //chua co du lieu lop hoc phan
                            //di toi man hinh setup
                            Log.d(TAG, "onDataChange: no user data, let setup your data");
                            Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        //di toi man hinh setup
                        Log.d(TAG, "onDataChange: no user data, let setup your data");
                        Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull final DatabaseError databaseError) {
                    Crashlytics.log(Log.ERROR, TAG, "Cancelled get user db on firebase");
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
            SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a", Locale.US);
            String latest_online = sdfDate.format(Calendar.getInstance().getTime());
            DatabaseReference ref_user_online = FirebaseDatabase.getInstance().getReference()
                    .child(LoginActivity.KEY_FIRBASE_USER).child(user.getUid())
                    .child(LoginActivity.KEY_FIREBASE_USERINFO).child(LoginActivity.KEY_FIREBASE_USER_LATEST_ONLINE);
            ref_user_online.setValue(latest_online);
        }
    }

    /**
     * Create notification channel for android >= 26
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = getResources().getString(R.string.APP_CHANNEL_ID);
            String channel_name = getResources().getString(R.string.APP_CHANNEL_NAME);
            String channel_description = getResources().getString(R.string.APP_CHANNEL_DESCRIPTION);

            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, important);
            channel.setDescription(channel_description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
            else {
                Log.d(TAG, "createNotificationChannel: Failed to get Notificatoin manager");
                //TODO: Error handle here
                Crashlytics.log(Log.ERROR, TAG, "Cannot create notification channel");
            }
        }
    }

    /**
     * Navigate to Main Activity or Create Alarm acitivity depend on Extras data
     */
    private void check_intent() {
        //check intent
        Intent splashIntent = getIntent();
        if (splashIntent.getExtras() != null && splashIntent.hasExtra("screen")) {
            String screen = splashIntent.getStringExtra("screen");
            switch (screen) {
                case "main":
                    Intent main_intent = new Intent(SplashActivity.this, MainActivity.class);

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
                    break;
                case "add_alarm":
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
                    break;
                default:
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
        else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Synchornize Subscribe to topic base on lst_user_hp
     * Remove unsubscribe what topic that not exist in lst_user_hp
     * Subscribe topic in lst_user_hp
     * @param token FCM Token
     * @param key   FCM Key server
     * @param lst_user_hp List user class
     */
    public void syncTopic(String token, final String key, final ArrayList<String> lst_user_hp) {
        JsonObjectRequest jsonRequest;

        jsonRequest = new JsonObjectRequest(
                Request.Method.GET, " https://iid.googleapis.com/iid/info/" + token + "?details=true", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        JSONObject json_topics = null;
                        try {
                            json_topics = new JSONObject("{}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //TODO: Error here
                            Crashlytics.log(Log.ERROR, TAG, "Error when create null JSON Object");
                            Toast.makeText(SplashActivity.this, R.string.error_sync_topic, Toast.LENGTH_LONG).show();
                            check_intent();
                        }
                        try {
                            json_topics = response.getJSONObject("rel").getJSONObject("topics");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.log(Log.ERROR, TAG, "Error when get topics JSON");
                        }
                        Log.d(TAG, "onResponse: " + json_topics.toString());
                        Iterator<String> keys = json_topics.keys();
                        ArrayList<String> sub_list = new ArrayList<>();
                        String key;

                        //remove unsubscribe topic
                        while(keys.hasNext()) {
                            key = keys.next();
                            sub_list.add(key);
                            if (lst_user_hp.indexOf(key.trim()) == -1 && (!key.equals(LoginActivity.TOPIC_TBCHUNG))) {
                                //user is not subscribe to this topic, unsub it
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                                Log.d(TAG, "onResponse: Unsubscribe topic = " + key);
                            }
                            Log.d(TAG, "onResponse: " + key);
                        }

                        //check TBChung is subscribe ?
                        if (sub_list.indexOf(LoginActivity.TOPIC_TBCHUNG) == -1) {
                            //TBChung was not subscribe yet!, subscribe it now
                            FirebaseMessaging.getInstance().subscribeToTopic(LoginActivity.TOPIC_TBCHUNG);
                            Log.d(TAG, "onResponse: Subscribe topic = " + LoginActivity.TOPIC_TBCHUNG);
                        }

                        //subscribe topic
                        for(String str: lst_user_hp) {
                            if (sub_list.indexOf(str) == -1) {
                                //this topics was not subscribed yet, subscribe it now
                                FirebaseMessaging.getInstance().subscribeToTopic(str);
                            }
                        }
                        //sync topic complete

                        //TODO: action after sync topic
                        check_intent();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: need error handle here
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());

                        Crashlytics.log(Log.ERROR, TAG, "Error when get FCM Info, network ping is " + error.getNetworkTimeMs());
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=" + key);
                return headers;
            }
        };

        Utils.getsInstance(this).getRequestQueue().add(jsonRequest);
    }

    /**
     * Download all class data from firebase and store in SQL Database
     * @param versionInfo Class database version
     */
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
                editor.apply();

                //check login
                check_login(true);
            }

            @Override
            public void onStartDownload() {

            }
        });

        DBLopHPHelper.getsInstance().download_all_hp_database();
    }

    /**
     * Show no intenet Alert
     */
    private void showNoInternetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.no_internet_connection)
                .setMessage(R.string.need_internet_for_posts)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        check_login(false);
                    }
                });
        builder.create().show();
    }
}
