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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.about.AboutActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.alarm.AlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.EditHPActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.SearchResultActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.MainPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVPostAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.ProgressDialogBuilderCustom;
import com.example.hoangcongtuan.quanlylichhoc.helper.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyUtils;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private final static String TAG = MainActivity.class.getName();

    public final static String FIND_URL = "https://us-central1-server-dut.cloudfunctions.net/searchPost?category=%s&text=%s";
    public final static String CATE_CHUNG = "chung";
    public final static String CATE_HOC_PHAN = "hocphan";
    public final static int RC_EDIT_HP_ACT = 1;
    public final static int PAGE_TB_CHUNG = 0;
    public final static int PAGE_TB_HP = 1;
    public final static int PAGE_TKB = 2;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] strTabs;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseUser user;
    private Uri avatarUrl;
    private TextView tvUserName;
    private TextView tvEmail;
    private TBChungFragment tbChungFragment;
    private TBHocPhanFragment tbHPhanFragment;
    private LichHocFragment lichHocFragment;
    private CoordinatorLayout main_content_layout;
    private AlertDialog pr_dialog;

    private DatabaseReference firebaseDBUserMaHP;

    private GoogleApiClient mGoogleApiClient;

    /**
     * Init view
     * check Intent extras, if has Post ID, scroll to ID's Post
     * @param savedInstanceState saved bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();
        setWidgetsEvent();

        //check intent
        if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
            tbChungFragment.hide_empty_state();
            tbHPhanFragment.hide_empty_state();
            Intent intent = getIntent();
            if (intent.getExtras() != null && intent.hasExtra("type")) {
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

        //test callable functions
        Utils.getsInstance(this).searchPost("chung", "google").addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: success = " + task.getResult());
                }
                else {
                    Log.d(TAG, "onComplete: Error");
                }
            }
        });

    }

    //callable function
    private Task<String> echoCallable(String text) {
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);

        return FirebaseFunctions.getInstance().getHttpsCallable("echoCallable")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        return (String)result.get("TextReturn");
                    }
                });
    }

    /**
     * Init Some value
     */
    private void init() {
        //init
        strTabs = getResources().getStringArray(R.array.tab_name);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
        avatarUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        Log.d(TAG, "init: avatarurl = " + avatarUrl);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        firebaseDBUserMaHP = database.child(LoginActivity.KEY_FIRBASE_USER)
                .child(user.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);

        //create progress dialog
        ProgressDialogBuilderCustom progressDialogBuilderCustom = new ProgressDialogBuilderCustom(this);
        progressDialogBuilderCustom.setText(R.string.processing);

        pr_dialog = progressDialogBuilderCustom.create();
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

        Utils.getsInstance(getApplicationContext()).getRequestQueue().add(avatarRequest);
    }

    /**
     * Search post function with "text" keyword, pass thi keyword to Search Activity and Category info
     * @param text keyword
     */
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
        }
        intent.putExtra("TEXT", text);
        startActivity(intent);
    }

    /**
     * Get topic subscribed and log it out
     * @param token FCM Token
     * @param key FCM Server key
     */
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=" + key);
                return headers;
            }

        };
        Utils.getsInstance(this).getRequestQueue().add(jsonRequest);
    }

    private void setWidgets() {
        //setWidgets
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        navigationView.setNavigationItemSelectedListener(this);
        tvUserName.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());

        //apply font for tablayout
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for(int i = 0; i < tabsCount; i++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            int tabChildsCount = vgTab.getChildCount();
            for(int j = 0; j < tabChildsCount; j++) {
                View tabViewChild = vgTab.getChildAt(j);
                if (tabViewChild instanceof TextView)
                    CalligraphyUtils.applyFontToTextView(tabViewChild.getContext(), (TextView)tabViewChild, Utils.APP_FONT_PATH);
            }
        }

    }

    private void setWidgetsEvent() {

    }

    private void setupViewPager(ViewPager viewPager) {

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(tbChungFragment, strTabs[0]);
        pagerAdapter.addFragment(tbHPhanFragment, strTabs[1]);
        pagerAdapter.addFragment(lichHocFragment, strTabs[2]);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    /**
     * UnSubscribe all topic and logout
     */
    private void logOut() {

        ArrayList<String> list_topic = Utils.getsInstance(MainActivity.this).dotToUnderLine(
                DBLopHPHelper.getsInstance().getListUserMaHP()
        );
        //unsubscrible all topics
        Utils.getsInstance(this).unSubscribeAllTopics(
                list_topic
        );
        Utils.getsInstance(this).unSubscribeTopic(
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
        builder.setTitle(getResources().getString(R.string.delete_user_class));
        builder.setMessage(getResources().getString(R.string.delete_data_detail));
        builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //show progress dialog
                pr_dialog.show();

                //xoa du lieu tren firebase
                firebaseDBUserMaHP.setValue(null).addOnSuccessListener(MainActivity.this,
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //xoa du lieu local
                                //unsbuscribe all topic
                                ArrayList<String> list_topic = Utils.getsInstance(MainActivity.this).dotToUnderLine(
                                        DBLopHPHelper.getsInstance().getListUserMaHP()
                                );

                                Utils.getsInstance(getApplicationContext()).unSubscribeAllTopics(list_topic);
                                Utils.getsInstance(getApplicationContext()).unSubscribeTopic(LoginActivity.TOPIC_TBCHUNG);

                                DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                                pr_dialog.dismiss();
                                logOut();
                            }
                        }).addOnFailureListener(MainActivity.this,
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull final Exception e) {
                                pr_dialog.dismiss();
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
                if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
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
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        getTopicSubcribe(
                                instanceIdResult.getToken(),
                                getResources().getString(R.string.SERVER_KEY)
                        );
                    }
                });
                break;
//
//            case R.id.item_showSubscribeTopic:
//                searchPost("Thông báo chuyển phòng học khu B");
//                break;

            case R.id.item_about:
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
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
            case RC_EDIT_HP_ACT:
                if (resultCode == RESULT_OK) {
                    lichHocFragment.updateUI();
                }
                break;
            case RVPostAdapter.RC_FAST_ADD_ALARM:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(main_content_layout,
                            getResources().getString(R.string.add_alarm_success),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}
