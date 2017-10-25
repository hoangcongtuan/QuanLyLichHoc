package com.example.hoangcongtuan.quanlylichhoc.activity.login;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.activity.setup.SetupActivity;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

/**
 * Man hinh dang nhap
 */


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private final static String TAG = LoginActivity.class.getName();
    private final static int RC_SIGN_IN = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference firebaseDB;
    private DatabaseReference firebaseDBUserMaHP;
    private DatabaseReference firebaseUserToken;

    private Button btnLoginFb;
    private Button btnLoginGg;
    private ProgressBar progressBarLogin;
    private CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();
    }

    private void init() {
        //init
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //init google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.d(TAG, "onSuccess: ");
                startAuthWithFirebase();
                handleFbLoginResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                //Log.d(TAG, "onError: ");
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Đăng nhập Facebook thất bại!!", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        });
    }

    private void getWidgets() {
        //getwidgets
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarLogin = (ProgressBar)findViewById(R.id.progresBar_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnLoginFb = (Button)findViewById(R.id.btnLoginFb);
        btnLoginGg = (Button)findViewById(R.id.btnLoginGg);

    }

    private void setWidgets() {
        //setwidgets
        setSupportActionBar(toolbar);
        progressBarLogin.setVisibility(View.INVISIBLE);

    }

    private void setWidgetsEvent() {
        //setwidgetsEvent
        btnLoginGg.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public void handleGgSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        //Log.d(TAG, "handleGgSignInResult: " + result.getStatus().getStatusMessage());
        if (result.isSuccess()) {
            GoogleSignInAccount signInAccount = result.getSignInAccount();
            startAuthWithFirebase();
            firebaseAuthWithGoogle(signInAccount);
        }
        else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Đăng nhập  thất bại!!", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            finishAuthWithFirebase();
        }

    }

    public void handleFbLoginResult(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "onComplete: Success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            handleFirebaseLoginSuccess(firebaseUser);
                        }
                        else {
                            //Log.d(TAG, "onComplete: Failed");
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Thất bại, có vẻ email của bạn đã được sử dụng!!", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            finishAuthWithFirebase();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGgSignInResult(googleSignInResult);
                break;
        }
    }

    public void startAuthWithFirebase() {
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    public void finishAuthWithFirebase() {
        progressBarLogin.setVisibility(View.INVISIBLE);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Log.d(TAG, "onComplete: Success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            //Log.d(TAG, "onComplete: User UId = " + user.getUid());
                            handleFirebaseLoginSuccess(firebaseUser);
                        }
                        else {
                            //Log.d(TAG, "onComplete: failure");
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Đăng nhập Google thất bại!!", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();

                        }
                    }
                });
    }

    public void handleFirebaseLoginSuccess(FirebaseUser user) {
        //get local user db
        final Cursor curAllMaHP = DBLopHPHelper.getsInstance().getAllLopHocPhan();
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        firebaseDBUserMaHP = firebaseDB.child("userInfo").child(firebaseUser.getUid()).child("listMaHocPHan");
        firebaseUserToken = firebaseDB.child("userInfo").child(firebaseUser.getUid()).child("FCMToken");
        firebaseDBUserMaHP.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //da thiet lap lop hoc phan
                    if (dataSnapshot.getChildrenCount() != 0) {
                        //co du lieu trong do
                        //save Firebase DB to local DB
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DBLopHPHelper.getsInstance().insertUserMaHocPhan((String)snapshot.getValue());
                        }
                        //kiem tra du lieu ve tat ca cac lop hoc phan
                        DBLopHPHelper.getsInstance().setOnCheckDB(new DBLopHPHelper.OnCheckDB() {
                            @Override
                            public void onDBAvailable() {
                                finishAuthWithFirebase();
                                //subscrible topics
                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        DBLopHPHelper.getsInstance().getListUserMaHP()
                                );

                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic("TBChung");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onDownloadFinish() {
                                finishAuthWithFirebase();
                                //subscrible topics
                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic(
                                        DBLopHPHelper.getsInstance().getListUserMaHP()
                                );

                                Utils.QLLHUtils.getsInstance(LoginActivity.this).subscribeTopic("TBChung");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onStartDownload() {

                            }
                        });

                        DBLopHPHelper.getsInstance().checkDB();

                    }
                    else {
                        //di toi man hinh setup
                        //xoa du lieu cu trong may
                        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                        finishAuthWithFirebase();
                        Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    //di toi man hinh setup
                    //xoa du lieu cu trong may
                    DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                    finishAuthWithFirebase();
                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        if (DBLopHPHelper.getsInstance().isUserLocalDBAvailable()) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        else {
//            Intent intent = new Intent(this, SetupActivity.class);
//            startActivity(intent);
//            finish();
//        }

        //write token to firebase user
        firebaseUserToken.setValue(FirebaseInstanceId.getInstance().getToken());
        Log.d(TAG, "handleFirebaseLoginSuccess: Token = " + FirebaseInstanceId.getInstance().getToken());

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        //Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }


    @Override
    public void onClick(View view) {
        //Log.d(TAG, "onClick: ");
        switch (view.getId()) {
            case R.id.btnLoginGg:
                googleSignIn();
                //Log.d(TAG, "onClick: ");
                break;
            case R.id.btnLoginFb:
                facebookSignIn();
                break;
        }
    }
}
