package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.StepPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.NoSwipeCustomViewPager;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{
    private final static String TAG = SetupActivity.class.getName();
    private NoSwipeCustomViewPager viewPager;
    private Button btnBack;
    private Button btnNext;
    private Button btnFinish;
    private FloatingActionButton fabAdd;
    private CoordinatorLayout layout_setup;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private StepPagerAdapter stepperPagerAdapter;
    private TextView tvStep1, tvStep2, tvStep3, tvStep1Label, tvStep2Label, tvStep3Label;
    private int currentStep;
    private GetImageFragment getImageFragment;
    private RecognizeFragment recognizeFragment;
    private FinishFragment finishFragment;
    private PrepareFragment prepareFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private DatabaseReference database;
    private DatabaseReference dbUserMaHocPhan;

    private Uri avatarUrl;
    private String userName;

    private TextView tvUserName;
    private ImageView imgAvatar;

    private GoogleApiClient mGoogleApiClient;

    private boolean manuallyMode = false;


    private final static int STEP_PREPARE = 0;
    private final static int STEP_GET_IMAGE = 1;
    private final static int STEP_RECOGNIZE = 2;
    private final static int STEP_FINISH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();
    }

    private void init() {
        //init
        stepperPagerAdapter = new StepPagerAdapter(getSupportFragmentManager());
        getImageFragment = new GetImageFragment();
        recognizeFragment = new RecognizeFragment();
        prepareFragment = new PrepareFragment();
        finishFragment = new FinishFragment();

        //init google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        //init stepper
        currentStep = STEP_PREPARE;

        //get firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        avatarUrl = firebaseUser.getPhotoUrl();
        userName = firebaseUser.getDisplayName();

        database = FirebaseDatabase.getInstance().getReference();
        dbUserMaHocPhan = database.child(LoginActivity.KEY_FIRBASE_USER).child(firebaseUser.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);
    }

    private void getWidgets() {
        //getWidgets
        viewPager = (NoSwipeCustomViewPager) findViewById(R.id.viewPagerSetup);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnFinish = (Button)findViewById(R.id.btnFinish);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        fabAdd = findViewById(R.id.fabAdd);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_setup);
        navigationView = (NavigationView)findViewById(R.id.setup_navigation);
        layout_setup = findViewById(R.id.layout_setup);
        tvStep1 = (TextView)findViewById(R.id.tvStep1);
        tvStep2 = (TextView)findViewById(R.id.tvStep2);
        tvStep3 = (TextView)findViewById(R.id.tvStep3);

        tvStep1Label = (TextView)findViewById(R.id.tvStep1Label);
        tvStep2Label = (TextView)findViewById(R.id.tvStep2Label);
        tvStep3Label = (TextView)findViewById(R.id.tvStep3Label);

        tvUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
    }

    private void setWidgets() {
        //setWidgets
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        stepperPagerAdapter.addFragment(prepareFragment, "prepare");
        stepperPagerAdapter.addFragment(getImageFragment, "welcome");
        stepperPagerAdapter.addFragment(recognizeFragment, "Recognize");
        stepperPagerAdapter.addFragment(finishFragment, "finish");
        viewPager.setAdapter(stepperPagerAdapter);
        ///viewPager.setPagingEnable(false);
        viewPager.setOffscreenPageLimit(4);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        tvUserName.setText(userName);
        setStepper(currentStep);

        final FabVisibilityChangedListener fabVisibilityChangedListener = new FabVisibilityChangedListener();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == STEP_PREPARE || position == STEP_GET_IMAGE)
                    fabAdd.hide();
                else {
                    if (fabAdd.isShown()) {
                        fabVisibilityChangedListener.position = position;
                        fabAdd.hide(fabVisibilityChangedListener);
                    } else {
                        changeFabState(position);
                        fabAdd.show();
                    }
                }
            }
        });
    }

    private void setWidgetsEvent() {
        //setWidgetsEvent
        getImageFragment.setCallBack(new GetImageFragment.CallBack() {
            @Override
            public void onBitmapAvailable() {
                btnNext.setEnabled(true);
                manuallyMode = false;
            }

            @Override
            public void onAddManually() {
                manuallyMode = true;
            }
        });

        prepareFragment.setPrepareFinish(new PrepareFragment.PrepareFinish() {
            @Override
            public void onPrepareFinish() {
                currentStep = STEP_GET_IMAGE;
                setStepper(currentStep);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case STEP_PREPARE:
                        btnNext.setEnabled(false);
                        btnBack.setEnabled(false);
                        break;
                    case STEP_GET_IMAGE:
                        btnBack.setEnabled(false);
                        if(getImageFragment.bitmap != null)
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
        });

        viewPager.setOffscreenPageLimit(4);

        navigationView.setNavigationItemSelectedListener(this);

        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        ImageRequest imageRequest = new ImageRequest(
                avatarUrl.toString(),
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

        Utils.getsInstance(getApplicationContext()).getRequestQueue().add(imageRequest);
    }


    private void checkFireBaseUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        /*:TODO bỏ cái chữ "userInfo", "listMaHocPHan" vô chỗ mô đó để lát sử dụng lưu.
         *  mấy cái chữ khác nữa,
         */
        dbUserMaHocPhan = database.child("userInfo").child(firebaseUser.getUid()).child("listMaHocPHan");
        dbUserMaHocPhan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DBLopHPHelper.getsInstance().insertUserMaHocPhan((String) snapshot.getValue());
                    }

                    //:TODO CÁi này trùng vs bên dưới nên tạo 1 hàm ra
                    Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SetupActivity.this, "không có dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setStepper(int stepId) {
        switch (stepId) {
            case STEP_PREPARE:
                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.background_circle);


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.background_circle);

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.background_circle);

                btnNext.setEnabled(false);
                btnBack.setEnabled(false);

                break;
            case STEP_GET_IMAGE:
                tvStep1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep1Label.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.background_circle_white);


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.background_circle);

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.background_circle);


                break;
            case STEP_RECOGNIZE:

                tvStep2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep2Label.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.background_circle_white);


                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.background_circle);

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.background_circle);

                recognizeFragment.setBitmap(
                        getImageFragment.bitmap
                );
                recognizeFragment.recognize();

                break;
            case STEP_FINISH:
                tvStep3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep3Label.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.background_circle_white);


                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.background_circle);

                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.background_circle);

                finishFragment.processTKB(recognizeFragment.getListMaHp());
                break;
        }
        viewPager.setCurrentItem(stepId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                if (manuallyMode) {
                    if (currentStep == STEP_FINISH) {
                        currentStep = STEP_GET_IMAGE;
                        setStepper(currentStep);
                        break;
                    }
                }
                else if (currentStep > STEP_GET_IMAGE) {
                    currentStep--;
                    setStepper(currentStep);
                }
                break;
            case R.id.btnNext:
                if(currentStep < STEP_FINISH){
                    currentStep++;
                    setStepper(currentStep);
                }
                break;
            case R.id.btnFinish:
                if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
                    finishFragment.setOnUpLoadUserDBComplete(new FinishFragment.OnUpLoadUserDBComplete() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(SetupActivity.this, "Có lỗi khi upload dữ liệu!", Toast.LENGTH_LONG).show();
                        }
                    });

                    finishFragment.writelstMaHPtoUserDB(dbUserMaHocPhan);
                }
                else
                    Toast.makeText(SetupActivity.this, "Không có internet, kiểm tra lại kết nôi mạng", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GetImageFragment.RQ_PER_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        getImageFragment.reallyOpenCamera();
                    } catch (AppException e) {
                        e.printStackTrace();
                        Utils.getsInstance(getApplicationContext()).showErrorMessage(this, e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.getsInstance(getApplicationContext()).showErrorMessage(this, e.getMessage());
                    }
                }

                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dang_xuat:
                FirebaseAuth.getInstance().signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
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

    private void changeFabState(int position) {
        Log.d(TAG, "changeFabState: position = " + position);
        if (position == STEP_RECOGNIZE) {
//            mFab.setImageResource(R.drawable.ic_add_24dp);
//            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
//            mFab.setOnClickListener(v -> {
//                Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
//                intent.putExtra(C.MULTI_CHECK, true);
//                CircularAnimUtil.startActivity(MainActivity.this, intent, mFab, R.color.colorPrimary);
//            });

            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recognizeFragment.showAddLopHPDialog();
                    //Toast.makeText(SetupActivity.this, "Fab add clicked on recognize!", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (position == STEP_FINISH){
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishFragment.showAddLopHPDialog();
                    //Toast.makeText(SetupActivity.this, "Fab add clicked on finish!", Toast.LENGTH_SHORT).show();
                }
            });
//            mFab.setImageResource(R.drawable.ic_favorite);
//            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
//            mFab.setOnClickListener(v -> showShareDialog());
        }
    }

    public CoordinatorLayout get_layout_setup() {
        return this.layout_setup;
    }

    private class FabVisibilityChangedListener extends FloatingActionButton.OnVisibilityChangedListener {

        private int position;

        @Override
        public void onHidden(FloatingActionButton fab) {
            changeFabState(position);
            fab.show();
        }
    }

}
