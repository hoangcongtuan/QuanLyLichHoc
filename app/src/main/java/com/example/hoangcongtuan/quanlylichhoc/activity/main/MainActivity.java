package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.Alarm.AlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.EditHPActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.SettingsActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.SplashActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.MainPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBChungAdapter;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = MainActivity.class.getName();
    public final static int RC_EDITHPACT = 1;

    MainPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    String[] strTabs;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Uri avatarUrl;
    TextView tvUserName;
    TextView tvEmail;
    TBChungFragment tbChungFragment;
    TBHocPhanFragment tbHPhan;
    LichHocFragment lichHocFragment;
    CoordinatorLayout main_content_layout;

    private DatabaseReference database;
    private DatabaseReference firebaseDBUserMaHP;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //kiem tra trong intent co extras hay ko, neu co -> xu ly payload notification gui toi

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();
        setWidgetsEvent();
    }

    private void init() {
        //init
        strTabs = getResources().getStringArray(R.array.tab_name);
        firebaseAuth = FirebaseAuth.getInstance();
        //config google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        user = firebaseAuth.getCurrentUser();
        avatarUrl = user.getPhotoUrl();

        database = FirebaseDatabase.getInstance().getReference();
        firebaseDBUserMaHP = database.child("userInfo").child(user.getUid()).child("listMaHocPHan");
    }

    private void getWidgets() {
        //getWidgets
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        main_content_layout = (CoordinatorLayout) findViewById(R.id.main_content_layout);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        navigationView = (NavigationView)findViewById(R.id.navigaionView);
        tvUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmail);

        ImageRequest avatarRequest = new ImageRequest(avatarUrl.toString(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageView imageView = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error");
                    }
                }

        );


        Utils.VolleyUtils.getsInstance(getApplicationContext()).getRequestQueue().add(avatarRequest);
    }

    public void getTopicSubcribe(String token, final String key) {
        JsonObjectRequest jsonRequest;
        jsonRequest = new JsonObjectRequest(Request.Method.GET, "https://iid.googleapis.com/iid/info/" + token + "?details=true", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.getJSONObject("rel").getJSONObject("topics").toString());
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: Topic list null");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization","key=" + key);
                return headers;
            }

        };
        Utils.VolleyUtils.getsInstance(this).getRequestQueue().add(jsonRequest);
    }

    private void setWidgets() {
        //setWidgets
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        navigationView.setNavigationItemSelectedListener(this);
        tvUserName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());

    }

    private void setWidgetsEvent() {
        Intent intent = getIntent();
        Log.d(TAG, "onCreate: Intent = " + intent.toString());
        if (intent.getExtras() != null && intent.hasExtra("tieu_de")) {
//            Log.d(TAG, "onCreate: thoi_gian = " + intent.getStringExtra("thoi_gian"));
//            Log.d(TAG, "onCreate: tieu_de = " + intent.getStringExtra("tieu_de"));
//            Log.d(TAG, "onCreate: noi_dung = " + intent.getStringExtra("noi_dung"));
            Log.d(TAG, "setWidgetsEvent: key = " + intent.getStringExtra("id"));
            String tbType = intent.getStringExtra("type");
            if (tbType.compareTo("tbc") == 0) {
                viewPager.setCurrentItem(0);
                tbChungFragment.scrollTo(intent.getStringExtra("id"));
            }

            else {
                viewPager.setCurrentItem(1);
                tbHPhan.scrollTo(intent.getStringExtra("id"));
            }

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        tbChungFragment = new TBChungFragment();
        tbHPhan = new TBHocPhanFragment();
        lichHocFragment = new LichHocFragment();

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(tbChungFragment, strTabs[0]);
        pagerAdapter.addFragment(tbHPhan, strTabs[1]);
        pagerAdapter.addFragment(lichHocFragment, strTabs[2]);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    public void logOut() {

        //unsubscrible all topics
        Utils.QLLHUtils.getsInstance(this).unSubscribeAllTopics(
                DBLopHPHelper.getsInstance().getListUserMaHP()
        );
        Utils.QLLHUtils.getsInstance(this).unSubscribeTopic(
                getResources().getString(R.string.topic_tb_chung)
        );

        FirebaseAuth.getInstance().signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                //Log.d(TAG, "onResult: ");
            }
        });

        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dang_xuat:
                logOut();
                break;
            case R.id.item_xoa_du_lieu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.delete_data));
                builder.setMessage(getResources().getString(R.string.delete_data_detail));
                builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //xoa du lieu tren firebase
                        firebaseDBUserMaHP.setValue(null).addOnSuccessListener(MainActivity.this,
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //xoa du lieu local
                                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();

                                        logOut();
                                        //Log.d(TAG, "onNavigationItemSelected: " + );
                                    }
                                }).addOnFailureListener(MainActivity.this,
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull final Exception e) {
                                        Snackbar.make(main_content_layout, getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                                .setAction(getResources().getString(R.string.details), new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        AlertDialog.Builder alerBulder = new AlertDialog.Builder(MainActivity.this);
                                                        alerBulder.setTitle(getResources().getString(R.string.error));
                                                        alerBulder.setMessage(e.getMessage());
                                                        alerBulder.create().show();
                                                    }
                                                });
                                    }
                                });

                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
            case R.id.item_thay_doi_HP:
                Intent intent = new Intent(MainActivity.this, EditHPActivity.class);
                startActivityForResult(intent, RC_EDITHPACT);
                break;
            case R.id.item_showFCMDetails:
                getTopicSubcribe(
                        FirebaseInstanceId.getInstance().getToken(),
                        getResources().getString(R.string.SERVER_KEY)
                );
                break;
            case R.id.item_showSubscribeTopic:
                break;
            case R.id.item_cat_dat:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.item_nhac_nho:
                Intent intentAlarm = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intentAlarm);
                break;

        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_EDITHPACT:
                if (resultCode == RESULT_OK) {
                    lichHocFragment.updateUI();
                }
                break;
            case RVTBChungAdapter.RC_FAST_ADD_ALARM:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(main_content_layout,
                            getResources().getString(R.string.add_alarm_success),
                            Snackbar.LENGTH_LONG).show();
                }
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //sendNotification();

    }

    public void sendNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setContentTitle("tieu de")
                .setAutoCancel(true)
                .setContentText("noi dung")
                .setSound(notificationSound)
                .setColor(ContextCompat.getColor(this, R.color.colorGreen));
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(SplashActivity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Intent intentAlarm = new Intent(this, SplashActivity.class);

        PendingIntent pIAlarm = PendingIntent.getActivity(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.drawable.ic_alarm_black_24dp, "Nhắc tôi", pIAlarm);


        NotificationManager notifcationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notifcationManager.notify(0, builder.build());
        //Log.d(TAG, "sendNotification: send notification");
    }

}
