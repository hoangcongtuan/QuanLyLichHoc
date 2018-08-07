package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.content.DialogInterface;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.hoangcongtuan.quanlylichhoc.activity.base.BaseActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.login.LoginActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.StepPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.NoSwipeCustomViewPager;
import com.example.hoangcongtuan.quanlylichhoc.customview.ProgressDialogBuilderCustom;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
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

import java.io.IOException;

public class SetupActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, RecognizeFragment.OnRecognize, GetImageFragment.GetImageFragCallBack,
        FinishFragment.FinishFragCallBack{
    private final static String TAG = SetupActivity.class.getName();
    private NoSwipeCustomViewPager viewPager;
    private Button btnBack;
    private Button btnNext;
    private Button btnFinish;
    private FloatingActionButton fabAdd;
    private CoordinatorLayout layout_setup;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private StepPagerAdapter stepperPagerAdapter;
    private TextView tvStep1, tvStep2, tvStep3, tvStep1Label, tvStep2Label, tvStep3Label;
    private int currentStep;
    private GetImageFragment getImageFragment;
    private RecognizeFragment recognizeFragment;
    private FinishFragment finishFragment;
    private AlertDialog pr_dialog;

    private DatabaseReference dbUserMaHocPhan;

    private Uri avatarUrl;
    private String userName;

    private TextView tvUserName;
    private ImageView imgAvatar;

    private GoogleApiClient mGoogleApiClient;

    private boolean manuallyMode = false;
    private boolean isImageAvailable = false;

    private final static int STEP_GET_IMAGE = 0;
    private final static int STEP_RECOGNIZE = 1;
    private final static int STEP_FINISH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.provide_class_id_method_tip);
        builder.setPositiveButton(R.string.let_try, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                manuallyMode = false;
            }
        });

        builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                manuallyMode = true;
                currentStep = STEP_FINISH;
                setStepper(currentStep);
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    private void init() {
        //init
        stepperPagerAdapter = new StepPagerAdapter(getSupportFragmentManager());
        getImageFragment = new GetImageFragment();
        recognizeFragment = new RecognizeFragment();
        finishFragment = new FinishFragment();

        //create progress dialog
        ProgressDialogBuilderCustom progressDialogBuilderCustom = new ProgressDialogBuilderCustom(this);
        progressDialogBuilderCustom.setText(R.string.processing);

        pr_dialog = progressDialogBuilderCustom.create();

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
        currentStep = STEP_GET_IMAGE;

        //get firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        avatarUrl = firebaseUser.getPhotoUrl();
        userName = firebaseUser.getDisplayName();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        dbUserMaHocPhan = database.child(LoginActivity.KEY_FIRBASE_USER).child(firebaseUser.getUid()).child(LoginActivity.KEY_FIREBASE_LIST_MAHP);
    }

    private void getWidgets() {
        //getWidgets
        viewPager = findViewById(R.id.viewPagerSetup);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);
        toolbar = findViewById(R.id.toolbar);
        fabAdd = findViewById(R.id.fabAdd);
        drawerLayout = findViewById(R.id.drawer_setup);
        navigationView = findViewById(R.id.setup_navigation);
        layout_setup = findViewById(R.id.layout_setup);

        tvStep1 = findViewById(R.id.tvStep1);
        tvStep2 = findViewById(R.id.tvStep2);
        tvStep3 = findViewById(R.id.tvStep3);

        tvStep1Label = findViewById(R.id.tvStep1Label);
        tvStep2Label = findViewById(R.id.tvStep2Label);
        tvStep3Label = findViewById(R.id.tvStep3Label);

        tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
    }

    private void setWidgets() {
        //setWidgets
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        stepperPagerAdapter.addFragment(getImageFragment, "welcome");
        stepperPagerAdapter.addFragment(recognizeFragment, "Recognize");
        stepperPagerAdapter.addFragment(finishFragment, "finish");

        viewPager.setAdapter(stepperPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        tvUserName.setText(userName);
        setStepper(currentStep);
    }

    private void setWidgetsEvent() {
        //setWidgetsEvent
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

    public void setStepper(int stepId) {
        viewPager.setCurrentItem(stepId);
        switch (stepId) {
            case STEP_GET_IMAGE:
                tvStep1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep1Label.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.shape_circle_white);

                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.shape_circle);

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.shape_circle);

                btnBack.setVisibility(View.INVISIBLE);
                btnFinish.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                if (!isImageAvailable)
                    btnNext.setEnabled(false);
                else
                    btnNext.setEnabled(true);
                break;

            case STEP_RECOGNIZE:
                tvStep2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep2Label.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.shape_circle_white);

                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.shape_circle);

                tvStep3Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep3.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.shape_circle);

//                TODO: Need more smart solution here
//                recognizeFragment.setBitmap(
//                        getImageFragment.bitmap
//                );
//                recognizeFragment.recognize();

                btnBack.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setEnabled(true);
                btnBack.setEnabled(true);
                btnFinish.setVisibility(View.INVISIBLE);
                break;

            case STEP_FINISH:
                tvStep3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStepperText));
                tvStep3Label.setTextColor(Color.WHITE);
                tvStep3.setBackgroundResource(R.drawable.shape_circle_white);

                tvStep2Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep2.setTextColor(Color.WHITE);
                tvStep2.setBackgroundResource(R.drawable.shape_circle);

                tvStep1Label.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDisable));
                tvStep1.setTextColor(Color.WHITE);
                tvStep1.setBackgroundResource(R.drawable.shape_circle);

                finishFragment.setListClass(recognizeFragment.getListMaHp());

                if (manuallyMode) {
                    btnBack.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnBack.setVisibility(View.VISIBLE);
                    btnBack.setEnabled(true);
                    btnNext.setVisibility(View.INVISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                }
                break;
        }

        //Change Fab Add button behavior base on step
        FabVisibilityChangedListener fabVisibilityChangedListener = new FabVisibilityChangedListener();
        if (currentStep == STEP_GET_IMAGE)
            fabAdd.hide();
        else {
            if (fabAdd.isShown()) {
                fabVisibilityChangedListener.position = currentStep;
                fabAdd.hide(fabVisibilityChangedListener);
            } else {
                changeFabState(currentStep);
                fabAdd.show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                if (currentStep > STEP_GET_IMAGE) {
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
                            pr_dialog.dismiss();
                            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailed() {
                            pr_dialog.dismiss();
                            Toast.makeText(SetupActivity.this, "Có lỗi khi upload dữ liệu!", Toast.LENGTH_LONG).show();
                        }
                    });

                    pr_dialog.show();
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
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recognizeFragment.showAddLopHPDialog();
                }
            });
        } else if (position == STEP_FINISH){
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishFragment.showAddLopHPDialog();
                }
            });
        }
    }

    public CoordinatorLayout get_layout_setup() {
        return this.layout_setup;
    }

    @Override
    public void startRecognize() {
        Log.d(TAG, "startRecognize: ");
        pr_dialog.show();
    }

    @Override
    public void endRecognize() {
        Log.d(TAG, "endRecognize: ");
        pr_dialog.dismiss();
    }

    @Override
    public void onBitmapAvailable(Bitmap bitmap) {
        btnNext.setEnabled(true);
        btnNext.setVisibility(View.VISIBLE);
        isImageAvailable = true;
        recognizeFragment.setBitmap(bitmap);
    }

    @Override
    public void onListClassChangeState(boolean isEmpty) {
        if (isEmpty)
            btnFinish.setEnabled(false);
        else
            btnFinish.setEnabled(true);
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
