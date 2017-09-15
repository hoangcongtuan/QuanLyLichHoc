package com.example.hoangcongtuan.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private final static String TAG = LoginActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private final static int RC_SIGN_IN = 1;
    private Button btnLoginFb;
    private Button btnLoginGg;
    private LoginButton fbLoginBtn;
    private CallbackManager callbackManager;
    private TextView tvLoginUser;
    private ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvLoginUser = (TextView)findViewById(R.id.tvLoginUser);
        progressBarLogin = (ProgressBar)findViewById(R.id.progresBar_login);

        tvLoginUser.setVisibility(View.INVISIBLE);
        progressBarLogin.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        //configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnLoginFb = (Button)findViewById(R.id.btnLoginFb);
        btnLoginGg = (Button)findViewById(R.id.btnLoginGg);
        btnLoginFb.isInEditMode();
        btnLoginGg.isInEditMode();
        
        btnLoginGg.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);

        //config Facebook Sign In

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: ");
                startAuthWithFirebase(Profile.getCurrentProfile().getName());
                handleFbLoginResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: ");
            }
        });

    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleFbLoginResult(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            handleFirebaseLoginSuccess(user);
                        }
                        else {
                            Log.d(TAG, "onComplete: Failed");
                            Toast.makeText(LoginActivity.this, "Auth Failure", Toast.LENGTH_LONG).show();
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
                handleSignInResult(googleSignInResult);
                break;
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount signInAccount = result.getSignInAccount();
            startAuthWithFirebase(signInAccount.getDisplayName());
            firebaseAuthWithGoogle(signInAccount);
        }
        else {
            finishAuthWithFirebase();
        }

    }

    public void startAuthWithFirebase(String userName) {
        progressBarLogin.setVisibility(View.VISIBLE);
        tvLoginUser.setVisibility(View.VISIBLE);
        tvLoginUser.setText("Đang đăng nhập với " + userName);
    }

    public void finishAuthWithFirebase() {
        progressBarLogin.setVisibility(View.INVISIBLE);
        tvLoginUser.setVisibility(View.INVISIBLE);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: User UId = " + user.getUid());
                            handleFirebaseLoginSuccess(user);
                        }
                        else {
                            Log.d(TAG, "onComplete: failure");
                            Toast.makeText(LoginActivity.this, "Auth Failure", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void handleFirebaseLoginSuccess(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        finishAuthWithFirebase();
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onStart: " + currentUser.getDisplayName());
            Log.d(TAG, "onStart: " + currentUser.getUid());
        }
        else {
            Log.d(TAG, "onStart: no Account Available, be Sign In now");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        //FirebaseAuth.getInstance().signOut();
        //Log.d(TAG, "onStop: Sign out");
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        switch (view.getId()) {
            case R.id.btnLoginGg:
                googleSignIn();
                Log.d(TAG, "onClick: ");
                break;
            case R.id.btnLoginFb:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
                break;
        }
    }
}
