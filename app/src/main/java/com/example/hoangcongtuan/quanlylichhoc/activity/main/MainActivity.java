package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.MainPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.listener.HidingScrollListener;
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

    private  final static String TOPIC_TBCHUNG = "TBChung";
    public final static int RC_EDITHPACT = 1;

    private MainPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] strTabs;
    private Toolbar toolbar;
    private LinearLayout appBarLayout;
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
    private int mToolbarHeight;


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
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        tbChungFragment = new TBChungFragment();
        tbHPhanFragment = new TBHocPhanFragment();
        lichHocFragment = new LichHocFragment();

        main_content_layout = (CoordinatorLayout) findViewById(R.id.main_content_layout);

        mToolbarHeight = Utils.getToolbarHeight(this);

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

    private void getTopicSubcribe(String token, final String key) {
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

//        int paddingTop = Utils.getToolbarHeight(MainActivity.this) + Utils.getTabsHeight(MainActivity.this);
//        viewPager.setPadding(viewPager.getPaddingLeft(), paddingTop, viewPager.getPaddingRight(), viewPager.getPaddingBottom());


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

        final HidingScrollListener hidingScrollListener_tbChung = new HidingScrollListener(MainActivity.this) {
            @Override
            public void onHide() {
                appBarLayout.animate().translationY(-mToolbarHeight).setInterpolator(new DecelerateInterpolator()).start();
                //getSupportActionBar().hide();
            }

            @Override
            public void onMoved(int distance) {
                appBarLayout.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                //getSupportActionBar().show();
            }
        };

        HidingScrollListener hidingScrollListener_tbHPhan = new HidingScrollListener(MainActivity.this) {
            @Override
            public void onHide() {
                appBarLayout.animate().translationY(-mToolbarHeight).setInterpolator(new DecelerateInterpolator()).start();
                //getSupportActionBar().hide();
            }

            @Override
            public void onMoved(int distance) {
                appBarLayout.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                //getSupportActionBar().show();
            }
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //show toolbar when move to new page
                hidingScrollListener_tbChung.showToolbar();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tbChungFragment.setOnHidingScrollListener(hidingScrollListener_tbChung);

        tbHPhanFragment.setOnHidingScrollListener(hidingScrollListener_tbHPhan);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(tbChungFragment, strTabs[0]);
        pagerAdapter.addFragment(tbHPhanFragment, strTabs[1]);
        pagerAdapter.addFragment(lichHocFragment, strTabs[2]);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void hideViews() {
        appBarLayout.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private void logOut() {

        //unsubscrible all topics
        Utils.QLLHUtils.getsInstance(this).unSubscribeAllTopics(
                DBLopHPHelper.getsInstance().getListUserMaHP()
        );
        Utils.QLLHUtils.getsInstance(this).unSubscribeTopic(
                TOPIC_TBCHUNG
        );

        FirebaseAuth.getInstance().signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
            }
        });

        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "Close Search View", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "On click search", Toast.LENGTH_SHORT).show();
                tbChungFragment.scrollTo("5896914293f0446f65b391e51d0cf4e0");
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
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
                startActivityForResult(intent, RC_EDITHPACT);
                break;
            case R.id.item_showFCMDetails:
                getTopicSubcribe(
                        FirebaseInstanceId.getInstance().getToken(),
                        getResources().getString(R.string.SERVER_KEY)
                );
                break;
            case R.id.item_showSubscribeTopic:
                tbHPhanFragment.scrollTo("c27bd18081c2c847b0ba3dd25c637779");
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
    protected void onStart() {
        super.onStart();
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
//        viewPager.setCurrentItem(0);
//                tbChungFragment.scrollTo("8a31f36bfdaaf8d590d2a705cb2bd728");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_EDITHPACT:
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
