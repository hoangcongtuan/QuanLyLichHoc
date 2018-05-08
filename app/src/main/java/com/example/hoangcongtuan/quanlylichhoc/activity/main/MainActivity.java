package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.hoangcongtuan.quanlylichhoc.activity.alarm.AlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.EditHPActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.SearchResultActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.MainPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = MainActivity.class.getName();

    public final static String FIND_URL = "https://us-central1-server-dut.cloudfunctions.net/searchPost?category=%s&text=%s";
    public final static String CATE_CHUNG = "chung";
    public final static String CATE_HOC_PHAN = "hocphan";

    public final static int RC_EDIT_HP_ACT = 1;

    public final static int PAGE_TB_CHUNG = 0;
    public final static int PAGE_TB_HP = 1;
    public final static int PAGE_TKB = 2;

    private MainPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] strTabs;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Uri avatarUrl;
    private TextView tvUserName;
    private TextView tvEmail;
    private TBChungFragment tbChungFragment;
    private TBHocPhanFragment tbHPhanFragment;
    private LichHocFragment lichHocFragment;
    private CoordinatorLayout main_content_layout;

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
        firebaseDBUserMaHP = database.child(LoginActivity.KEY_FIRBASE_USER)
                .child(user.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);
    }


    private void getWidgets() {
        //getWidgets
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPager);

        tbChungFragment = new TBChungFragment();
        tbHPhanFragment = new TBHocPhanFragment();
        lichHocFragment = new LichHocFragment();

        main_content_layout = findViewById(R.id.main_content_layout);

        tabLayout = findViewById(R.id.tabs);
        navigationView = findViewById(R.id.navigaionView);
        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        tvEmail =  navigationView.getHeaderView(0).findViewById(R.id.tvEmail);

        ImageRequest avatarRequest = new ImageRequest(avatarUrl.toString(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0,
                ImageView.ScaleType.FIT_CENTER,
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

    private void searchPost(String text) {
        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
        switch (viewPager.getCurrentItem()) {
            case PAGE_TB_CHUNG:
                //pass param and start search activity
                intent.putExtra("CATEGORY", PAGE_TB_CHUNG);
                break;
            case PAGE_TB_HP:
                intent.putExtra("CATEGORY", PAGE_TB_HP);
                break;
            case PAGE_TKB:
                return;
                //intent.putExtra("CATEGORY", PAGE_TKB);
                //break;
        }
        intent.putExtra("TEXT", text);
        startActivity(intent);
    }

    private void getTopicSubcribe(String token, final String key) {
        final JsonObjectRequest jsonRequest;
        jsonRequest = new JsonObjectRequest(
                Request.Method.GET, " https://iid.googleapis.com/iid/info/" + token + "?details=true", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=" + key);
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

    }


    private void setupViewPager(ViewPager viewPager) {

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(tbChungFragment, strTabs[0]);
        pagerAdapter.addFragment(tbHPhanFragment, strTabs[1]);
        pagerAdapter.addFragment(lichHocFragment, strTabs[2]);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void logOut() {

        ArrayList<String> list_topic = DBLopHPHelper.getsInstance().getListUserMaHP();
        //unsubscrible all topics
        Utils.QLLHUtils.getsInstance(this).unSubscribeAllTopics(
                list_topic
        );
        Utils.QLLHUtils.getsInstance(this).unSubscribeTopic(
                LoginActivity.TOPIC_TBCHUNG
        );

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });


        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "onMenuItemActionExpand: ");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "onMenuItemActionCollapse: ");
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                //Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                searchPost(query);
                searchItem.collapseActionView();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: ");
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return true;
            }
        });
        return true;
    }


    public void showDeleteUserDBDialog()  {
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
                                //unsbuscribe all topic
                                ArrayList<String> list_topic = DBLopHPHelper.getsInstance().getListUserMaHP();

                                Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeAllTopics(list_topic);
                                Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeTopic(LoginActivity.TOPIC_TBCHUNG);

                                DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                                logOut();
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
                                        })
                                        .show();
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dang_xuat:
                logOut();
                break;
            case R.id.item_xoa_du_lieu:
                if (Utils.InternetUitls.getsInstance(getApplicationContext()).isNetworkConnected()) {
                    showDeleteUserDBDialog();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.no_internet));
                    builder.setMessage(getResources().getString(R.string.no_internet_msg));
                    builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();
                }

                break;
            case R.id.item_thay_doi_HP:
                Intent intent = new Intent(MainActivity.this, EditHPActivity.class);
                startActivityForResult(intent, RC_EDIT_HP_ACT);
                break;
            case R.id.item_showFCMDetails:
                getTopicSubcribe(
                        FirebaseInstanceId.getInstance().getToken(),
                        getResources().getString(R.string.SERVER_KEY)
                );

//                Log.d(TAG, "onNavigationItemSelected: Token = " + FirebaseInstanceId.getInstance().getToken());
//                Log.d(TAG, "onNavigationItemSelected: Token ID = " + FirebaseInstanceId.getInstance().getId());
//                Log.d(TAG, "onNavigationItemSelected: Token ID time creation = " + FirebaseInstanceId.getInstance().getCreationTime());
                break;
            case R.id.item_showSubscribeTopic:
                searchPost("Thông báo chuyển phòng học khu B");
                break;
//            case R.id.item_cat_dat:
//                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intentSettings);
//                break;
            case R.id.item_nhac_nho:
                Intent intentAlarm = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intentAlarm);
                break;

        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Utils.InternetUitls.getsInstance(getApplicationContext()).isNetworkConnected()) {
            tbChungFragment.hide_empty_state();
            tbHPhanFragment.hide_empty_state();
            Intent intent = getIntent();
            Log.d(TAG, "onCreate: Intent = " + intent.toString());
            if (intent.getExtras() != null && intent.hasExtra("tieu_de")) {
                Log.d(TAG, "setWidgetsEvent: key = " + intent.getStringExtra("id"));
                String tbType = intent.getStringExtra("type");
                if (tbType.compareTo("tbc") == 0) {
                    viewPager.setCurrentItem(0);
                    tbChungFragment.scrollTo(intent.getStringExtra("id"));
                }
                else {
                    viewPager.setCurrentItem(1);
                    tbHPhanFragment.scrollTo(intent.getStringExtra("id"));
                }
            }
        }
        else {
            tbChungFragment.show_empty_state();
            tbHPhanFragment.show_empty_state();
        }


//        viewPager.setCurrentItem(0);
//                tbChungFragment.scrollTo("8a31f36bfdaaf8d590d2a705cb2bd728");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_EDIT_HP_ACT:
                if (resultCode == RESULT_OK) {
                    lichHocFragment.updateUI();
                }
                break;
            case RVTBAdapter.RC_FAST_ADD_ALARM:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(main_content_layout,
                            getResources().getString(R.string.add_alarm_success),
                            Snackbar.LENGTH_LONG).show();
                }
                break;

        }
    }
}
