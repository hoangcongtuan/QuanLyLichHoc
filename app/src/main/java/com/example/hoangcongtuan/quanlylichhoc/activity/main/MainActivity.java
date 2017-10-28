package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.EditHPActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.MainPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = MainActivity.class.getName();

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
    TBChung tbChung;
    TBHocPhan tbHPhan;
    LichHocFragment lichHocFragment;

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

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        navigationView = (NavigationView)findViewById(R.id.navigaionView);
        tvUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tvUserName);

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
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        navigationView.setNavigationItemSelectedListener(this);
        tvUserName.setText(user.getDisplayName());

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
                tbChung.scrollTo(intent.getStringExtra("id"));
            }

            else {
                viewPager.setCurrentItem(1);
                tbHPhan.scrollTo(intent.getStringExtra("id"));
            }

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        tbChung = new TBChung();
        tbHPhan = new TBHocPhan();
        lichHocFragment = new LichHocFragment();

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(tbChung, strTabs[0]);
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
        Utils.QLLHUtils.getsInstance(this).unSubscribeTopic("TBChung");

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
                builder.setTitle("Ban co chac muon xoa khong?");
                builder.setPositiveButton("Xoa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //xoa du lieu local
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                        //xoa du lieu tren firebase
                        firebaseDBUserMaHP.setValue(null);
                        logOut();
                        //Log.d(TAG, "onNavigationItemSelected: " + );
                    }
                });

                builder.setNegativeButton("Huy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
            case R.id.item_thay_doi_HP:
                Intent intent = new Intent(MainActivity.this, EditHPActivity.class);
                startActivity(intent);
                break;
            case R.id.item_showFCMDetails:
                getTopicSubcribe(
                        getResources().getString(R.string.FCM_TOKEN),
                        getResources().getString(R.string.SERVER_KEY)
                );
                break;
            case R.id.item_showSubscribeTopic:

        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
