package com.example.hoangcongtuan.quanlylichhoc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.hoangcongtuan.quanlylichhoc.adapter.StepPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomViewPager;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{
    private final static String TAG = SetupActivity.class.getName();
    private CustomViewPager viewPager;
    private Button btnBack;
    private Button btnNext;
    private Button btnFinish;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private StepPagerAdapter stepperPagerAdapter;
    private TextView tvStep1, tvStep2, tvStep3, tvStep1Label, tvStep2Label, tvStep3Label;
    private int currentStep;
    private WelcomeFragment welcomeFragment;
    private RecognizeFragment recognizeFragment;
    private FinishFragment finishFragment;
    private PrepareFragment prepareFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private GoogleApiClient mGoogleApiClient;


    private final static int STEP_PREPARE = 0;
    private final static int STEP_GET_IMAGE = 1;
    private final static int STEP_RECOGNIZE = 2;
    private final static int STEP_FINISH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        viewPager = (CustomViewPager) findViewById(R.id.viewPagerSetup);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnFinish = (Button)findViewById(R.id.btnFinish);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_setup);
        navigationView = (NavigationView)findViewById(R.id.setup_navigation);

        tvStep1 = (TextView)findViewById(R.id.tvStep1);
        tvStep2 = (TextView)findViewById(R.id.tvStep2);
        tvStep3 = (TextView)findViewById(R.id.tvStep3);

        tvStep1Label = (TextView)findViewById(R.id.tvStep1Label);
        tvStep2Label = (TextView)findViewById(R.id.tvStep2Label);
        tvStep3Label = (TextView)findViewById(R.id.tvStep3Label);

        stepperPagerAdapter = new StepPagerAdapter(getSupportFragmentManager());
        welcomeFragment = new WelcomeFragment();
        welcomeFragment.setWelcomeFragInterface(new WelcomeFragment.WelcomeFragInterface() {
            @Override
            public void onBitmapAvailable() {
                btnNext.setEnabled(true);
            }
        });
        recognizeFragment = new RecognizeFragment();
        prepareFragment = new PrepareFragment();

        prepareFragment.setPrepareFinish(new PrepareFragment.PrepareFinish() {
            @Override
            public void onPrepareFinish() {
//                Intent intent = new Intent(SetupActivity.this, SpinnerDemo.class);
//                startActivity(intent);

                currentStep = STEP_GET_IMAGE;
                setStepper(currentStep);
            }
        });

        finishFragment = new FinishFragment();

        stepperPagerAdapter.addFragment(prepareFragment, "prepare");
        stepperPagerAdapter.addFragment(welcomeFragment, "welcome");
        stepperPagerAdapter.addFragment(recognizeFragment, "Recognize");
        stepperPagerAdapter.addFragment(finishFragment, "finish");
        viewPager.setAdapter(stepperPagerAdapter);
        viewPager.setPagingEnable(false);

        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                switch (position) {
                    case STEP_PREPARE:
                        btnNext.setEnabled(false);
                        btnBack.setEnabled(false);
                        break;
                    case STEP_GET_IMAGE:
                        btnBack.setEnabled(false);
                        if(welcomeFragment.bitmap != null)
                            btnNext.setEnabled(true);
                        else
                            btnNext.setEnabled(false);
                        break;
                    case STEP_RECOGNIZE:
                        btnNext.setEnabled(true);
                        btnBack.setEnabled(true);

                        btnNext.setVisibility(View.VISIBLE);
                        btnFinish.setVisibility(View.INVISIBLE);
                        break;
                    case STEP_FINISH:

                        btnFinish.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        //currentStep = STEP_GET_IMAGE;
        currentStep = STEP_PREPARE;
        setStepper(currentStep);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final Uri imageUrl = firebaseUser.getPhotoUrl();
        String userName = firebaseUser.getDisplayName();

        TextView tvUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        final ImageView imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);

        tvUserName.setText(userName);

        ImageRequest imageRequest = new ImageRequest(
                imageUrl.toString(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgAvatar.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                    }
                }
        );

        Utils.VolleyUtils.getsInstance(getApplicationContext()).getRequestQueue().add(imageRequest);
       // DBLopHPHelper.getsInstance(this).checkDB();
    }


    public void setStepper(int stepId) {
        switch (stepId) {
            case STEP_PREPARE:
                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                btnNext.setEnabled(false);
                btnBack.setEnabled(false);

                break;
            case STEP_GET_IMAGE:
                tvStep1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep1Label.setTextColor(Color.WHITE);
                tvStep1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle_white));


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));


                break;
            case STEP_RECOGNIZE:

                tvStep2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep2Label.setTextColor(Color.WHITE);
                tvStep2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle_white));


                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                recognizeFragment.setBitmap(
                        welcomeFragment.bitmap
                );
                recognizeFragment.recognize();

                break;
            case STEP_FINISH:
                tvStep3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep3Label.setTextColor(Color.WHITE);
                tvStep3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle_white));


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_circle));

                finishFragment.processTKB(recognizeFragment.lstMaHP);
                break;
        }
        Log.d(TAG, "setStepper: " + stepId);
        viewPager.setCurrentItem(stepId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                if (currentStep > STEP_GET_IMAGE)
                    currentStep--;
                setStepper(currentStep);
                break;
            case R.id.btnNext:
                if(currentStep < STEP_FINISH){
                    currentStep++;
                    setStepper(currentStep);
                }
                break;
            case R.id.btnFinish:
                finishFragment.writelstMaHPtoUserDB();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dang_xuat:
                Log.d(TAG, "onNavigationItemSelected: ");
                FirebaseAuth.getInstance().signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "onResult: ");
                    }
                });

                LoginManager.getInstance().logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
